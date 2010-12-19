/* EmpegDatabaseRebuilder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPException;
import com.inzyme.exception.ExceptionUtils;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.TextProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.util.ApplicationUtils;
import com.inzyme.util.Debug;

import org.jempeg.JEmplodeProperties;
import org.jempeg.empeg.util.HijackUtils;
import org.jempeg.empeg.util.RemoteFid;
import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDConstants;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.SocketConnection;

public class EmpegDatabaseRebuilder
{
    public static void main(String[] _args) {
	try {
	    PropertiesManager.initializeInstance
		("jEmplode Properties",
		 new File(ApplicationUtils.getSettingsFolder(), "jempegrc"));
	    JEmplodeProperties.initializeDefaults();
	    if (_args.length < 1) {
		System.err.println("usage: RebuildDatabase <ipaddress>");
		System.exit(0);
	    }
	    InetAddress empegAddress = InetAddress.getByName(_args[0]);
	    EmpegProtocolClient protocolClient
		= new EmpegProtocolClient(new SocketConnection(empegAddress,
							       8300),
					  new TextProgressListener());
	    protocolClient.open();
	    try {
		rebuildDatabase(protocolClient, new TextProgressListener());
	    } catch (Object object) {
		protocolClient.close();
		throw object;
	    }
	    protocolClient.close();
	} catch (Throwable t) {
	    ExceptionUtils.printChainedStackTrace(t);
	}
    }
    
    public static void rebuildDatabaseFromMemory
	(PlayerDatabase _playerDatabase, EmpegProtocolClient _protocolClient,
	 IProgressListener _progressListener)
	throws FTPException, IOException, ProtocolException {
	PlayerVersionInfo versionInfo = _protocolClient.getVersionInfo();
	String dbEncoding;
	String dbName;
	if (versionInfo.getVersion().startsWith("3.")) {
	    dbEncoding = "ISO-8859-1";
	    dbName = "database3";
	} else {
	    dbEncoding = "ISO-8859-1";
	    dbName = "database";
	}
	System.out.println("EmpegDatabaseRebuilder.rebuildDatabaseFromMemory: "
			   + dbName);
	DatabaseTags databaseTags = _playerDatabase.getDatabaseTags();
	ByteArrayOutputStream staticDatabaseBAOS = new ByteArrayOutputStream();
	LittleEndianOutputStream staticDatabaseLEOS
	    = new LittleEndianOutputStream(staticDatabaseBAOS);
	ByteArrayOutputStream playlistDatabaseBAOS
	    = new ByteArrayOutputStream();
	LittleEndianOutputStream playlistDatabaseLEOS
	    = new LittleEndianOutputStream(playlistDatabaseBAOS);
	long maxFid = _playerDatabase.getNodeMap().getMax();
	staticDatabaseLEOS.writeUnsigned8(0);
	staticDatabaseLEOS.writeUnsigned8(7);
	staticDatabaseLEOS.write("illegal", "ISO-8859-1");
	staticDatabaseLEOS.writeSigned8(-1);
	int lastIndex = 0;
	for (long fid = 256L; fid <= maxFid; fid++) {
	    IFIDNode node = _playerDatabase.getNode(fid);
	    if (node != null && !node.isTransient()) {
		int index = FIDConstants.getFIDNumber((int) fid);
		for (int curIndex = lastIndex + 1; curIndex < index;
		     curIndex++)
		    staticDatabaseLEOS.writeSigned8(-1);
		lastIndex = index;
		if (node instanceof FIDPlaylist) {
		    FIDPlaylist playlist = (FIDPlaylist) node;
		    int playlistSize = playlist.getSize();
		    for (int j = 0; j < playlistSize; j++)
			playlistDatabaseLEOS
			    .writeUnsigned32(playlist.getNodeAt(j).getFID());
		}
		NodeTags tags = (NodeTags) node.getTags().clone();
		DynamicData.removeDynamicData(tags);
		Enumeration tagNamesEnum = tags.getTagNames();
		while (tagNamesEnum.hasMoreElements()) {
		    String tagName = (String) tagNamesEnum.nextElement();
		    String tagValue = tags.getValue(tagName);
		    int type = databaseTags.getNumber(tagName);
		    int length = tagValue.length();
		    staticDatabaseLEOS.writeUnsigned8(type);
		    staticDatabaseLEOS.writeUnsigned8(length);
		    if (length > 0)
			staticDatabaseLEOS.write(tagValue, dbEncoding);
		}
		staticDatabaseLEOS.writeSigned8(-1);
	    }
	}
	ByteArrayOutputStream tagIndexBAOS = new ByteArrayOutputStream();
	PrintStream tagIndexPS = new PrintStream(tagIndexBAOS);
	for (int tagNum = 0; tagNum < 256; tagNum++) {
	    String tagName = databaseTags.getName(tagNum);
	    if (tagName != null)
		tagIndexPS.print(tagName);
	    tagIndexPS.print('\n');
	}
	tagIndexPS.flush();
	tagIndexPS.close();
	HijackUtils.upload(HijackUtils
			       .getAddress(_protocolClient.getConnection()),
			   "/empeg/var/tags",
			   new ByteArrayInputStream(tagIndexBAOS
							.toByteArray()),
			   0, tagIndexBAOS.size(), null);
	staticDatabaseLEOS.flush();
	staticDatabaseLEOS.close();
	HijackUtils.upload(HijackUtils
			       .getAddress(_protocolClient.getConnection()),
			   "/empeg/var/" + dbName,
			   new ByteArrayInputStream(staticDatabaseBAOS
							.toByteArray()),
			   0, staticDatabaseBAOS.size(), null);
	playlistDatabaseLEOS.flush();
	playlistDatabaseLEOS.close();
	HijackUtils.upload(HijackUtils
			       .getAddress(_protocolClient.getConnection()),
			   "/empeg/var/playlists",
			   new ByteArrayInputStream(playlistDatabaseBAOS
							.toByteArray()),
			   0, playlistDatabaseBAOS.size(), null);
    }
    
    public static void rebuildDatabase
	(EmpegProtocolClient _protocolClient,
	 IProgressListener _progressListener)
	throws FTPException, IOException, ProtocolException {
	InetAddress empegAddress
	    = HijackUtils.getAddress(_protocolClient.getConnection());
	Pinger pinger = new Pinger(_protocolClient);
	FTPClient ftp = new FTPClient(empegAddress);
	HijackUtils.login(ftp);
	ftp.site("rw");
	try {
	    DatabaseTags databaseTags = new DatabaseTags();
	    boolean isNewFidLayout = HijackUtils.isNewFidLayout(empegAddress);
	    int maxFid = HijackUtils.getMaxFid(ftp);
	    int maxIndex = FIDConstants.getFIDNumber(maxFid) + 1;
	    ByteArrayOutputStream tagIndexBAOS = new ByteArrayOutputStream();
	    PrintStream tagIndexPS = new PrintStream(tagIndexBAOS);
	    ByteArrayOutputStream staticDatabaseBAOS
		= new ByteArrayOutputStream();
	    LittleEndianOutputStream staticDatabaseLEOS
		= new LittleEndianOutputStream(staticDatabaseBAOS);
	    ByteArrayOutputStream playlistDatabaseBAOS
		= new ByteArrayOutputStream();
	    LittleEndianOutputStream playlistDatabaseLEOS
		= new LittleEndianOutputStream(playlistDatabaseBAOS);
	    staticDatabaseLEOS.writeUnsigned8(0);
	    staticDatabaseLEOS.writeUnsigned8(7);
	    staticDatabaseLEOS.write("illegal", "ISO-8859-1");
	    staticDatabaseLEOS.writeSigned8(-1);
	    int lastIndex = 0;
	    for (int index = FIDConstants.getFIDNumber(256); index <= maxIndex;
		 index++) {
		pinger.pingIfNecessary();
		long fid = FIDConstants.makeFID(index, 0);
		Debug.println(4, ("RebuildDatabase.main: FID " + fid + " / "
				  + maxIndex));
		ByteArrayOutputStream oneFileOS = new ByteArrayOutputStream();
		int actualDriveNum = -1;
		RemoteFid remoteFid = null;
		for (int attemptDriveNum = 0;
		     actualDriveNum == -1 && attemptDriveNum <= 1;
		     attemptDriveNum++) {
		    try {
			remoteFid = new RemoteFid(attemptDriveNum, fid | 0x1L,
						  isNewFidLayout);
			HijackUtils.download(ftp, remoteFid.getFullPath(),
					     oneFileOS, 2147483647, null);
			actualDriveNum = attemptDriveNum;
		    } catch (FTPException ftpexception) {
			/* empty */
		    }
		}
		if (actualDriveNum != -1) {
		    _progressListener.operationStarted(remoteFid.toString());
		    _progressListener.taskUpdated(fid, (long) maxFid);
		    Properties tags = new Properties();
		    String propsStr
			= new String(oneFileOS.toByteArray(), "UTF-8");
		    StringTokenizer lineTokenizer
			= new StringTokenizer(propsStr, "\n");
		    while (lineTokenizer.hasMoreTokens()) {
			String line = lineTokenizer.nextToken();
			int equalsIndex = line.indexOf('=');
			if (equalsIndex != -1) {
			    String tagName = line.substring(0, equalsIndex);
			    String tagValue = line.substring(equalsIndex + 1);
			    tags.put(tagName, tagValue);
			} else
			    System.out.println
				("EmpegDatabaseRebuilder.rebuildDatabase: NO EQUALS: "
				 + line + " of " + propsStr + ", "
				 + remoteFid);
		    }
		    if ("playlist".equals(tags.getProperty("type"))) {
			RemoteFid playlistData
			    = new RemoteFid(actualDriveNum, fid,
					    isNewFidLayout);
			ByteArrayOutputStream playlistBAOS
			    = new ByteArrayOutputStream();
			try {
			    HijackUtils.download(ftp,
						 playlistData.getFullPath(),
						 playlistBAOS, 2147483647,
						 null);
			} catch (FTPException e) {
			    _progressListener.operationStarted
				("  playlist was deleted improperly ... ignoring.");
			    continue;
			}
			playlistBAOS.flush();
			playlistBAOS.close();
			int playlistLength = 0;
			byte[] playlistBytes = playlistBAOS.toByteArray();
			if (playlistBytes.length > 0) {
			    LittleEndianInputStream leis
				= (new LittleEndianInputStream
				   (new ByteArrayInputStream(playlistBytes)));
			    while (leis.available() > 0) {
				long childFid = leis.readUnsigned32();
				if (childFid != 0L) {
				    playlistDatabaseLEOS
					.writeUnsigned32(childFid);
				    playlistLength++;
				}
			    }
			}
			tags.setProperty("length",
					 String.valueOf(playlistLength * 4));
			_progressListener.operationStarted
			    ("  playlist = adjusted to " + playlistLength
			     + " entries");
		    }
		    for (int curIndex = lastIndex + 1; curIndex < index;
			 curIndex++)
			staticDatabaseLEOS.writeSigned8(-1);
		    lastIndex = index;
		    Enumeration tagNamesEnum = tags.keys();
		    while (tagNamesEnum.hasMoreElements()) {
			String tagName = (String) tagNamesEnum.nextElement();
			String tagValue = tags.getProperty(tagName);
			int type = databaseTags.getNumber(tagName);
			int length = tagValue.length();
			staticDatabaseLEOS.writeUnsigned8(type);
			staticDatabaseLEOS.writeUnsigned8(length);
			if (length > 0)
			    staticDatabaseLEOS.write(tagValue, "ISO-8859-1");
		    }
		    staticDatabaseLEOS.writeSigned8(-1);
		}
	    }
	    for (int tagNum = 0; tagNum < 256; tagNum++) {
		String tagName = databaseTags.getName(tagNum);
		if (tagName != null)
		    tagIndexPS.print(tagName);
		tagIndexPS.print('\n');
	    }
	    tagIndexPS.flush();
	    tagIndexPS.close();
	    _progressListener.operationStarted("Writing tags ...");
	    HijackUtils.upload(empegAddress, "/empeg/var/tags",
			       new ByteArrayInputStream(tagIndexBAOS
							    .toByteArray()),
			       0, tagIndexBAOS.size(), null);
	    staticDatabaseLEOS.flush();
	    staticDatabaseLEOS.close();
	    _progressListener.operationStarted("Writing database3 ...");
	    HijackUtils.upload(empegAddress, "/empeg/var/database3",
			       new ByteArrayInputStream(staticDatabaseBAOS
							    .toByteArray()),
			       0, staticDatabaseBAOS.size(), null);
	    playlistDatabaseLEOS.flush();
	    playlistDatabaseLEOS.close();
	    _progressListener.operationStarted("Writing playlists ...");
	    HijackUtils.upload(empegAddress, "/empeg/var/playlists",
			       new ByteArrayInputStream(playlistDatabaseBAOS
							    .toByteArray()),
			       0, playlistDatabaseBAOS.size(), null);
	} catch (Object object) {
	    ftp.site("ro");
	    throw object;
	}
	ftp.site("ro");
    }
}
