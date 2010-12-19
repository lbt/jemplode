package org.jempeg.empeg.protocol;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jempeg.JEmplodeProperties;
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

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.progress.SilentProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.properties.PropertiesUtils;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.TypeConversionUtils;
import com.inzyme.util.Debug;

public class EmpegSynchronizeClient extends AbstractSynchronizeClient {
  private boolean myIsJupiter;

  public EmpegSynchronizeClient(IConnectionFactory _connectionFactory) {
    super(_connectionFactory);
    try {
      myIsJupiter = "jupiter".equalsIgnoreCase(getProtocolClient(new SilentProgressListener()).getPlayerType());
    }
    catch (Throwable t) {
      throw new ChainedRuntimeException("Failed to determine whether or not device is a Jupiter.", t);
    }
  }

  public IProtocolClient getProtocolClient(ISimpleProgressListener _progressListener) {
    EmpegProtocolClient protocolClient = new EmpegProtocolClient(getConnectionFactory().createConnection(), _progressListener);
    return protocolClient;
  }

  public void synchronizeDelete(IFIDNode _node, IProtocolClient _client) throws SynchronizeException {
    try {
      _client.delete(_node.getFID(), false);
      _client.delete(_node.getFID() | 0x1, false);
      _client.delete(_node.getFID() | 0xf, false);
    }
    catch (Exception e) {
      throw new SynchronizeException(new ResourceBundleKey(ResourceBundleUtils.ERRORS_KEY, "synchronize.synchronizeFailed"), e);
    }
  }

  public void synchronizeFile(IFIDNode _node, IProtocolClient _protocolClient) throws SynchronizeException {
    try {
      FIDLocalFile localFileNode = (FIDLocalFile) _node;
      IImportFile localFile = localFileNode.getFile();
      long length = localFile.getLength();
      long fid = localFileNode.getFID();
      InputStream is = new BufferedInputStream(localFile.getInputStream());
      try {
        _protocolClient.write(IProtocolClient.STORAGE_ZERO, fid, 0, length, is, length);
      }
      finally {
        is.close();
      }
    }
    catch (Exception e) {
      throw new SynchronizeException(new ResourceBundleKey(ResourceBundleUtils.ERRORS_KEY, "synchronize.synchronizeNodeFailed", new Object[] {
        _node
      }), e);
    }
  }

  public void synchronizePlaylistTags(FIDPlaylist _playlist, PlaylistPair[] _playlistPairs, IProtocolClient _protocolClient) throws SynchronizeException {
    try {
      byte[] playlistArray = _playlist.toProtocol1Format();
      _protocolClient.write(IProtocolClient.STORAGE_ZERO, _playlist.getFID(), 0, playlistArray.length, new ByteArrayInputStream(playlistArray), playlistArray.length);
      // write FID offset 0 (playlist contents)
      // write out the tags, fid|1
      synchronizeTags(_playlist, _protocolClient);
    }
    catch (Exception e) {
      throw new SynchronizeException(new ResourceBundleKey(ResourceBundleUtils.ERRORS_KEY, "synchronize.synchronizeFailed"), e);
    }
  }

  public void synchronizeTags(IFIDNode _node, IProtocolClient _protocolClient) throws SynchronizeException {
    try {
      long fid = _node.getFID();
      NodeTags tags = (NodeTags) _node.getTags().clone();

      if (myIsJupiter) {
        // Hack for Jupiters because they don't support the RID tag
        String comment = tags.getValue(DatabaseTags.COMMENT_TAG);
        int ridIndex = comment.indexOf(";RID=");
        if (ridIndex != -1) {
          comment = comment.substring(0, ridIndex);
        }
        tags.setValue(DatabaseTags.COMMENT_TAG, comment + ";RID=" + tags.getValue(DatabaseTags.RID_TAG));

        // Jupiters also have a bug with track numbers starting with a zero, so strip those off
        String trackNr = tags.getValue(DatabaseTags.TRACKNR_TAG);
        try {
          if (trackNr.startsWith("0")) {
            tags.setIntValue(DatabaseTags.TRACKNR_TAG, Integer.parseInt(trackNr));
          }
        }
        catch (NumberFormatException e) {
          Debug.println(Debug.WARNING, "Unable to strip leading zeros from " + trackNr + ".");
        }
      }

      DynamicData dynData = DynamicData.createDynamicData(tags);
      DynamicData.removeDynamicData(tags);
      ByteArrayOutputStream tagsOS = new ByteArrayOutputStream();
      PropertiesUtils.write(tags.toProperties(), tagsOS, "ISO-8859-1");
      ((EmpegProtocolClient) _protocolClient).writeFIDFromMemory(fid | 1, tagsOS.toByteArray());
      WrappedDynamicData wrappedDynData = new WrappedDynamicData(dynData);
      byte[] bytes = wrappedDynData.toByteArray();
      ((EmpegProtocolClient) _protocolClient).writeFIDFromMemory(fid | 0xF, bytes);
    }
    catch (Exception e) {
      throw new SynchronizeException(new ResourceBundleKey(ResourceBundleUtils.ERRORS_KEY, "synchronize.synchronizeFailed"), e);
    }
  }

