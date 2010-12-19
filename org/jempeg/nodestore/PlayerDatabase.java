/**
 * This file is licensed under the GPL. See the LICENSE0 file included in this release, or http://www.opensource.org/licenses/gpl-license.html for the details of the license.
 */
package org.jempeg.nodestore;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.tree.TreePath;

import org.jempeg.filesystem.MusicImportFileFactory;
import org.jempeg.nodestore.event.IDatabaseListener;
import org.jempeg.nodestore.event.INodeTagListener;

import com.inzyme.container.IContainer;
import com.inzyme.filesystem.ImportFileFactory;
import com.inzyme.model.Reason;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.util.Debug;

/**
 * PlayerDatabase provides the in-memory representation of the contents of a Rio device.
 * 
 * @author Mike Schrag
 */
public class PlayerDatabase implements IContainer {
  static {
    ImportFileFactory.addImportFileFactory(new MusicImportFileFactory());
  }

  public static final String FREE_SPACE_PROPERTY = "jempeg.playerDatabase.freeSpace";

  private Hashtable myFileToNodeMap;
  private Vector myDatabaseListeners;
  private Vector myNodeTagListeners;

  private boolean myTrackFreeSpace;

  private DatabaseTags myTagNames;
  private FIDNodeMap myFIDs;

  private long myTotalSpace;
  private long myFreeSpace;

  private SynchronizeQueue mySynchronizeQueue;
  private IDeviceSettings myDeviceSettings;

  private boolean myCreateUnattachedItems;
  private boolean myNestedPlaylistAllowed;

  /**
   * Creates a new PlayerDatabase.
   * 
   * @param _client the ProtocolClient to communicate with
   */
  public PlayerDatabase() {
    myTrackFreeSpace = true;
    myNestedPlaylistAllowed = true;
    myFIDs = new FIDNodeMap();
    myTagNames = new DatabaseTags();
    myDatabaseListeners = new Vector();
    myNodeTagListeners = new Vector();
    mySynchronizeQueue = new SynchronizeQueue();
    myDeviceSettings = new BasicDeviceSettings();
    myFileToNodeMap = new Hashtable();
  }

  /**
   * Sets whether or not an Unattached Items playlist needs to be created for this device. Unattached items is only used by older devices like the Empeg to store tunes that aren't in playlists.
   * 
   * @param _createUnattachedItems whether or not to create unattached items
   */
  public void setCreateUnattachedItems(boolean _createUnattachedItems) {
    myCreateUnattachedItems = _createUnattachedItems;
  }

  /**
   * Returns whether or not this device allows nested playlists. This is only a cue to the UI.
   * 
   * @return whether or not this device allows nested playlists
   */
  public boolean isNestedPlaylistAllowed() {
    return myNestedPlaylistAllowed;
  }

  /**
   * Sets whether or not this device allows nested playlists. This is only a cue to the UI.
   * 
   * @param _nestedPlaylistAllowed whether or not this device allows nested playlists
   */
  public void setNestedPlaylistAllowed(boolean _nestedPlaylistAllowed) {
    myNestedPlaylistAllowed = _nestedPlaylistAllowed;
  }

  /**
   * Returns the current device settings.
   * 
   * @return the current device settings
   */
  public IDeviceSettings getDeviceSettings() {
    return myDeviceSettings;
  }

  /**
   * Sets the current device settings
   */
  public void setDeviceSettings(IDeviceSettings _deviceSettings) {
    myDeviceSettings = _deviceSettings;
  }

  /**
   * @see com.inzyme.model.IContainer#getName()
   */
  public String getName() {
    return "PlayerDatabase";
  }

  /**
   * @see com.inzyme.model.IContainer#getSize()
   */
  public int getSize() {
    return myFIDs.size();
  }

  /**
   * @see com.inzyme.model.IContainer#getValueAt(int)
   */
  public Object getValueAt(int _index) {
    IFIDNode node = myFIDs.getNodeAt(_index);
    return node;
  }

  /**
   * Clears the database.
   */
  public void clear() {
    myDeviceSettings = new BasicDeviceSettings();
    myTotalSpace = 0;
    myFreeSpace = 0;
    myTagNames.clear();
    myFIDs.clear();
    myFileToNodeMap.clear();
    myFIDs.reset();
    fireDatabaseCleared(this);
  }

