/* HijackUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.io.StreamUtils;
import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

import org.jempeg.empeg.versiontracker.HijackVersionTracker;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.SocketConnection;
import org.jempeg.protocol.SocketConnectionFactory;

public class HijackUtils
{
    public static Reason shouldUseHijack(IConnection _conn) {
	Reason reason = null;
	if (!(_conn instanceof SocketConnection))
	    reason
		= (new Reason
		   ("jEmplode must be connected to your Empeg with Ethernet to use Hijack."));
	else if (!PropertiesManager.getInstance()
		      .getBooleanProperty("jempeg.useHijack"))
	    reason
		= (new Reason
		   ("You must have 'Use Hijack with Possible' enabled under Tools=>Options... to use Hijack."));
	else {
	    SocketConnection conn = (SocketConnection) _conn;
	    HijackVersionTracker versionTracker
		= (new HijackVersionTracker
		   (new SocketConnectionFactory(conn.getAddress(),
						conn.getPort())));
	    try {
		if (!versionTracker.isInstalled())
		    reason = versionTracker.getFailureReason("use Hijack");
	    } catch (Throwable t) {
		Debug.println
		    (8,
		     "You wanted to use Hijack, but I can't find it on this connection.");
		reason = versionTracker.getFailureReason("use Hijack");
	    }
	}
	return reason;
    }
    
    public static InetAddress getAddress(IConnection _conn) {
	SocketConnection socketConn = (SocketConnection) _conn;
	InetAddress inetAddress = socketConn.getAddress();
	return inetAddress;
    }
    
    public static void upload(InetAddress _address, String _remoteFilename,
			      SeekableInputStream _uploadStream, int _offset,
			      IProgressListener _listener) throws IOException {
	upload(_address, _remoteFilename, _uploadStream, _offset,
	       (int) _uploadStream.length(), _listener);
    }
    
    public static void upload(InetAddress _address, String _remoteFilename,
			      InputStream _uploadStream, int _offset,
			      int _length,
			      IProgressListener _listener) throws IOException {
	FTPClient ftp = new FTPClient(_address);
	try {
	    login(ftp);
	    OutputStream os = ftp.putBinary(_remoteFilename, _offset);
	    StreamUtils.copy(_uploadStream, os, 16384, (long) _length,
			     _listener);
	    os.flush();
	    os.close();
	} catch (Object object) {
	    ftp.quit();
	    throw object;
	}
	ftp.quit();
    }
    
    public static void upload(FTPClient _ftpClient, String _remoteFilename,
			      InputStream _uploadStream, int _offset,
			      int _length,
			      IProgressListener _listener) throws IOException {
	OutputStream os = _ftpClient.putBinary(_remoteFilename, _offset);
	StreamUtils.copy(_uploadStream, os, 16384, (long) _length, _listener);
	os.flush();
	os.close();
    }
    
    public static void download
	(InetAddress _address, String _remoteFilename,
	 OutputStream _outputStream, IProgressListener _listener)
	throws IOException {
	FTPClient ftp = new FTPClient(_address);
	try {
	    login(ftp);
	    InputStream is = ftp.getBinary(_remoteFilename);
	    try {
		int size;
		if (_remoteFilename.startsWith("/proc"))
		    size = 2147483647;
		else
		    size = getSize(ftp, _remoteFilename);
		StreamUtils.copy(is, _outputStream, 16384, (long) size,
				 _listener);
	    } catch (Object object) {
		is.close();
		throw object;
	    }
	    is.close();
	    ftp.readGetResponse();
	} catch (Object object) {
	    ftp.quit();
	    throw object;
	}
	ftp.quit();
    }
    
    public static void download
	(FTPClient _ftpClient, String _remoteFilename,
	 OutputStream _outputStream, int _size, IProgressListener _listener)
	throws IOException {
	InputStream is = _ftpClient.getBinary(_remoteFilename);
	try {
	    StreamUtils.copy(is, _outputStream, 16384, (long) _size,
			     _listener);
	} catch (Object object) {
	    is.close();
	    throw object;
	}
	is.close();
	_ftpClient.readGetResponse();
    }
    
    public static void deleteFid(InetAddress _address, long _fid)
	throws IOException {
	FTPClient ftp = new FTPClient(_address);
	try {
	    login(ftp);
	    RemoteFid fid = findFID(ftp, _fid);
	    if (fid != null)
		ftp.quote("DELE " + fid.getFullPath(), null);
	} catch (Object object) {
	    ftp.quit();
	    throw object;
	}
	ftp.quit();
    }
    
    public static void uploadFid
	(InetAddress _address, long _fid, InputStream _is, long _initialOffset,
	 long _size, ISimpleProgressListener _listener, boolean _newFidLayout,
	 int _drive)
	throws IOException {
	FTPClient ftp = new FTPClient(_address);
	try {
	    login(ftp);
	    RemoteFid remoteFid = findFID(ftp, _fid);
	    if (remoteFid == null)
		remoteFid = new RemoteFid(_drive, _fid, _newFidLayout);
	    OutputStream os = null;
	    try {
		os = ftp.putBinary(remoteFid.getFullPath(),
				   (int) _initialOffset);
	    } catch (FTPException e) {
		ftp.mkdir(remoteFid.getPath());
		os = ftp.putBinary(remoteFid.getFullPath(),
				   (int) _initialOffset);
	    }
	    StreamUtils.copy(_is, os, 16384, _size, 0L, _size, _listener);
	    os.flush();
	    os.close();
	} catch (Object object) {
	    ftp.quit();
	    throw object;
	}
	ftp.quit();
    }
    
    private static boolean isExistingFile(FTPClient _ftp,
					  RemoteFid _remoteFid) {
	try {
	    String[] validCode = { "553" };
	    _ftp.quote("NLST " + _remoteFid.getFullPath(), validCode);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }
    
    private static RemoteFid findFID(FTPClient _ftp, long _fid) {
	RemoteFid foundFid = null;
	for (int driveNum = 0; foundFid == null && driveNum <= 1; driveNum++) {
	    RemoteFid fid = new RemoteFid(driveNum, _fid, false);
	    if (isExistingFile(_ftp, fid))
		foundFid = fid;
	    else {
		fid = new RemoteFid(driveNum, _fid, true);
		if (isExistingFile(_ftp, fid))
		    foundFid = fid;
	    }
	}
	return foundFid;
    }
    
    public static String getString
	(InetAddress _address, String _remoteFilename) throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	download(_address, _remoteFilename, baos, null);
	String str = baos.toString();
	return str;
    }
    
    public static String getString
	(FTPClient _ftpClient, String _remoteFilename) throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	download(_ftpClient, _remoteFilename, baos, 2147483647, null);
	String str = baos.toString();
	return str;
    }
    
    public static boolean isNewFidLayout(InetAddress _address)
	throws IOException {
	object = object_1_;
	break;
    }
    
    public static int getMaxFid(FTPClient _ftp) throws IOException {
	int maxFid = 0;
	for (int driveNum = 0; driveNum <= 1; driveNum++) {
	    try {
		maxFid = getMaxFid(_ftp, "/empeg/fids" + driveNum, maxFid);
	    } catch (FTPException ftpexception) {
		/* empty */
	    }
	}
	return maxFid;
    }
    
    private static int getMaxFid
	(FTPClient _ftp, String _path, int _currentMax) throws IOException {
	int maxFid = _currentMax;
	Enumeration filesEnum = getFiles(_ftp, _path).elements();
	while (filesEnum.hasMoreElements()) {
	    FtpFile ftpFile = (FtpFile) filesEnum.nextElement();
	    if (ftpFile.getName().indexOf('_') == -1) {
		RemoteFid remoteFid = new RemoteFid(ftpFile.getFullPath());
		maxFid = (int) Math.max((long) maxFid, remoteFid.getFid());
	    } else
		maxFid = getMaxFid(_ftp, ftpFile.getFullPath(), maxFid);
	}
	return maxFid;
    }
    
    protected static int getSize(FTPClient _ftp, String _filename)
	throws IOException {
	int size = -1;
	int slashIndex = _filename.lastIndexOf('/');
	String path = _filename.substring(0, slashIndex);
	String name = _filename.substring(slashIndex + 1);
	Enumeration filesEnum = getFiles(_ftp, path).elements();
	while (size == -1 && filesEnum.hasMoreElements()) {
	    FtpFile file = (FtpFile) filesEnum.nextElement();
	    if (name.equals(file.getName()))
		size = file.getSize();
	}
	return size;
    }
    
    public static Vector getFiles(InetAddress _address, String _folder)
	throws IOException {
	FTPClient ftp = new FTPClient(_address);
	Vector vector;
	try {
	    login(ftp);
	    vector = getFiles(ftp, _folder);
	} catch (Object object) {
	    ftp.quit();
	    throw object;
	}
	ftp.quit();
	return vector;
    }
    
    public static Vector getFiles(FTPClient _ftp, String _folder)
	throws IOException {
	Vector filesVec = new Vector();
	String[] lines = _ftp.dir(_folder, true);
	Vector tokensVec = new Vector();
	for (int i = 0; i < lines.length; i++) {
	    tokensVec.removeAllElements();
	    StringTokenizer st = new StringTokenizer(lines[i]);
	    while (st.hasMoreTokens())
		tokensVec.addElement(st.nextToken());
	    if (tokensVec.size() >= 5) {
		String filename
		    = (String) tokensVec.elementAt(tokensVec.size() - 1);
		String sizeStr
		    = (String) tokensVec.elementAt(tokensVec.size() - 5);
		FtpFile ftpFile = new FtpFile(_folder, filename,
					      Integer.parseInt(sizeStr));
		filesVec.addElement(ftpFile);
	    }
	}
	return filesVec;
    }
    
    public static void login(FTPClient _ftp) throws IOException {
	_ftp.login(PropertiesManager.getInstance()
		       .getProperty("jempeg.hijackLogin"),
		   PropertiesManager.getInstance()
		       .getProperty("jempeg.hijackPassword"));
	_ftp.setType(FTPTransferType.BINARY);
	_ftp.setConnectMode(FTPConnectMode.ACTIVE);
    }
    
    public static void reboot(InetAddress _address) {
	try {
	    URL u = new URL("http", _address.getHostAddress(), 80, "/?reboot");
	    InputStream is = u.openStream();
	    is.close();
	} catch (IOException ioexception) {
	    /* empty */
	}
    }
}