  protected Reason[] download0(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient, boolean _rebuildOnFailure, IProgressListener _progressListener) throws ProtocolException, SynchronizeException {
    _playerDatabase.setNestedPlaylistAllowed(true);

    EmpegProtocolClient protocolClient = (EmpegProtocolClient) _protocolClient;
    _progressListener.setWaitState(true);
    Vector reasonsVec = new Vector();
    try {
      _protocolClient.open();
      _playerDatabase.setDeviceSettings(_protocolClient.getDeviceSettings());
      _playerDatabase.setCreateUnattachedItems(true);

      _progressListener.taskStarted("Checking connection...");
      _progressListener.taskUpdated(0, 1);
      boolean connected = false;
      for (long startTime = System.currentTimeMillis(); !connected && (System.currentTimeMillis() - startTime) < 15000; ) {
        if (_protocolClient.isDeviceConnected()) {
          connected = true;
        }
        try {
          Thread.sleep(1000);
        }
        catch (Throwable t) {
        }
      }
      if (!connected) {
        throw new ProtocolException("Unit not found, check cabling and setup.");
      }
      _protocolClient.getProtocolVersion();
      _protocolClient.checkProtocolVersion();

      _progressListener.taskUpdated(1, 1);
      _progressListener.operationStarted("Downloading...");
      _progressListener.operationUpdated(0, 1);
      computeFreeSpace(_playerDatabase, _protocolClient);

      int operationsTotal = 10;
      int operationsCurrent = 0;
      _progressListener.taskStarted("Starting download...");
      _progressListener.taskUpdated(operationsCurrent ++, operationsTotal);

      // first things first, remove old tag names
      // MODIFIED
      //			_playerDatabase.getNodeMap().reset();
      //			_playerDatabase.removeAllNodeListeners();
      //			_playerDatabase.removeAllNodeTagListeners();
      _progressListener.taskUpdated(operationsCurrent ++, operationsTotal);

      // retrieve the tags
      _progressListener.taskStarted("Retrieving tags index...");
      try {
        retrieveTagIndex(_playerDatabase, protocolClient);
      }
      catch (Throwable e) {
        reasonsVec.addElement(new Reason(e));
        Reason.fromArray(rebuildOnFailure(_playerDatabase, _protocolClient, _rebuildOnFailure, _progressListener), reasonsVec);
        return Reason.toArray(reasonsVec);
      }
      _progressListener.taskUpdated(operationsCurrent ++, operationsTotal);

      // retrieve the database FIDs
      _progressListener.taskStarted("Retrieving databases...");
      try {
        retrieveDatabases(_playerDatabase, protocolClient);
      }
      catch (Throwable e) {
        reasonsVec.addElement(new Reason(e));
        Reason.fromArray(rebuildOnFailure(_playerDatabase, _protocolClient, _rebuildOnFailure, _progressListener), reasonsVec);
        return Reason.toArray(reasonsVec);
      }
      _progressListener.taskUpdated(operationsCurrent ++, operationsTotal);

      _progressListener.taskStarted("Retrieving playlists...");
      long[] buffer;
      try {
        byte[] byteBuffer = protocolClient.readFIDToMemory(FIDConstants.FID_PLAYLISTDATABASE);
        buffer = new long[byteBuffer.length / 4];
        ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
        LittleEndianInputStream eis = new LittleEndianInputStream(bais);
        for (int i = 0; i < buffer.length; i ++ ) {
          buffer[i] = eis.readUnsigned32();
        }
        Debug.println(Debug.VERBOSE, "FIDPlaylistDatabase contains " + buffer.length + " entries");
      }
      catch (Throwable e) {
        Debug.println(e);
        Reason.fromArray(rebuildOnFailure(_playerDatabase, _protocolClient, _rebuildOnFailure, _progressListener), reasonsVec);
        return Reason.toArray(reasonsVec);
      }

      long maxFid = _playerDatabase.getNodeMap().getMax();
      Debug.println(Debug.VERBOSE, "FIDPlaylistDatabase maxFID = " + maxFid);
      int pos = 0;
      for (int i = FIDConstants.FID_ROOTPLAYLIST; i <= maxFid; i ++ ) {
        FIDPlaylist playlist = _playerDatabase.getPlaylist(i);
        if (playlist == null) {
          continue;
        }

        int nFids = playlist.getTags().getIntValue(DatabaseTags.LENGTH_TAG, 0) / 4;
        //        System.out.println("EmpegSynchronizeClient.download0: nFids = " + nFids + " of " + playlist);
        if (nFids > buffer.length) {
          reasonsVec.addElement(new Reason("Inconsistency in playlist database. nFids = " + nFids + " but buffer.length = " + buffer.length));
          Reason.fromArray(rebuildOnFailure(_playerDatabase, _protocolClient, _rebuildOnFailure, _progressListener), reasonsVec);
          return Reason.toArray(reasonsVec);
        }

        //					Debug.println(Debug.VERBOSE, "nFids for " + playlist + " = " + nFids
        // + " (" + pos + "-" + (pos + nFids) + ") of " + buffer.length);
        PlaylistPair[] pairs = new PlaylistPair[nFids];
        int pairNum = 0;
        for (pairNum = 0; pairNum < nFids && pos < buffer.length; pairNum ++ ) {
          //System.out.println("EmpegSynchronizeClient.download0: " + pairNum + " of " + nFids + ", " + pos + "," + buffer.length);
          pairs[pairNum] = new PlaylistPair(buffer[pos ++], 0);
        }
        if (pairNum != nFids) {
          reasonsVec.addElement(new Reason("Inconsistency in playlist length. nFids = " + nFids + " but pairNum = " + pairNum));
          Reason.fromArray(rebuildOnFailure(_playerDatabase, _protocolClient, _rebuildOnFailure, _progressListener), reasonsVec);
          return Reason.toArray(reasonsVec);
        }

        playlist.populate(pairs);
        playlist.setDirty(false);
      }
      Debug.println(Debug.VERBOSE, "Ended up with " + pos + " of " + buffer.length);
      if (pos != buffer.length) {
        reasonsVec.addElement(new Reason("Inconsistency in playlist database. pos = " + pos + " but buffer.length = " + buffer.length));
        Reason.fromArray(rebuildOnFailure(_playerDatabase, _protocolClient, _rebuildOnFailure, _progressListener), reasonsVec);
        return Reason.toArray(reasonsVec);
      }
      _progressListener.taskUpdated(operationsCurrent ++, operationsTotal);

      NodeTag.resetNodeTags(_playerDatabase.getDatabaseTags());

      _progressListener.operationUpdated(1, 1);
      _progressListener.taskStarted("Updating interface...");

      // tags or database is missing?
      // Check Stuff
      Reason rootPlaylistReason = _playerDatabase.checkRootLists();
      if (rootPlaylistReason != null) {
        reasonsVec.addElement(rootPlaylistReason);
      }
      _progressListener.taskUpdated(1, 1);

      return Reason.toArray(reasonsVec);
    }
    catch (ProtocolException e) {
      throw new SynchronizeException(new ResourceBundleKey(ResourceBundleUtils.ERRORS_KEY, "synchronize.databaseDownloadFailed"), e);
    }
    finally {
      _progressListener.setWaitState(false);
      try {
        if (_protocolClient != null) {
          _protocolClient.close();
        }
      }
      catch (Throwable t) {
        Debug.println(t);
      }
    }
  }