  /**
   * Returns the SynchronizeQueue for this client.
   * 
   * @return the SynchronizeQueue for this client
   */
  public SynchronizeQueue getSynchronizeQueue() {
    return mySynchronizeQueue;
  }

  /**
   * Returns whether or not synchronization is required.
   * 
   * @return whether or not synchronization is required
   */
  public boolean isDirty() {
    return myDeviceSettings.isDirty() || !mySynchronizeQueue.isEmpty() || mySynchronizeQueue.isSynchronizingNow();
  }

  /**
   * Adds a DatabaseListener to this player database.
   * 
   * @param _listener the listener to add
   */
  public void addDatabaseListener(IDatabaseListener _listener) {
    myDatabaseListeners.addElement(_listener);
  }

  /**
   * Removes a DatabaseListener from this player database.
   * 
   * @param _listener the listener to remove
   */
  public void removeDatabaseListener(IDatabaseListener _listener) {
    myDatabaseListeners.removeElement(_listener);
  }

  /**
   * Adds a NodeTagListener to this player database.
   * 
   * @param _listener the listener to add
   */
  public void addNodeTagListener(INodeTagListener _listener) {
    myNodeTagListeners.addElement(_listener);
  }

  /**
   * Removes a NodeTagListener from this player database.
   * 
   * @param _listener the listener to remove
   */
  public void removeNodeTagListener(INodeTagListener _listener) {
    myNodeTagListeners.removeElement(_listener);
  }

  /**
   * Removes all DatabaseListeners and NodeListener from this player database.
   */
  public void removeAllListeners() {
    myDatabaseListeners.removeAllElements();
    myNodeTagListeners.removeAllElements();
  }

  /**
   * Returns the node that has the given FID.
   * 
   * @param _fid the FID to lookup
   * @return the node that has the given FID
   */
  public IFIDNode getNode(long _fid) {
    return myFIDs.getNode(_fid);
  }

  /**
   * Returns the FIDplaylist that has the given FID.
   * 
   * @param _fid the FID to lookup
   * @return the FIDplaylist that has the given FID
   */
  public FIDPlaylist getPlaylist(long _fid) {
    IFIDNode node = getNode(_fid);
    if (node instanceof FIDPlaylist) {
      return (FIDPlaylist) node;
    }
    else {
      return null;
    }
  }

  /**
   * Returns the DatabaseTags that are currently loaded. When you load a PlayerDatabase, all the tag names that are used are put into a single set that is referenced by all nodes.
   * 
   * @return the DatabaseTags that are currently loaded
   */
  public DatabaseTags getDatabaseTags() {
    return myTagNames;
  }

  /**
   * Returns the FIDNodeMap that contains all the nodes in this PlayerDatabase. If you want to just get an enumeration of nodes in this database you can call getNodeMap().elements()
   * 
   * @return the FIDNodeMap of all of this PlayerDatabase's nodes
   */
  public FIDNodeMap getNodeMap() {
    return myFIDs;
  }

  /**
   * Returns the root playlist from this PlayerDatabase. From the root playlist you can navigate the entire tree structure.
   * 
   * @return the root playlist from this PlayerDatabase
   */
  public FIDPlaylist getRootPlaylist() {
    FIDPlaylist rootPlaylist = (FIDPlaylist) getNode(FIDConstants.FID_ROOTPLAYLIST);
    if (rootPlaylist == null) {
      checkRootLists();
      rootPlaylist = (FIDPlaylist) getNode(FIDConstants.FID_ROOTPLAYLIST);
    }
    return rootPlaylist;
  }

  /**
   * Adds the given FID to this database.
   * 
   * @param _fid the FID to add
   * @param _node the node to add
   * @param _generateDatabaseChangeEvent whether or not to generate a database change event
   * @throws IOException if the node cannot be added
   */
  void addFID(long _fid, IFIDNode _node, boolean _generateDatabaseChangeEvent) {
    myFIDs.addNode(_fid, _node);
    if (_generateDatabaseChangeEvent) {
      if (_node instanceof FIDLocalFile) {
        mySynchronizeQueue.enqueue(new NodeAddedDatabaseChange((FIDLocalFile) _node));
      }
      else if (_node instanceof FIDPlaylist) {
        mySynchronizeQueue.enqueue(new PlaylistAddedDatabaseChange((FIDPlaylist) _node));
      }
      else {
        throw new IllegalArgumentException("Unable to add a database change event for " + _node + ".");
      }
    }
    fireNodeAdded(_node);
  }

