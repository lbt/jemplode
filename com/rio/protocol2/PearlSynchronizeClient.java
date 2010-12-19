/* PearlSynchronizeClient - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Properties;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.MemoryImportFile;
import com.inzyme.io.PaddedInputStream;
import com.inzyme.io.PaddedOutputStream;
import com.inzyme.io.StreamUtils;
import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.util.ApplicationUtils;
import com.inzyme.util.Debug;

import org.jempeg.nodestore.FIDLocalFile;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.FIDRemoteTune;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeReplacedDatabaseChange;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.PlaylistPair;
import org.jempeg.nodestore.SynchronizeException;
import org.jempeg.nodestore.model.NodeFinder;
import org.jempeg.protocol.AbstractSynchronizeClient;
import org.jempeg.protocol.FIDInputStream;
import org.jempeg.protocol.IConnectionFactory;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ProtocolException;

public class PearlSynchronizeClient extends AbstractSynchronizeClient
{
    private static final String SOUP_PROPERTIES_FILENAME = ".soups";
    private boolean myUsesSoups;
    
    public PearlSynchronizeClient(IConnectionFactory _connectionFactory) {
	super(_connectionFactory);
    }
    
    public IProtocolClient getProtocolClient
	(ISimpleProgressListener _progressListener) {
	PearlProtocolClient protocolClient
	    = new PearlProtocolClient(getConnectionFactory()
					  .createConnection(),
				      _progressListener, this);
	return protocolClient;
    }
    
    public void synchronizeDelete(IFIDNode _node, IProtocolClient _client)
	throws SynchronizeException {
	try {
	    _client.delete(_node.getFID(), false);
	} catch (ProtocolException e) {
	    throw new SynchronizeException
		      ((new ResourceBundleKey
			("errors", "synchronize.synchronizeNodeFailed",
			 new Object[] { _node })),
		       e);
	}
    }
    
    public void synchronizeFile
	(IFIDNode _node, IProtocolClient _protocolClient)
	throws SynchronizeException {
	try {
	    FIDLocalFile localFileNode = (FIDLocalFile) _node;
	    IImportFile localFile = localFileNode.getFile();
	    long length = localFile.getLength();
	    long fid = localFileNode.getFID();
	    java.io.InputStream is
		= new BufferedInputStream(localFile.getInputStream());
	    try {
		_protocolClient.write(0L, fid, 0L, length, is, length);
	    } catch (Object object) {
		is.close();
		throw object;
	    }
	    is.close();
	} catch (Exception e) {
	    throw new SynchronizeException
		      ((new ResourceBundleKey
			("errors", "synchronize.synchronizeNodeFailed",
			 new Object[] { _node })),
		       e);
	}
    }
    
    public void synchronizePlaylistTags
	(FIDPlaylist _playlist, PlaylistPair[] _playlistPairs,
	 IProtocolClient _protocolClient)
	throws SynchronizeException {
	try {
	    NodeTags tags = _playlist.getTags();
	    Properties tagsProperties = tags.toProperties();
	    String playlistStr
		= PearlStringUtils.getPlaylistString(_playlistPairs);
	    tagsProperties.put("playlist", playlistStr);
	    PearlProtocolClient pearlProtocolClient
		= (PearlProtocolClient) _protocolClient;
	    pearlProtocolClient.changeFileInfo(_playlist.getFID(),
					       tagsProperties);
	} catch (Exception e) {
	    throw new SynchronizeException
		      ((new ResourceBundleKey
			("errors", "synchronize.synchronizeNodeFailed",
			 new Object[] { _playlist })),
		       e);
	}
    }
    
    public void synchronizeTags
	(IFIDNode _node, IProtocolClient _protocolClient)
	throws SynchronizeException {
	try {
	    NodeTags tags = _node.getTags();
	    Properties tagsProperties = tags.toProperties();
	    PearlProtocolClient pearlProtocolClient
		= (PearlProtocolClient) _protocolClient;
	    pearlProtocolClient.changeFileInfo(_node.getFID(), tagsProperties);
	} catch (ProtocolException e) {
	    throw new SynchronizeException
		      ((new ResourceBundleKey
			("errors", "synchronize.synchronizeNodeFailed",
			 new Object[] { _node })),
		       e);
	}
    }
    
    protected void beforeSynchronize
	(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient,
	 IProgressListener _progressListener)
	throws SynchronizeException, ProtocolException {
	try {
	    Properties soupProperties = new Properties();
	    Enumeration nodesEnum = _playerDatabase.getNodeMap().elements();
	    while (nodesEnum.hasMoreElements()) {
		IFIDNode node = (IFIDNode) nodesEnum.nextElement();
		if (node instanceof FIDPlaylist && !node.isTransient()) {
		    String soupTag = node.getTags().getValue("soup");
		    if (soupTag.length() > 0) {
			soupProperties.setProperty(String
						       .valueOf(node.getFID()),
						   soupTag);
			myUsesSoups = true;
		    }
		}
	    }
	    if (myUsesSoups) {
		ByteArrayOutputStream soupOutputStream
		    = new ByteArrayOutputStream();
		soupProperties.save(soupOutputStream, "soups");
		MemoryImportFile soupImportFile
		    = new MemoryImportFile(".soups",
					   soupOutputStream.toByteArray(),
					   new Properties());
		IFIDNode soupPropertiesNode
		    = getSoupPropertiesNode(_playerDatabase);
		if (soupPropertiesNode == null) {
		    FIDLocalFile localFile
			= new FIDLocalFile(_playerDatabase, soupImportFile,
					   false);
		    localFile.getTags().setValue("type", "taxi");
		    localFile.getTags().setValue("title", ".soups");
		    localFile.addToDatabase(true);
		} else
		    _playerDatabase.getSynchronizeQueue().enqueue
			(new NodeReplacedDatabaseChange(soupPropertiesNode,
							soupImportFile));
	    }
	} catch (Throwable t) {
	    Debug.println(t);
	}
    }
    
    protected void afterSynchronize
	(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient,
	 IProgressListener _progressListener)
	throws SynchronizeException, ProtocolException {
	/* empty */
    }
    
    protected Reason[] download0
	(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient,
	 boolean _rebuildOnFailure, IProgressListener _progressListener)
	throws ProtocolException, SynchronizeException {
	_playerDatabase.setCreateUnattachedItems(false);
	IDeviceSettings deviceSettings = _protocolClient.getDeviceSettings();
	long maxValue = _playerDatabase.getUsedSpace();
	long curValue = 0L;
	File readFileInfoCacheFile
	    = new File(getCacheFolder(deviceSettings), "fileinfo");
	File writeFileInfoCacheFile
	    = new File(getCacheFolder(deviceSettings), "fileinfo.tmp");
	FileInfoEnumeration fileInfoEnum
	    = readCachedFileInfo(readFileInfoCacheFile, deviceSettings);
	boolean readCache = fileInfoEnum != null;
	boolean writeCache = false;
	PaddedOutputStream fileInfoCacheOutputStream = null;
	if (!readCache) {
	    fileInfoEnum
		= ((PearlProtocolClient) _protocolClient).getAllFileInfo();
	    if (PropertiesManager.getInstance()
		    .getBooleanProperty("cacheDatabase")) {
		try {
		    FileOutputStream fos
			= new FileOutputStream(writeFileInfoCacheFile);
		    fileInfoCacheOutputStream = new PaddedOutputStream(fos, 4);
		    writeCache = true;
		} catch (Throwable t) {
		    Debug.println(t);
		    try {
			/* empty */
		    } catch (Object object) {
			try {
			    fileInfoEnum.release();
			    StreamUtils.close(fileInfoCacheOutputStream);
			    if (writeCache) {
				readFileInfoCacheFile.delete();
				writeFileInfoCacheFile
				    .renameTo(readFileInfoCacheFile);
				readFileInfoCacheFile.setLastModified
				    (deviceSettings.getDeviceGeneration()
				     * 1000L);
			    }
			} catch (Throwable t_0_) {
			    Debug.println(t_0_);
			}
			throw object;
		    }
		}
	    }
	}
	while (fileInfoEnum.hasMoreElements()) {
	    Properties fileInfo = (Properties) fileInfoEnum.nextElement();
	    if (writeCache) {
		try {
		    fileInfoCacheOutputStream.writeProperties
			(fileInfo, PearlStringUtils.NAME_TO_ENCODING, false);
		    fileInfoCacheOutputStream.write(10);
		} catch (Throwable e) {
		    Debug.println(e);
		    writeCache = false;
		}
	    }
	    String fidStr = fileInfo.getProperty("fid");
	    long fid;
	    if (fidStr == null)
		fid = -1L;
	    else
		fid = Long.parseLong(fidStr);
	    String type = fileInfo.getProperty("type");
	    if (type == null)
		type = "";
	    IFIDNode node;
	    if ("tune".equals(type) || "taxi".equals(type)) {
		NodeTags nodeTags = new NodeTags(fileInfo);
		node = new FIDRemoteTune(_playerDatabase, fid, nodeTags);
	    } else if ("playlist".equals(type)) {
		String playlistStr = fileInfo.getProperty("playlist");
		if (playlistStr == null)
		    playlistStr = "";
		fileInfo.remove("playlist");
		PlaylistPair[] pairs
		    = PearlStringUtils.getPlaylistPairs(playlistStr);
		node = new FIDPlaylist(_playerDatabase, fid,
				       new NodeTags(fileInfo), false, pairs);
	    } else {
		NodeTags nodeTags = new NodeTags(fileInfo);
		nodeTags.setValue("type", "illegal");
		node = new FIDRemoteTune(_playerDatabase, fid, nodeTags);
		Debug.println(8, "Unknown file info: " + fileInfo);
	    }
	    if (node != null)
		curValue += (long) node.getLength();
	    _progressListener.progressReported
		((long) (int) (100.0
			       * ((double) curValue / (double) maxValue)),
		 100L);
	}
	FIDInputStream fis = null;
	try {
	    try {
		IFIDNode soupPropertiesNode
		    = getSoupPropertiesNode(_playerDatabase);
		if (soupPropertiesNode != null) {
		    myUsesSoups = true;
		    fis = new FIDInputStream(_protocolClient,
					     soupPropertiesNode.getFID());
		    Properties soupProperties = new Properties();
		    soupProperties.load(fis);
		    Enumeration fidsEnum = soupProperties.keys();
		    while (fidsEnum.hasMoreElements()) {
			String fidStr = (String) fidsEnum.nextElement();
			String soup = soupProperties.getProperty(fidStr);
			long fid = Long.parseLong(fidStr);
			IFIDNode fidNode = _playerDatabase.getNode(fid);
			fidNode.getTags().setValue("soup", soup, false);
		    }
		}
	    } catch (Throwable t) {
		Debug.println(t);
	    }
	} catch (Object object) {
	    StreamUtils.close(fis);
	    throw object;
	}
	StreamUtils.close(fis);
	Reason[] reasons = new Reason[0];
	try {
	    fileInfoEnum.release();
	    StreamUtils.close(fileInfoCacheOutputStream);
	    if (writeCache) {
		readFileInfoCacheFile.delete();
		writeFileInfoCacheFile.renameTo(readFileInfoCacheFile);
		readFileInfoCacheFile.setLastModified
		    (deviceSettings.getDeviceGeneration() * 1000L);
	    }
	} catch (Throwable t) {
	    Debug.println(t);
	}
	return reasons;
    }
    
    private File getCacheFolder(IDeviceSettings _deviceSettings) {
	File cacheFolder
	    = new File(ApplicationUtils.getSettingsFolder(),
		       "pearl-" + _deviceSettings.getSerialNumber());
	cacheFolder.mkdirs();
	return cacheFolder;
    }
    
    private FileInfoEnumeration readCachedFileInfo
	(File _fileInfoCacheFile, IDeviceSettings _deviceSettings) {
	FileInfoEnumeration cachedFileInfoEnum;
	try {
	    if (PropertiesManager.getInstance()
		    .getBooleanProperty("cacheDatabase")
		&& _fileInfoCacheFile.exists()
		&& (_fileInfoCacheFile.lastModified() / 1000L
		    == _deviceSettings.getDeviceGeneration())) {
		FileInputStream fileInfoInputStream
		    = new FileInputStream(_fileInfoCacheFile);
		PaddedInputStream paddedInputStream
		    = new PaddedInputStream(fileInfoInputStream, 4);
		cachedFileInfoEnum
		    = new FileInfoEnumeration(paddedInputStream, true);
	    } else
		cachedFileInfoEnum = null;
	} catch (Throwable t) {
	    cachedFileInfoEnum = null;
	}
	return cachedFileInfoEnum;
    }
    
    private IFIDNode getSoupPropertiesNode(PlayerDatabase _playerDatabase)
	throws ParseException {
	NodeFinder searcher = new NodeFinder(_playerDatabase);
	IFIDNode[] matchingNodes
	    = searcher.findMatchingNodes("type = taxi and title = .soups");
	IFIDNode soupPropertiesNode;
	if (matchingNodes.length == 1)
	    soupPropertiesNode = matchingNodes[0];
	else
	    soupPropertiesNode = null;
	return soupPropertiesNode;
    }
}