  protected void beforeSynchronize(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient, IProgressListener _progressListener) throws SynchronizeException, ProtocolException {
    EmpegProtocolClient empegProtocolClient = (EmpegProtocolClient) _protocolClient;
    _progressListener.taskStarted("Locking UI...");
    empegProtocolClient.lockUI(true);
    _progressListener.taskStarted("Checking media ...");
    empegProtocolClient.checkMedia(false);
    _progressListener.taskStarted("Obtaining write lock ...");
    empegProtocolClient.writeLock();
    _progressListener.taskStarted("Deleting music database ...");
    empegProtocolClient.deleteDatabases();
  }

  protected void afterSynchronize(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient, IProgressListener _progressListener) throws SynchronizeException, ProtocolException {
    EmpegProtocolClient empegProtocolClient = (EmpegProtocolClient) _protocolClient;
    _progressListener.taskStarted("Rebuilding music database ...");
    rebuildDatabases(_playerDatabase, empegProtocolClient, _progressListener, true);
    try {
      _protocolClient.unlock();
      _protocolClient.close();
      _progressListener.taskStarted("Rebooting player ...");
      empegProtocolClient.restartUnit(false, true);
    }
    catch (Throwable e) {
      Debug.println(e);
    }
  }

  private void retrieveDatabases(PlayerDatabase _playerDatabase, EmpegProtocolClient _protocolClient) throws ProtocolException, FileNotFoundException {
    byte[] staticBuffer = _protocolClient.readFIDToMemory2(FIDConstants.FID_STATICDATABASE);
    byte[] dynamicBuffer;
    try {
      dynamicBuffer = _protocolClient.readFIDToMemory(FIDConstants.FID_DYNAMICDATABASE);
    }
    catch (Throwable e) {
      Debug.println(e);
      dynamicBuffer = new byte[0];
    }

    int staticPos = 0;
    int index = 0;
    WrappedDynamicData wrappedDynData = new WrappedDynamicData();
    LittleEndianInputStream dynDataEIS = new LittleEndianInputStream(new ByteArrayInputStream(dynamicBuffer));
    if (dynamicBuffer.length > 0) {
      try {
        wrappedDynData.readHeader(dynDataEIS);
      }
      catch (Throwable e) {
        Debug.println(e);
      }
    }

    while (staticPos < staticBuffer.length) {
      DynamicData dynData = null;
      if (dynamicBuffer.length > 0) {
        try {
          dynData = wrappedDynData.readNextDynamicData(dynDataEIS);
        }
        catch (Throwable e) {
          Debug.println(e);
        }
      }

      if (staticBuffer[staticPos] != -1) {
        boolean stopReading = createNode(_playerDatabase, FIDConstants.makeFID(index, FIDConstants.FIDTYPE_TUNE), staticBuffer, staticPos, dynData);
        if (stopReading) {
          break;
        }
      }

      // Skip to the next record
      while (staticBuffer[staticPos] != -1 && staticPos < staticBuffer.length) {
        staticPos ++;
        staticPos += TypeConversionUtils.toUnsigned8(staticBuffer[staticPos]) + 1;
      }

      staticPos ++;
      index ++;
    }
  }