  /**
   * Removes the given FID from this database.
   * 
   * @param _fid the FID to removed
   * @throws IOException if the node cannot be removed
   */
  boolean removeFID(long _fid) {
    IFIDNode node = myFIDs.removeNode(_fid);
    boolean existed = (node != null && !(node instanceof FIDStub));
    if (existed) {
      if (!node.isTransient()) {
        mySynchronizeQueue.enqueue(new NodeRemovedDatabaseChange(node));
      }
      // TODO: Should transient nodes fire events? I think maybe so
      fireNodeRemoved(node);
    }
    return existed;
  }

  /**
   * Fires a nodeAdded event to all listeners.
   */
  void fireNodeAdded(IFIDNode _node) {
    for (int i = myDatabaseListeners.size() - 1; i >= 0; i -- ) {
      IDatabaseListener listener = (IDatabaseListener) myDatabaseListeners.elementAt(i);
      listener.nodeAdded(_node);
    }
  }

  /**
   * Fires a nodeRemoved event to all listeners.
   */
  void fireNodeRemoved(IFIDNode _node) {
    for (int i = myDatabaseListeners.size() - 1; i >= 0; i -- ) {
      IDatabaseListener listener = (IDatabaseListener) myDatabaseListeners.elementAt(i);
      listener.nodeRemoved(_node);
    }
  }

  /**
   * Fires a freeSpaceChanged event to all listeners.
   */
  void fireFreeSpaceChanged(PlayerDatabase _playerDatabase, long _totalSpace, long _freeSpace) {
    for (int i = myDatabaseListeners.size() - 1; i >= 0; i -- ) {
      IDatabaseListener listener = (IDatabaseListener) myDatabaseListeners.elementAt(i);
      listener.freeSpaceChanged(_playerDatabase, _totalSpace, _freeSpace);
    }
  }

  /**
   * Fires a nodeAdded event to all listeners.
   */
  void fireDatabaseCleared(PlayerDatabase _playerDatabase) {
    for (int i = myDatabaseListeners.size() - 1; i >= 0; i -- ) {
      IDatabaseListener listener = (IDatabaseListener) myDatabaseListeners.elementAt(i);
      listener.databaseCleared(_playerDatabase);
    }
  }

  /**
   * Fires a nodeIdentified event to all listeners.
   */
  void fireNodeIdentified(IFIDNode _node) {
    for (int i = myNodeTagListeners.size() - 1; i >= 0; i -- ) {
      INodeTagListener listener = (INodeTagListener) myNodeTagListeners.elementAt(i);
      listener.nodeIdentified(_node);
    }
  }

  /**
   * Fires a beforeNodeTagModified event to all listeners.
   */
  void fireBeforeNodeTagsModified(IFIDNode _node, String _tag, String _oldValue, String _newValue) {
    for (int i = myNodeTagListeners.size() - 1; i >= 0; i -- ) {
      INodeTagListener listener = (INodeTagListener) myNodeTagListeners.elementAt(i);
      listener.beforeNodeTagModified(_node, _tag, _oldValue, _newValue);
    }
  }

  /**
   * Fires a afterNodeTagModified event to all listeners.
   */
  void fireAfterNodeTagsModified(IFIDNode _node, String _tag, String _oldValue, String _newValue) {
    for (int i = myNodeTagListeners.size() - 1; i >= 0; i -- ) {
      INodeTagListener listener = (INodeTagListener) myNodeTagListeners.elementAt(i);
      listener.afterNodeTagModified(_node, _tag, _oldValue, _newValue);
    }
  }

