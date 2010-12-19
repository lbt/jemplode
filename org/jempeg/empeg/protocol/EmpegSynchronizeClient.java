/* EmpegSynchronizeClient - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import java.util.Vector;

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.progress.SilentProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.properties.PropertiesUtils;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.TypeConversionUtils;
import com.inzyme.util.Debug;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDConstants;
import org.jempeg.nodestore.FIDLocalFile;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.FIDRemoteTune;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.PlaylistPair;
import org.jempeg.nodestore.SynchronizeException;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.protocol.AbstractSynchronizeClient;
import org.jempeg.protocol.IConnectionFactory;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ProtocolException;

public class EmpegSynchronizeClient extends AbstractSynchronizeClient
{
    private boolean myIsJupiter;
    
    public EmpegSynchronizeClient(IConnectionFactory _connectionFactory) {
	super(_connectionFactory);
	try {
	    myIsJupiter
		= "jupiter".equalsIgnoreCase(getProtocolClient
						 (new SilentProgressListener())
						 .getPlayerType());
	} catch (Throwable t) {
	    throw new ChainedRuntimeException
		      ("Failed to determine whether or not device is a Jupiter.",
		       t);
	}
    }
    
    public IProtocolClient getProtocolClient
	(ISimpleProgressListener _progressListener) {
	EmpegProtocolClient protocolClient
	    = new EmpegProtocolClient(getConnectionFactory()
					  .createConnection(),
				      _progressListener);
	return protocolClient;
    }
    
    public void synchronizeDelete(IFIDNode _node, IProtocolClient _client)
	throws SynchronizeException {
	try {
	    _client.delete(_node.getFID(), false);
	    _client.delete(_node.getFID() | 0x1L, false);
	    _client.delete(_node.getFID() | 0xfL, false);
	} catch (Exception e) {
	    throw new SynchronizeException
		      (new ResourceBundleKey("errors",
					     "synchronize.synchronizeFailed"),
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
	    byte[] playlistArray = _playlist.toProtocol1Format();
	    _protocolClient.write(0L, _playlist.getFID(), 0L,
				  (long) playlistArray.length,
				  new ByteArrayInputStream(playlistArray),
				  (long) playlistArray.length);
	    synchronizeTags(_playlist, _protocolClient);
	} catch (Exception e) {
	    throw new SynchronizeException
		      (new ResourceBundleKey("errors",
					     "synchronize.synchronizeFailed"),
		       e);
	}
    }
    
    public void synchronizeTags
	(IFIDNode _node, IProtocolClient _protocolClient)
	throws SynchronizeException {
	try {
	    long fid = _node.getFID();
	    NodeTags tags = (NodeTags) _node.getTags().clone();
	    if (myIsJupiter) {
		String comment = tags.getValue("comment");
		int ridIndex = comment.indexOf(";RID=");
		if (ridIndex != -1)
		    comment = comment.substring(0, ridIndex);
		tags.setValue("comment",
			      comment + ";RID=" + tags.getValue("rid"));
		String trackNr = tags.getValue("tracknr");
		try {
		    if (trackNr.startsWith("0"))
			tags.setIntValue("tracknr", Integer.parseInt(trackNr));
		} catch (NumberFormatException e) {
		    Debug.println(8, ("Unable to strip leading zeros from "
				      + trackNr + "."));
		}
	    }
	    DynamicData dynData = DynamicData.createDynamicData(tags);
	    DynamicData.removeDynamicData(tags);
	    ByteArrayOutputStream tagsOS = new ByteArrayOutputStream();
	    PropertiesUtils.write(tags.toProperties(), tagsOS, "ISO-8859-1");
	    ((EmpegProtocolClient) _protocolClient)
		.writeFIDFromMemory(fid | 0x1L, tagsOS.toByteArray());
	    WrappedDynamicData wrappedDynData
		= new WrappedDynamicData(dynData);
	    byte[] bytes = wrappedDynData.toByteArray();
	    ((EmpegProtocolClient) _protocolClient)
		.writeFIDFromMemory(fid | 0xfL, bytes);
	} catch (Exception e) {
	    throw new SynchronizeException
		      (new ResourceBundleKey("errors",
					     "synchronize.synchronizeFailed"),
		       e);
	}
    }
    
    protected Reason[] download0
	(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient,
	 boolean _rebuildOnFailure, IProgressListener _progressListener)
	throws ProtocolException, SynchronizeException {
	object = object_6_;
	break;
    }
    
    protected void beforeSynchronize
	(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient,
	 IProgressListener _progressListener)
	throws SynchronizeException, ProtocolException {
	EmpegProtocolClient empegProtocolClient
	    = (EmpegProtocolClient) _protocolClient;
	_progressListener.taskStarted("Locking UI...");
	empegProtocolClient.lockUI(true);
	_progressListener.taskStarted("Checking media ...");
	empegProtocolClient.checkMedia(false);
	_progressListener.taskStarted("Obtaining write lock ...");
	empegProtocolClient.writeLock();
	_progressListener.taskStarted("Deleting music database ...");
	empegProtocolClient.deleteDatabases();
    }
    
    protected void afterSynchronize
	(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient,
	 IProgressListener _progressListener)
	throws SynchronizeException, ProtocolException {
	EmpegProtocolClient empegProtocolClient
	    = (EmpegProtocolClient) _protocolClient;
	_progressListener.taskStarted("Rebuilding music database ...");
	rebuildDatabases(_playerDatabase, empegProtocolClient,
			 _progressListener, true);
	try {
	    _protocolClient.unlock();
	    _protocolClient.close();
	    _progressListener.taskStarted("Rebooting player ...");
	    empegProtocolClient.restartUnit(false, true);
	} catch (Throwable e) {
	    Debug.println(e);
	}
    }
    
    private void retrieveDatabases
	(PlayerDatabase _playerDatabase, EmpegProtocolClient _protocolClient)
	throws ProtocolException, FileNotFoundException {
	byte[] staticBuffer = _protocolClient.readFIDToMemory2(3L);
	byte[] dynamicBuffer;
	try {
	    dynamicBuffer = _protocolClient.readFIDToMemory(4L);
	} catch (Throwable e) {
	    Debug.println(e);
	    dynamicBuffer = new byte[0];
	}
	int staticPos = 0;
	int index = 0;
	WrappedDynamicData wrappedDynData = new WrappedDynamicData();
	LittleEndianInputStream dynDataEIS
	    = (new LittleEndianInputStream
	       (new ByteArrayInputStream(dynamicBuffer)));
	if (dynamicBuffer.length > 0) {
	    try {
		wrappedDynData.readHeader(dynDataEIS);
	    } catch (Throwable e) {
		Debug.println(e);
	    }
	}
	while (staticPos < staticBuffer.length) {
	    DynamicData dynData = null;
	    if (dynamicBuffer.length > 0) {
		try {
		    dynData = wrappedDynData.readNextDynamicData(dynDataEIS);
		} catch (Throwable e) {
		    Debug.println(e);
		}
	    }
	    if (staticBuffer[staticPos] != -1) {
		boolean stopReading
		    = createNode(_playerDatabase,
				 FIDConstants.makeFID(index, 0), staticBuffer,
				 staticPos, dynData);
		if (stopReading)
		    break;
	    }
	    for (/**/;
		 (staticBuffer[staticPos] != -1
		  && staticPos < staticBuffer.length);
		 staticPos
		     = ++staticPos + (TypeConversionUtils
					  .toUnsigned8(staticBuffer[staticPos])
				      + 1)) {
		/* empty */
	    }
	    staticPos++;
	    index++;
	}
    }
    
    private synchronized void retrieveTagIndex
	(PlayerDatabase _playerDatabase, EmpegProtocolClient _protocolClient)
	throws ProtocolException, FileNotFoundException {
	_protocolClient.checkProtocolVersion();
	byte[] tagBuffer = _protocolClient.readFIDToMemory(2L);
	if (tagBuffer.length <= 16)
	    throw new ProtocolException("Tags file is suspiciously short ("
					+ tagBuffer.length
					+ " bytes), failing gracefully.");
	String str;
	try {
	    str = new String(tagBuffer, "ISO-8859-1");
	} catch (UnsupportedEncodingException e) {
	    Debug.println(e);
	    str = new String(tagBuffer);
	}
	StringTokenizer tokenizer = new StringTokenizer(str, "\n");
	int index = 0;
	while (tokenizer.hasMoreTokens()) {
	    String tagName = tokenizer.nextToken();
	    if (tagName.length() > 0) {
		_playerDatabase.getDatabaseTags().setName(index, tagName);
		index++;
	    }
	}
    }
    
    private boolean createNode(PlayerDatabase _playerDatabase, long _fid,
			       byte[] _staticEntries, int _staticIndex,
			       DynamicData _dynamicData) {
	NodeTags tags = new NodeTags();
	DatabaseTags dbTags = _playerDatabase.getDatabaseTags();
	int pos = _staticIndex;
	while (_staticEntries[pos] != -1) {
	    int type = TypeConversionUtils.toUnsigned8(_staticEntries[pos++]);
	    int len = TypeConversionUtils.toUnsigned8(_staticEntries[pos++]);
	    if (len > 0) {
		String name;
		try {
		    name = new String(_staticEntries, pos, len, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
		    Debug.println(e);
		    name = new String(_staticEntries, pos, len);
		}
		String tagName = dbTags.getName(type);
		if (tagName == null)
		    Debug.println(16, "Empty tagName???");
		else
		    tags.setValue(tagName, name);
		pos += len;
	    }
	}
	if (myIsJupiter) {
	    String comment = tags.getValue("comment");
	    int ridIndex = comment.indexOf(";RID=");
	    if (ridIndex != -1)
		tags.setValue("rid", comment.substring(ridIndex + 5));
	}
	if (_dynamicData != null)
	    _dynamicData.fillInDynamicData(tags);
	tags.setDirty(false);
	if (tags.getValue("type").equals("illegal"))
	    return false;
	try {
	    String type = tags.getValue("type");
	    IFIDNode node;
	    if (type.equals("tune"))
		node = new FIDRemoteTune(_playerDatabase, _fid, tags);
	    else if (type.equals("playlist"))
		node = new FIDPlaylist(_playerDatabase, _fid, tags, false,
				       false);
	    else
		throw new IllegalArgumentException("Unknown FID type '" + type
						   + "' for " + _fid
						   + " (title = "
						   + tags.getValue("title")
						   + ")");
	    if (node == null) {
		Debug.println(8,
			      "Node for " + _fid + " == null (" + tags + ")");
		return true;
	    }
	} catch (IllegalArgumentException e) {
	    Debug.println(8, ("Unknown fid type " + tags.getValue("type")
			      + " for fid " + _fid + " titled \""
			      + tags.getValue("title") + "\""));
	}
	return false;
    }
    
    private synchronized void rebuildDatabases
	(PlayerDatabase _playerDatabase, EmpegProtocolClient _protocolClient,
	 IProgressListener _progressListener, boolean _goForBroke)
	throws ProtocolException {
	boolean rebuildFromMemory
	    = PropertiesManager.getInstance()
		  .getBooleanProperty("rebuildFromMemory");
	int size = _playerDatabase.getSize();
	do {
	    if (size > 0 && rebuildFromMemory && _goForBroke) {
		Debug.println
		    (8,
		     "EmpegSynchronizeClient.rebuildDatabases: rebuild from memory");
		try {
		    EmpegDatabaseRebuilder.rebuildDatabaseFromMemory
			(_playerDatabase, _protocolClient, _progressListener);
		    break;
		} catch (Throwable e) {
		    Debug.println(e);
		    Debug.println
			(8,
			 "EmpegSynchronizeClient.rebuildDatabases: falling back to old way ...");
		    _protocolClient.rebuildPlayerDatabase(0L);
		    break;
		}
	    }
	    _protocolClient.rebuildPlayerDatabase(0L);
	} while (false);
    }
    
    private Reason[] rebuildOnFailure
	(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient,
	 boolean _rebuildOnFail, IProgressListener _progressListener)
	throws SynchronizeException, ProtocolException {
	Reason[] reasons;
	if (_rebuildOnFail) {
	    _progressListener.taskStarted("Rebuilding databases");
	    _progressListener.taskUpdated(0L, 1L);
	    EmpegProtocolClient empegProtocolClient
		= (EmpegProtocolClient) _protocolClient;
	    try {
		try {
		    beforeSynchronize(_playerDatabase, _protocolClient,
				      _progressListener);
		    _progressListener
			.taskStarted("Rebuilding music database ...");
		    rebuildDatabases(_playerDatabase, empegProtocolClient,
				     _progressListener, false);
		    computeFreeSpace(_playerDatabase, _protocolClient);
		} catch (Throwable e) {
		    Debug.println(e);
		}
	    } catch (Object object) {
		try {
		    _protocolClient.unlock();
		    _progressListener.taskStarted("Rebooting player ...");
		    empegProtocolClient.restartUnit(false, true);
		} catch (Throwable e) {
		    Debug.println(e);
		}
		throw object;
	    }
	    try {
		_protocolClient.unlock();
		_progressListener.taskStarted("Rebooting player ...");
		empegProtocolClient.restartUnit(false, true);
	    } catch (Throwable e) {
		Debug.println(e);
	    }
	    _progressListener.taskUpdated(1L, 1L);
	    reasons = download0(_playerDatabase, _protocolClient, false,
				_progressListener);
	} else
	    reasons = new Reason[0];
	return reasons;
    }
}