  private synchronized void retrieveTagIndex(PlayerDatabase _playerDatabase, EmpegProtocolClient _protocolClient) throws ProtocolException, FileNotFoundException {
    _protocolClient.checkProtocolVersion();
    byte[] tagBuffer = _protocolClient.readFIDToMemory(FIDConstants.FID_TAGINDEX);
    if (tagBuffer.length <= 16) {
      throw new ProtocolException("Tags file is suspiciously short (" + tagBuffer.length + " bytes), failing gracefully.");
    }
    String str;
    try {
      str = new String(tagBuffer, "ISO-8859-1");
    }
    catch (UnsupportedEncodingException e) {
      Debug.println(e);
      str = new String(tagBuffer);
    }
    StringTokenizer tokenizer = new StringTokenizer(str, "\n");
    int index = 0;
    while (tokenizer.hasMoreTokens()) {
      String tagName = tokenizer.nextToken();
      if (tagName.length() > 0) {
        _playerDatabase.getDatabaseTags().setName(index, tagName);
        index ++;
      }
    }
  }

  private boolean createNode(PlayerDatabase _playerDatabase, long _fid, byte[] _staticEntries, int _staticIndex, DynamicData _dynamicData) {
    //myProgressListener.taskUpdated(_staticIndex, _staticEntries.length);
    // extract this fid's tags from the static_entries structure

    NodeTags tags = new NodeTags();
    DatabaseTags dbTags = _playerDatabase.getDatabaseTags();
    int pos = _staticIndex;
    while (_staticEntries[pos] != -1) {
      int type = TypeConversionUtils.toUnsigned8(_staticEntries[pos ++]);
      int len = TypeConversionUtils.toUnsigned8(_staticEntries[pos ++]);
      if (len > 0) {
        String name;
        try {
          name = new String(_staticEntries, pos, len, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
          Debug.println(e);
          name = new String(_staticEntries, pos, len);
        }
        String tagName = dbTags.getName(type);
        if (tagName == null) {
          Debug.println(Debug.ERROR, "Empty tagName???");
        }
        else {
          //System.out.println("EmpegSynchronizeClient.createNode: " + tagName + "=" + name);
          tags.setValue(tagName, name);
        }
        pos += len;
      }
    }

    // Hack for Jupiters because they don't support the RID tag
    if (myIsJupiter) {
      String comment = tags.getValue(DatabaseTags.COMMENT_TAG);
      int ridIndex = comment.indexOf(";RID=");
      if (ridIndex != -1) {
        tags.setValue(DatabaseTags.RID_TAG, comment.substring(ridIndex + 5));
      }
    }

    if (_dynamicData != null) {
      _dynamicData.fillInDynamicData(tags);
    }
    tags.setDirty(false);

    if (tags.getValue(DatabaseTags.TYPE_TAG).equals(DatabaseTags.TYPE_ILLEGAL)) {
      return false;
    }

    try {
      String type = tags.getValue(DatabaseTags.TYPE_TAG);
      IFIDNode node;

      if (type.equals(DatabaseTags.TYPE_TUNE)) {
        node = new FIDRemoteTune(_playerDatabase, _fid, tags);
      }
      else if (type.equals(DatabaseTags.TYPE_PLAYLIST)) {
        node = new FIDPlaylist(_playerDatabase, _fid, tags, false, false);
      }
      else {
        throw new IllegalArgumentException("Unknown FID type '" + type + "' for " + _fid + " (title = " + tags.getValue(DatabaseTags.TITLE_TAG) + ")");
      }

      if (node == null) {
        Debug.println(Debug.WARNING, "Node for " + _fid + " == null (" + tags + ")");
        return true;
      }
    }
    catch (IllegalArgumentException e) {
      Debug.println(Debug.WARNING, "Unknown fid type " + tags.getValue(DatabaseTags.TYPE_TAG) + " for fid " + _fid + " titled \"" + tags.getValue(DatabaseTags.TITLE_TAG) + "\"");
    }

    return false;
  }

  private synchronized void rebuildDatabases(PlayerDatabase _playerDatabase, EmpegProtocolClient _protocolClient, IProgressListener _progressListener, boolean _goForBroke) throws ProtocolException {
    boolean rebuildFromMemory = PropertiesManager.getInstance().getBooleanProperty(JEmplodeProperties.REBUILD_FROM_MEMORY_KEY);
    int size = _playerDatabase.getSize();
    if (size > 0 && rebuildFromMemory && _goForBroke) {
      Debug.println(Debug.WARNING, "EmpegSynchronizeClient.rebuildDatabases: rebuild from memory");
      try {
        EmpegDatabaseRebuilder.rebuildDatabaseFromMemory(_playerDatabase, _protocolClient, _progressListener);
      }
      catch (Throwable e) {
        Debug.println(e);
        Debug.println(Debug.WARNING, "EmpegSynchronizeClient.rebuildDatabases: falling back to old way ...");
        ((EmpegProtocolClient) _protocolClient).rebuildPlayerDatabase(0);
      }
    }
    //    else if (rebuildOnPC) {
    //      try {
    //        EmpegDatabaseRebuilder.rebuildDatabase(_protocolClient, _progressListener);
    //      }
    //      catch (Throwable t) {
    //        Debug.println(t);
    //        Debug.println(Debug.WARNING, "EmpegSynchronizeClient.rebuildDatabases: falling back to old way ...");
    //        ((EmpegProtocolClient) _protocolClient).rebuildPlayerDatabase(0);
    //      }
    //    }
    else {
      ((EmpegProtocolClient) _protocolClient).rebuildPlayerDatabase(0);
    }
  }

  /**
   * Return possibly rebuild and redownload the database.
   * 
   * @param _rebuildOnFail
   *          should we rebuild on fail?
   * @param _doRebuild
   *          if so, should we do a rebuild?
   * @throws IOException
   *           if the database cannot be rebuilt
   */
  private Reason[] rebuildOnFailure(PlayerDatabase _playerDatabase, IProtocolClient _protocolClient, boolean _rebuildOnFail, IProgressListener _progressListener) throws SynchronizeException, ProtocolException {
    Reason[] reasons;
    // tags or database is missing?
    if (_rebuildOnFail) {
      _progressListener.taskStarted("Rebuilding databases");
      _progressListener.taskUpdated(0, 1);

      // Rebuild it!
      EmpegProtocolClient empegProtocolClient = (EmpegProtocolClient) _protocolClient;
      try {
        beforeSynchronize(_playerDatabase, _protocolClient, _progressListener);
        _progressListener.taskStarted("Rebuilding music database ...");
        rebuildDatabases(_playerDatabase, empegProtocolClient, _progressListener, false);
        computeFreeSpace(_playerDatabase, _protocolClient);
      }
      catch (Throwable e) {
        Debug.println(e);
      }
      finally {
        try {
          _protocolClient.unlock();
          _progressListener.taskStarted("Rebooting player ...");
          empegProtocolClient.restartUnit(false, true);
        }
        catch (Throwable e) {
          Debug.println(e);
        }
      }
      _progressListener.taskUpdated(1, 1);

      // start the whole process over
      reasons = download0(_playerDatabase, _protocolClient, false, _progressListener); // but dont rebuild on error
    }
    else {
      reasons = new Reason[0];
    }
    return reasons;
  }
}