  /**
   * Creats a new root playlist.
   * 
   * @return a new root playlist
   * @throws IOException if the playlist cannot be created
   */
  protected FIDPlaylist createRootPlaylist() {
    NodeTags tags = new NodeTags();
    tags.setValue(DatabaseTags.TYPE_TAG, DatabaseTags.TYPE_PLAYLIST);
    tags.setValue(DatabaseTags.TITLE_TAG, ResourceBundleUtils.getUIString("rootPlaylist.title"));

    FIDPlaylist playlist = new FIDPlaylist(this, FIDConstants.FID_ROOTPLAYLIST, tags, false, true);
    playlist.setDirty(true); // update on sync
    return playlist;
  }

  /**
   * Creats a new unattached items playlist.
   * 
   * @return a new unattached items playlist
   * @throws IOException if the playlist cannot be created
   */
  protected FIDPlaylist createUnattachedItems() {
    NodeTags tags = new NodeTags();
    tags.setValue(DatabaseTags.TYPE_TAG, DatabaseTags.TYPE_PLAYLIST);
    tags.setValue(DatabaseTags.TITLE_TAG, ResourceBundleUtils.getUIString("unattachedItemsPlaylist.title"));

    FIDPlaylist playlist = new FIDPlaylist(this, FIDConstants.FID_UNATTACHED_ITEMS, tags, true, false);
    playlist.setDirty(false); // update on sync
    return playlist;
  }

  Hashtable getFileToNodeMap() {
    return myFileToNodeMap;
  }

  // Check root and unattached items playlists are valid, present and connected
  /**
   * Looks for the root playlist or creates on if it doesn't exist.
   * 
   * @returns the number of errors
   */
  public Reason checkRootLists() {
    Reason reason = null;

    // check root playlist
    IFIDNode rootNode = getNode(FIDConstants.FID_ROOTPLAYLIST); // dont do GetRootPlaylist() - dont cast
    if (rootNode == null) {
      //reason = new Reason("No root playlist, creating new one");
      rootNode = createRootPlaylist();
    }
    else if (!(rootNode instanceof FIDPlaylist)) {
      //reason = new Reason("Invalid root playlist, creating new one");
      rootNode.delete();
      rootNode = createRootPlaylist();
    }

    if (myCreateUnattachedItems) {
      // Make a fake unattached items node that doesn't get synced
      // just to be compatible with our friend Emplode
      IFIDNode unattachedItemsNode = getNode(FIDConstants.FID_UNATTACHED_ITEMS);
      if (unattachedItemsNode == null) {
        unattachedItemsNode = createUnattachedItems();
      }
      else if (!(unattachedItemsNode instanceof FIDPlaylist)) {
        unattachedItemsNode.delete();
        unattachedItemsNode = createUnattachedItems();
      }
      //			FIDPlaylist rootPlaylist = (FIDPlaylist)rootNode;
      //			if (!rootPlaylist.contains(unattachedItemsNode)) {
      //				rootPlaylist.addNode(unattachedItemsNode);
      //			}
    }

    // done!
    return reason;
  }

  /**
   * Checks this PlayerDatabase for problems and optionally repairs them. This also calculates reference counts on all nodes, so you <i>must call this after a call to download() or synchronize() to make sure the database is in a valid state </i>. It does not get automatically called since you really need to separate errors/warnings during the two processes (since download reasons are typically fatal errors but repair reasons are usually warnings from repairs).
   * 
   * @param _repair whether or not problems should be automatically fixed
   * @return the set of reasons that explain the problems that occurred
   * @throws IOException if PlayerDatabase cannot be checked for problems
   */
  public Reason[] checkForProblems0(boolean _repair) {
    // reset all references to zero to start with
    Vector reasonsVec = new Vector();

    Enumeration elements = myFIDs.elements();
    while (elements.hasMoreElements()) {
      IFIDNode node = (IFIDNode) elements.nextElement();
      node.clearParentPlaylists();
    }

    Debug.println(Debug.INFORMATIVE, "Checking root playlist...");

    // check the root playlist and unattached items (throw passed back to caller if it occurs)
    Reason rootPlaylistReason = checkRootLists();
    if (rootPlaylistReason != null) {
      reasonsVec.addElement(rootPlaylistReason);
      Debug.println(Debug.WARNING, rootPlaylistReason.getReason());
    }

    // recurse the tree of playlists checking for loops
    Debug.println(Debug.INFORMATIVE, "Starting tree->Repair() recursion...");

    FIDPlaylist rootPlaylist = getRootPlaylist();

    Reason[] reasons = rootPlaylist.checkForProblems(_repair, new TreePath(rootPlaylist));
    Reason.fromArray(reasons, reasonsVec);

    Debug.println(Debug.INFORMATIVE, "Completed tree->Repair() recursion...");

    Debug.println(Debug.INFORMATIVE, "Setting refcounts...");
    CollationKeyCache cache = CollationKeyCache.createDefaultCache();
    Enumeration fidsEnum = myFIDs.elements();
    while (fidsEnum.hasMoreElements()) {
      IFIDNode node = (IFIDNode) fidsEnum.nextElement();
      if (node instanceof FIDPlaylist) {
        FIDPlaylist playlist = (FIDPlaylist) node;
        if (!playlist.isTransient() && playlist.getReferenceCount() == 0) {
          rootPlaylist.addNode(playlist, false, cache);
          Reason[] zeroRefCountReasons = playlist.checkForProblems(true, new TreePath(playlist));
          Reason.fromArray(zeroRefCountReasons, reasonsVec);
          Debug.println(Debug.WARNING, "Playlist '" + playlist.getTitle() + " (fid = " + playlist.getFID() + ")' had a zero refcount.  Adding to root.");
          reasonsVec.addElement(new Reason(ResourceBundleUtils.getUIString("database.repair.zeroRefCountPlaylist", new Object[] {
            playlist.getTitle()
          })));
        }
      }
    }
    Debug.println(Debug.INFORMATIVE, "Completed setting refcounts...");
    Debug.println(Debug.INFORMATIVE, "Database checked for problems...");

    // done - return number of repairs that would be/have been done
    return Reason.toArray(reasonsVec);
  }

  /**
   * Sets the current total space and free space and fires.
   * 
   * @param _totalSpace the total space on the device
   * @param _freeSpace the free space on the device
   */
  public void setFreeSpace(long _totalSpace, long _freeSpace) {
    myTotalSpace = _totalSpace;
    myFreeSpace = _freeSpace;
    fireFreeSpaceChanged(this, myTotalSpace, myFreeSpace);
  }

  /**
   * Returns the total size in bytes of this Player.
   * 
   * @return the total size in bytes of this Player
   */
  public long getTotalSpace() {
    return myTotalSpace;
  }

  /**
   * Returns the total number of bytes that are not used.
   * 
   * @return the total number of bytes that are not used
   */
  public long getFreeSpace() {
    return myFreeSpace;
  }

  /**
   * Returns the total number of bytes that are used.
   * 
   * @return the total number of bytes that are used
   */
  public long getUsedSpace() {
    return myTotalSpace - myFreeSpace;
  }

  /**
   * Sets whether or not to track the available free space on the device that this database represents.
   * 
   * @param _trackFreeSpace whether or not to track available free space
   */
  public void setTrackFreeSpace(boolean _trackFreeSpace) {
    myTrackFreeSpace = _trackFreeSpace;
  }

  /**
   * Adjusts the amount of free space available by the size of the given node.
   * 
   * @param _node the node to use space for
   */
  void consumeSpace(IFIDNode _node) {
    adjustFreeSpace(_node, false);
  }

  /**
   * Checks for sufficient free space and throws an exception if there isn't enough
   */
  void checkFreeSpace(IFIDNode _node) {
    if (myTrackFreeSpace && _node.getLength() > myFreeSpace) {
      throw new RuntimeException(ResourceBundleUtils.getErrorString("insufficientSpace"));
    }
  }

  /**
   * Adjusts the amount of free space available by the size of the given node.
   * 
   * @param _node the node to reclaim
   */
  void freeSpace(IFIDNode _node) {
    adjustFreeSpace(_node, true);
  }

  /**
   * Adjusts the amount of free space available by the given amount. This amount can be positive or negative.
   * 
   * @param _size the amount to adjust the available free space counter
   */
  protected void adjustFreeSpace(IFIDNode _node, boolean _freeSpace) {
    if (myTrackFreeSpace) {
      long size = _node.getLength();
      if (_freeSpace) {
        myFreeSpace += size;
      }
      else {
        myFreeSpace -= size;
      }
      fireFreeSpaceChanged(this, myTotalSpace, myFreeSpace);
    }
  }
}