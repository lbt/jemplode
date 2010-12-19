/**
 * This file is licensed under the GPL. See the LICENSE0 file included in this release, or http://www.opensource.org/licenses/gpl-license.html for the details of the license.
 */
package org.jempeg.nodestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.tree.TreePath;

import org.jempeg.JEmplodeProperties;
import org.jempeg.nodestore.event.IPlaylistListener;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.model.TagValueRetriever;

import com.inzyme.container.CollationKeySortableContainer;
import com.inzyme.container.IMutableTypeContainer;
import com.inzyme.container.ISortableContainer;
import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.io.RefByteArrayOutputStream;
import com.inzyme.model.LongVector;
import com.inzyme.model.Reason;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.sort.IntQuickSort;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.text.StringUtils;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.util.Debug;

/**
 * FIDPlaylist represents a playlist on the Empeg. Playlists have a list of FID's that are contained within it.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.11 $
 */
public class FIDPlaylist extends AbstractFIDNode implements IMutableTypeContainer, ISortableContainer, IFIDPlaylistWrapper {
  static final long serialVersionUID = 8208806883442920321L;

  public static final String[] AGGREGATE_TAGS = new String[] {
      DatabaseTags.ARTIST_TAG,
      DatabaseTags.SOURCE_TAG,
      DatabaseTags.GENRE_TAG,
      DatabaseTags.YEAR_TAG
  };

  private static boolean SORT_PLAYLISTS_ABOVE_TUNES = PropertiesManager.getInstance().getBooleanProperty(JEmplodeProperties.SORT_PLAYLISTS_ABOVE_TUNES);

  public static void setSortPlaylistsAboveTunes(PlayerDatabase _playerDatabase, boolean _sortPlaylistsAboveTunes) {
    if (FIDPlaylist.SORT_PLAYLISTS_ABOVE_TUNES != _sortPlaylistsAboveTunes) {
      FIDPlaylist.SORT_PLAYLISTS_ABOVE_TUNES = _sortPlaylistsAboveTunes;
      if (_playerDatabase != null) {
        _playerDatabase.getRootPlaylist().resort(true);
      }
    }
  }

  private int myContainedType;
  private ArrayList myFIDs;
  private boolean myTransient;
  private boolean mySoup;
  private NodeTag mySortTag;
  private boolean mySortAscending = true;

  private transient List myPlaylistListeners;
  private transient boolean myReordering;

  /**
   * Creates a new FIDPlaylist.
   * 
   * @param _db the PlayerDatabase to create the FIDPlaylist within
   * @param _transient is this a transient playlist?
   */
  public FIDPlaylist(PlayerDatabase _db, boolean _transient) {
    this(_db, _transient, true);
  }

  /**
   * Creates a new FIDPlaylist.
   * 
   * @param _db the PlayerDatabase to create the FIDPlaylist within
   * @param _transient is this a transient playlist?
   */
  public FIDPlaylist(PlayerDatabase _db, boolean _transient, boolean _addToDatabase) {
    super(_db, _db.getNodeMap().findFree());
    init(_transient, null, true, true, _addToDatabase);
  }

  /**
   * Creates a new FIDPlaylist.
   * 
   * @param _db the PlayerDatabase to create the FIDPlaylist within
   * @param _fid the FID of this node
   * @param _tags the NodeTags for this Node
   * @param _transient is this a transient playlist?
   */
  public FIDPlaylist(PlayerDatabase _db, long _fid, NodeTags _tags, boolean _transient, boolean _generateDatabaseChangeEvent) {
    super(_db, _fid, _tags);
    init(_transient, null, false, _generateDatabaseChangeEvent, true);
    setIdentified(true);
  }

  /**
   * Creates a new FIDPlaylist.
   * 
   * @param _db the PlayerDatabase to create the FIDPlaylist within
   * @param _fid the FID of this node
   * @param _tags the NodeTags for this Node
   * @param _transient is this a transient playlist?
   */
  public FIDPlaylist(PlayerDatabase _db, long _fid, NodeTags _tags, boolean _transient, PlaylistPair[] _members) {
    super(_db, _fid, _tags);
    init(_transient, _members, false, false, true);
    setDirty(false);
    setIdentified(true);
  }

  /**
   * Initialize this Playlist
   */
  protected void init(boolean _transient, PlaylistPair[] _members, boolean _initializeGeneration, boolean _generateDatabaseChange, boolean _addToDatabase) {
    myTransient = _transient;
    myFIDs = new ArrayList();
    myContainedType = IFIDNode.TYPE_TUNE;
    if (_members != null) {
      populate(_members);
    }
    if (!_transient && _initializeGeneration) {
      initializeGeneration();
    }
    if (_addToDatabase) {
      addToDatabase(!_transient && _generateDatabaseChange);
    }
    setType(IFIDNode.TYPE_PLAYLIST);
    getTags().setValue(DatabaseTags.TYPE_TAG, DatabaseTags.TYPE_PLAYLIST);
    String sortTag = getTags().getValue(DatabaseTags.SORT_TAG);
    if (sortTag.length() > 0) {
      setSortTag(NodeTag.getNodeTag(sortTag), false);
    }
  }

  protected boolean isAddedToDatabase() {
    return true;
  }

  /**
   * Adds a PlaylistListener to this player database.
   * 
   * @param _listener the listener to add
   */
  public synchronized void addPlaylistListener(IPlaylistListener _listener) {
    if (myPlaylistListeners == null) {
      myPlaylistListeners = new ArrayList();
    }
    myPlaylistListeners.add(_listener);
  }

  /**
   * Removes a PlaylistListener from a specific playlist.
   * 
   * @param _listener the listener to remove
   */
  public void removePlaylistListener(IPlaylistListener _listener) {
    if (myPlaylistListeners != null) {
      myPlaylistListeners.remove(_listener);
    }
  }

  public void setSortTag(NodeTag _sortTag, boolean _resortIfNecessary) {
    if (_sortTag != null && _sortTag.getName().length() == 0) {
      _sortTag = null;
    }
    if (mySortTag != _sortTag) {
      mySortTag = _sortTag;
      if (_resortIfNecessary) {
        resort(false);
      }
    }
  }

  public NodeTag getSortTag() {
    return mySortTag;
  }

  public void afterNodeTagModified(IFIDNode _node, String _tag, String _oldValue, String _newValue) {
    super.afterNodeTagModified(_node, _tag, _oldValue, _newValue);
    if (isIdentified() && DatabaseTags.SORT_TAG.equals(_tag)) {
      setSortTag(NodeTag.getNodeTag(_newValue), true);
    }
  }

  /**
   * Fires a playlistNodeInserted event to all listeners.
   */
  void firePlaylistStructureChanged() {
    if (!isTransient()) {
      getDB().getSynchronizeQueue().enqueue(new PlaylistMembersDatabaseChange(this));
    }
    if (myPlaylistListeners != null) {
      for (int i = myPlaylistListeners.size() - 1; i >= 0; i -- ) {
        IPlaylistListener listener = (IPlaylistListener) myPlaylistListeners.get(i);
        listener.playlistStructureChanged(this);
      }
    }
  }

  /**
   * Fires a playlistNodeInserted event to all listeners.
   */
  void firePlaylistNodeInserted(IFIDNode _childNode, int _index) {
    if (!isTransient()) {
      getDB().getSynchronizeQueue().enqueue(new PlaylistMembersDatabaseChange(this));
    }
    if (myPlaylistListeners != null) {
      for (int i = myPlaylistListeners.size() - 1; i >= 0; i -- ) {
        IPlaylistListener listener = (IPlaylistListener) myPlaylistListeners.get(i);
        listener.playlistNodeInserted(this, _childNode, _index);
      }
    }
  }

  /**
   * Fires a playlistNodeRemoved event to all listeners.
   */
  void firePlaylistNodeRemoved(IFIDNode _childNode, int _index) {
    if (!isTransient()) {
      getDB().getSynchronizeQueue().enqueue(new PlaylistMembersDatabaseChange(this));
    }
    if (myPlaylistListeners != null) {
      for (int i = myPlaylistListeners.size() - 1; i >= 0; i -- ) {
        IPlaylistListener listener = (IPlaylistListener) myPlaylistListeners.get(i);
        listener.playlistNodeRemoved(this, _childNode, _index);
      }
    }
  }

  /**
   * Sets the type constant of the nodes that will be contained in this playlist. The type is one of IFIDNode.TYPE_xxx
   * 
   * @param _containedType the type constant of what will be inside of this playlist
   */
  public void setContainedType(int _containedType) {
    myContainedType = _containedType;
  }

  /**
   * Returns the type constant of the nodes that this playlist contains so that the UI may render the playlist differently based on its contained type (for instance, column headers change based on the contained type). The type is one of IFIDNode.TYPE_xxx.
   * 
   * @return the type constant of the nodes inside of this playlist
   */
  public int getContainedType() {
    return myContainedType;
  }

  /**
   * Returns whether or not this playlist is a soup playlist.
   * 
   * @return whether or not this is a soup playlist
   */
  public boolean isSoup() {
    return mySoup;
  }

  /**
   * Sets whether or not this is a soup playlist.
   * 
   * @param _soup whether or not this is a soup playlist
   */
  public void setSoup(boolean _soup) {
    // If this playlist is becoming unsoupy, we are supposed to notify
    // all the children to fire REFS_TAG update events, but this is pretty
    // damn rare
    mySoup = _soup;
  }

  /**
   * Returns whether or not this is a transient playlist. A transient playlist is one that will not ever be downloaded to the Empeg.
   * 
   * @return whether or not this playlist is transient
   */
  public boolean isTransient() {
    return myTransient;
  }

  /**
   * Sets whether or not this playlist is transient.
   * 
   * @param _transient whether or not this playlist is transient
   */
  public void setTransient(boolean _transient) {
    myTransient = _transient;
  }

  /**
   * @see org.jempeg.empeg.model.IFIDNode#getAttributes(boolean)
   */
  public int getAttributes(boolean _recursive) {
    int attributes = super.getAttributes(_recursive);
    if (_recursive) {
      int size = getSize();
      for (int i = 0; i < size; i ++ ) {
        IFIDNode node = getNodeAt(i);
        if (node != null) {
          attributes |= node.getAttributes(_recursive);
        }
      }
    }
    else {
      attributes &= ~(IFIDNode.ATTRIBUTE_DIRTY);
    }
    return attributes;
  }

  /**
   * Sorts this playlist.
   * 
   * @param _tagName the tag name to sort on
   * @param _ascending sorting ascending or descending
   */
  public synchronized void sortBy(NodeTag _sortNodeTag, boolean _sortAscending) {
    if (_sortNodeTag != null && (mySortTag == null || (mySortTag.equals(_sortNodeTag) && _sortAscending))) {
      boolean oldSortAscending = mySortAscending;
      NodeTag oldSortTag = mySortTag;
      ArrayList oldFIDs = myFIDs;
      try {
        myReordering = true;
        mySortAscending = _sortAscending;
        mySortTag = _sortNodeTag;
        if (getSize() > 0) {
          myFIDs = new ArrayList();

          PlayerDatabase db = getDB();
          int size = oldFIDs.size();
          CollationKeyCache cache = CollationKeyCache.createDefaultCache();
          for (int i = 0; i < size; i ++ ) {
            PlaylistPair playlistPair = (PlaylistPair) oldFIDs.get(i);
            IFIDNode node = db.getNode(playlistPair.getFID());
            addNode(node, false, cache);
          }

          setDirty(true);
          firePlaylistStructureChanged();
        }
      }
      catch (RuntimeException t) {
        myFIDs = oldFIDs;
        throw t;
      }
      finally {
        myReordering = false;
        mySortAscending = oldSortAscending;
        mySortTag = oldSortTag;
      }
    }
  }

  public synchronized void resort(boolean _recursive) {
    if (mySortTag != null) {
      sortBy(mySortTag, true);
    }

    if (_recursive) {
      int size = getSize();
      for (int i = 0; i < size; i ++ ) {
        FIDPlaylist playlist = getPlaylistAt(i);
        if (playlist != null) {
          playlist.resort(_recursive);
        }
      }
    }
  }

  /**
   * Repositions the given indexes by a certain distance.
   * 
   * @param _indexes the indexes to reposition
   * @param _distance the distance to move them (can be positive or negative)
   * @return the new indexes after being repositioned
   */
  public synchronized int[] reposition(int[] _indexes, int _distance) {
    // You can't reposition a sorted playlist ... Sorry dude.
    if (mySortTag != null) {
      _distance = 0;
    }

    int[] newIndexes;
    if (_indexes.length > 0 && _distance != 0) {
      newIndexes = new int[_indexes.length];
      IntQuickSort quickSort = new IntQuickSort();
      quickSort.sort(_indexes);

      if (_distance < 0) {
        _distance = Math.max(-_indexes[0], _distance);
      }
      else {
        _distance = Math.min(getSize() - _indexes[_indexes.length - 1] - 1, _distance);
      }

      if (_distance != 0) {
        if (_distance < 0) {
          for (int i = 0; i < _indexes.length; i ++ ) {
            int index = _indexes[i];
            newIndexes[i] = index + _distance;
            PlaylistPair playlistPair = (PlaylistPair) myFIDs.get(index);
            myFIDs.remove(index);
            myFIDs.add(newIndexes[i], playlistPair);
          }
        }
        else {
          for (int i = _indexes.length - 1; i >= 0; i -- ) {
            int index = _indexes[i];
            newIndexes[i] = index + _distance;
            PlaylistPair playlistPair = (PlaylistPair) myFIDs.get(index);
            myFIDs.remove(index);
            myFIDs.add(newIndexes[i], playlistPair);
          }
        }

        setDirty(true);
        firePlaylistStructureChanged();
      }
      else {
        newIndexes = _indexes;
      }
    }
    else {
      newIndexes = _indexes;
    }
    return newIndexes;
  }

  /**
   * Returns the number of children of this playlist.
   * 
   * @return the number of children of this playlist
   */
  public int size() {
    if (myFIDs == null) {
      return -1;
    }
    else {
      return myFIDs.size();
    }
  }

  /**
   * Returns the number of tracks in all the children of this playlist.
   */
  public int getTrackCount() {
    int trackCount = 0;
    int size = size();
    for (int i = 0; i < size; i ++ ) {
      IFIDNode child = getNodeAt(i);
      if (child instanceof FIDPlaylist) {
        trackCount += ((FIDPlaylist) child).getTrackCount();
      }
      else {
        trackCount ++;
      }
    }
    return trackCount;
  }

  /**
   * Returns the FID at the given index.
   * 
   * @param _pos the index to lookup
   * @return the FID at the given index
   */
  public long getFIDAt(int _pos) {
    PlaylistPair fid = getPlaylistPairAt(_pos);
    return fid.getFID();
  }

  /**
   * Returns the Playlist Pair at the given index.
   * 
   * @param _pos the index to lookup
   * @return the Playlist Pair at the given index
   */
  public PlaylistPair getPlaylistPairAt(int _pos) {
    PlaylistPair pair = (PlaylistPair) myFIDs.get(_pos);
    return pair;
  }

  /**
   * Returns the FIDNode at the given index.
   * 
   * @param _pos the index to lookup
   * @return the FIDNode at the given index
   */
  public IFIDNode getNodeAt(int _pos) {
    return getDB().getNode(getFIDAt(_pos));
  }

  /**
   * Returns all the nodes of this playlist as an array.
   * 
   * @return all the nodes of this playlist as an array
   */
  public synchronized IFIDNode[] toArray() {
    IFIDNode[] nodes = new IFIDNode[getSize()];
    for (int i = 0; i < nodes.length; i ++ ) {
      nodes[i] = getNodeAt(i);
    }
    return nodes;
  }

  /**
   * Returns all the playlist pairs of this playlist as an array.
   * 
   * @return all the playlist pairs of this playlist as an array
   */
  public synchronized PlaylistPair[] getPlaylistPairs() {
    PlaylistPair[] playlistPairs = new PlaylistPair[getSize()];
    myFIDs.toArray(playlistPairs);
    return playlistPairs;
  }

  /**
   * Returns the FIDPlaylist at the given index (or null if the node at the index is node a Playlist).
   * 
   * @param _pos the index to lookup
   * @return the FIDPlaylist at the given index
   */
  public FIDPlaylist getPlaylistAt(int _pos) {
    IFIDNode node = getNodeAt(_pos);
    if (node instanceof FIDPlaylist) {
      return (FIDPlaylist) node;
    }
    else {
      return null;
    }
  }

  /**
   * Returns whether or not this playlist contains the given node.
   * 
   * @param _node the node to lookup
   * @return whether or not this playlist contains the given node
   */
  public boolean contains(IFIDNode _node) {
    boolean contains = myFIDs.contains(new PlaylistPair(_node.getFID(), -1));
    return contains;
  }

  /**
   * Returns the first index of the given node.
   * 
   * @param _node the node to lookup
   * @return the first index of the given node
   */
  public int getIndexOf(IFIDNode _node) {
    long fid = _node.getFID();
    int index = myFIDs.indexOf(new PlaylistPair(fid, -1));
    return index;
  }

  public int getIndexOf(IFIDNode _node, CollationKeyCache _cache) {
    CollationKeySortableContainer cacheContainer = new CollationKeySortableContainer(_cache, this);
    int matchIndex = FIDPlaylist.binarySearch(cacheContainer, mySortTag, mySortAscending, _node);
    if (matchIndex >= 0) {
      IFIDNode matchingNode = getNodeAt(matchIndex);
      if (!matchingNode.equals(_node)) {
        int size = myFIDs.size();
        int foundIndex = -1;

        if (size > 0) {
          Comparable nodeKey = (Comparable) cacheContainer.getSortValue(mySortTag, _node);
          long fid = _node.getFID();
          boolean done = false;
          int lowIndex = matchIndex - 1;
          int highIndex = matchIndex + 1;
          while (foundIndex == -1 && !done) {
            done = true;
            if (lowIndex >= 0) {
              if (getFIDAt(lowIndex) == fid) {
                foundIndex = lowIndex;
              }
              else if (nodeKey.compareTo(cacheContainer.getSortValueAt(mySortTag, lowIndex)) != 0) {
                lowIndex = -1;
                done = false;
              }
              else {
                lowIndex --;
                done = false;
              }
            }
            if (foundIndex == -1 && highIndex < size) {
              if (getFIDAt(highIndex) == fid) {
                foundIndex = highIndex;
              }
              else if (nodeKey.compareTo(cacheContainer.getSortValueAt(mySortTag, highIndex)) != 0) {
                highIndex = Integer.MAX_VALUE;
                done = false;
              }
              else {
                highIndex ++;
                done = false;
              }
            }
          }
        }

        if (foundIndex != -1) {
          matchIndex = foundIndex;
        }
        else {
          matchIndex = -(matchIndex + 1);
        }
      }
    }
    return matchIndex;
  }

  /**
   * For devices that don't have hierarchical playlists, it's desirable to recursively flatten a playlist and add its children rather than the playlist itself.
   * 
   * @param child the node to add
   * @param _addPlaylistChildrenInsteadOfPlaylist if true, _child will be flattened; otherwise this will call addNode(_child)
   */
  public synchronized int addPlaylistChildren(FIDPlaylist _childPlaylist, CollationKeyCache _cache) {
    int firstIndex = -1;
    int size = _childPlaylist.size();
    for (int i = 0; i < size; i ++ ) {
      int index;
      IFIDNode childNode = _childPlaylist.getNodeAt(i);
      if (childNode instanceof FIDPlaylist) {
        index = addPlaylistChildren((FIDPlaylist) childNode, _cache);
      }
      else {
        index = addNode(childNode, false, _cache);
      }
      if (firstIndex == -1) {
        firstIndex = index;
      }
    }
    return firstIndex;
  }

  /**
   * Adds a node to the end of this playlist.
   * 
   * @param _node the node to add
   * @return the index that this node was added to
   */
  public synchronized int addNode(IFIDNode _node, boolean _checkForLoops, CollationKeyCache _cache) {
    int index;
    if (mySortTag != null) {
      CollationKeySortableContainer cacheContainer = new CollationKeySortableContainer(_cache, this);
      index = FIDPlaylist.binarySearch(cacheContainer, mySortTag, mySortAscending, _node);
      if (index < 0) {
        index = (index + 1) * -1;
      }
    }
    else {
      index = myFIDs.size();
    }
    insertNodeAt(_node, index, _checkForLoops);
    return index;
  }

  /**
   * Recursively turn a transient playlist into a dirty persistent playlist.
   * 
   * @param _transientPlaylist the transient playliet
   */
  protected void makePersistent(FIDPlaylist _transientPlaylist) {
    if (_transientPlaylist.isTransient()) {
      _transientPlaylist.setTransient(false);
      _transientPlaylist.setDirty(true);

      int size = _transientPlaylist.getSize();
      for (int i = 0; i < size; i ++ ) {
        IFIDNode childNode = _transientPlaylist.getNodeAt(i);
        if (childNode instanceof FIDPlaylist) {
          FIDPlaylist childPlaylist = (FIDPlaylist) childNode;
          makePersistent(childPlaylist);
        }
      }

      firePlaylistStructureChanged();
    }
  }

  /**
   * Recursively turn this playlist into a soup playlist.
   */
  public void makeSoupy() {
    makeSoupy(this);
  }

  /**
   * Recursively turn a playlist into a soup playlist.
   * 
   * @param _playlist the playliet
   */
  protected void makeSoupy(FIDPlaylist _playlist) {
    _playlist.setSoup(true);

    int size = _playlist.getSize();
    for (int i = 0; i < size; i ++ ) {
      FIDPlaylist childPlaylist = _playlist.getPlaylistAt(i);
      if (childPlaylist != null) {
        makeSoupy(childPlaylist);
      }
    }
  }

  /**
   * Inserts a node into this playlist.
   * 
   * @param _node the node to insert
   * @param _index the index to insert at
   */
  public synchronized void insertNodeAt(IFIDNode _node, int _index, boolean _checkForLoops) {
    PlayerDatabase db = getDB();
    long fid = _node.getFID();

    // What is this for?
    if (!db.getNode(fid).isMarkedForDeletion()) {
      // You can't add a transient node to a non-transient node
      if (_node instanceof FIDPlaylist) {
        FIDPlaylist playlist = (FIDPlaylist) _node;
        if (_node.isTransient() && !isTransient()) {
          makePersistent(playlist);
        }

        if (_checkForLoops) {
          Set ancestorsSet = getAncestors();
          FIDPlaylist loop = playlist.getLoop(ancestorsSet);
          if (loop != null) {
            Debug.handleError("You can't add " + _node.getTitle() + " to " + this.getTitle() + " because it creates a loop.", true);
            return;
          }
        }
      }

      int insertIndex;
      if (_index == -1) {
        insertIndex = myFIDs.size();
      }
      else {
        insertIndex = _index;
      }

      myFIDs.add(insertIndex, new PlaylistPair(_node));
      if (!myReordering) {
        setDirty(true);
        _node.nodeAddedToPlaylist(this);
        updateLength();

        firePlaylistNodeInserted(_node, insertIndex);

        copyAggregateTags(getTags(), _node.getTags(), true);
      }
    }
  }

  private FIDPlaylist getLoop(Set _ancestorsSet) {
    FIDPlaylist loop = null;
    if (_ancestorsSet.contains(this)) {
      loop = this;
    }
    else {
      int size = getSize();
      for (int i = 0; loop == null && i < size; i ++ ) {
        FIDPlaylist childPlaylist = getPlaylistAt(i);
        if (childPlaylist != null) {
          loop = childPlaylist.getLoop(_ancestorsSet);
        }
      }
    }
    return loop;
  }

  /**
   * Removes all nodes with the given FID from this playlist.
   * 
   * @param _node the node to remove
   */
  public synchronized boolean removeNode(IFIDNode _node) {
    boolean nodeRemoved = removeFID(_node, -1, true);
    return nodeRemoved;
  }

  /**
   * Removes a node from this playlist.
   * 
   * @param _pos the index of the node to remove
   */
  public synchronized void removeNodeAt(int _pos) {
    removeFID(null, _pos, true);
  }

  public synchronized Reason[] checkForProblems(boolean _repair, TreePath _path) {
    List reasonsVec = new LinkedList();

    PlayerDatabase db = getDB();

    // recurse through every FID in this playlist
    int i = 0;
    while (i < myFIDs.size()) {
      PlaylistPair pair = getPlaylistPairAt(i);
      long curFID = pair.getFID();
      IFIDNode node = db.getNode(curFID);
      if (node == null) {
        Debug.println(Debug.WARNING, this.getTitle() + " (fid = " + this.getFID() + ") referred to a missing tune or playlist with ID " + curFID);
        //				Reason reason = new Reason(this.getTitle() + " referred to a missing tune or playlist with ID " + curFID);
        //				reasonsVec.addElement(reason);

        if (_repair) {
          // remove reference to offending FID
          removeFID(null, i, false);
        }
        else {
          i ++;
        }
      }
      else {
        // add a reference to this FID
        node.nodeAddedToPlaylist(this);

        if (pair.isStale(node)) {
          removeFID(null, i, false);
        }
        else {
          boolean moveToNextNode = true;

          // Is this a playlist? If so, we need to check loops and recurse it
          if (node instanceof FIDPlaylist) {
            FIDPlaylist playlist = (FIDPlaylist) node;

            FIDPlaylist loopParentPlaylist = null;
            int pathCount = _path.getPathCount();
            for (int pathNum = 0; loopParentPlaylist == null && pathNum < pathCount; pathNum ++ ) {
              FIDPlaylist pathPlaylist = (FIDPlaylist) _path.getPathComponent(pathNum);
              if (pathPlaylist.equals(playlist)) {
                loopParentPlaylist = pathPlaylist;
              }
            }

            if (loopParentPlaylist != null) {
              reasonsVec.add(new Reason("Removed a loop created by " + playlist.getTitle() + " inside of " + this.getTitle() + "."));
              removeFID(null, i, false);
              moveToNextNode = false;
            }
            // only recurse through a playlist if nobody else has already
            else if (playlist.getReferenceCount() == 1) {
              Reason[] reasons = playlist.checkForProblems(_repair, _path.pathByAddingChild(playlist));
              Reason.fromArray(reasons, reasonsVec);
            }
          }

          if (moveToNextNode) {
            i ++;
          }
        }
      }
    }

    return Reason.toArray(reasonsVec);
  }

  public synchronized boolean refreshAggregateTags(IFIDNode _becauseOf, boolean _resort) {
    boolean tagValuesChanged;
    if (areAggregateTagsDifferent(_becauseOf)) {
      String variousTagValue = NodeTag.getVariousTagValue();
      NodeTags newPlaylistTags = new NodeTags();

      boolean done = false;
      int size = getSize();
      for (int i = 0; !done && i < size; i ++ ) {
        IFIDNode node = getNodeAt(i);
        NodeTags nodeTags = node.getTags();
        boolean newTagValuesChanged = FIDPlaylist.copyAggregateTags(newPlaylistTags, nodeTags, (i > 0));

        // If all the aggregate tags have turned into "Various", we can break out, because it can't
        // change to anything else after that.
        if (newTagValuesChanged) {
          done = true;
          for (int aggregateTagNum = 0; done && aggregateTagNum < FIDPlaylist.AGGREGATE_TAGS.length; aggregateTagNum ++ ) {
            String newPlaylistTagValue = newPlaylistTags.getValue(FIDPlaylist.AGGREGATE_TAGS[aggregateTagNum]);
            done = newPlaylistTagValue.equals(variousTagValue);
          }
        }
      }

      tagValuesChanged = copyAggregateTags(newPlaylistTags, false);
      if (_resort && mySortTag != null) {
        int currentIndex = getIndexOf(_becauseOf);
        Object obj = myFIDs.remove(currentIndex);

        CollationKeySortableContainer cacheContainer = new CollationKeySortableContainer(CollationKeyCache.createDefaultCache(), this);
        int newIndex = FIDPlaylist.binarySearch(cacheContainer, mySortTag, mySortAscending, mySortTag.getValue(_becauseOf));
        if (newIndex < 0) {
          newIndex = (newIndex + 1) * -1;
        }
        if (newIndex == currentIndex) {
          myFIDs.add(currentIndex, obj);
        }
        else {
          myFIDs.add(newIndex, obj);
          setDirty(true);
          firePlaylistNodeRemoved(_becauseOf, currentIndex);
          firePlaylistNodeInserted(_becauseOf, newIndex);
        }
      }
    }
    else {
      tagValuesChanged = false;
    }
    return tagValuesChanged;
  }

  private boolean copyAggregateTags(NodeTags _becauseOfTags, boolean _aggregate) {
    NodeTags playlistTags = getTags();
    boolean tagValuesChanged = FIDPlaylist.copyAggregateTags(playlistTags, _becauseOfTags, _aggregate);
    return tagValuesChanged;
  }

  private boolean areAggregateTagsDifferent(IFIDNode _node) {
    NodeTags nodeTags = _node.getTags();
    NodeTags playlistTags = getTags();
    boolean tagValuesDifferent = false;
    for (int i = 0; !tagValuesDifferent && i < FIDPlaylist.AGGREGATE_TAGS.length; i ++ ) {
      String playlistTagValue = playlistTags.getValue(FIDPlaylist.AGGREGATE_TAGS[i]);
      String nodeTagValue = nodeTags.getValue(FIDPlaylist.AGGREGATE_TAGS[i]);
      tagValuesDifferent = !nodeTagValue.equals(playlistTagValue);
    }
    return tagValuesDifferent;
  }

  private static boolean copyAggregateTags(NodeTags _playlistTags, NodeTags _becauseOfTags, boolean _aggregate) {
    String variousTagValue = NodeTag.getVariousTagValue();

    boolean tagValuesChanged = false;
    for (int i = 0; i < FIDPlaylist.AGGREGATE_TAGS.length; i ++ ) {
      String tagName = FIDPlaylist.AGGREGATE_TAGS[i];

      boolean tagValueChanged = false;
      String playlistTagValue = _playlistTags.getValue(tagName);
      if (!_aggregate || !playlistTagValue.equals(variousTagValue)) {
        String newTagValue = _becauseOfTags.getValue(tagName);
        if (!_aggregate) {// || playlistTagValue.length() == 0) {
          tagValueChanged = true;
          _playlistTags.setValue(tagName, newTagValue);
        }
        else if (!playlistTagValue.equals(newTagValue)) {
          tagValueChanged = true;
          _playlistTags.setValue(tagName, variousTagValue);
        }
      }

      tagValuesChanged |= tagValueChanged;
    }
    return tagValuesChanged;
  }

  public static boolean isAggregateTag(String _tagName) {
    boolean isAggregateTag = false;
    for (int i = 0; !isAggregateTag && i < FIDPlaylist.AGGREGATE_TAGS.length; i ++ ) {
      isAggregateTag = FIDPlaylist.AGGREGATE_TAGS[i].equals(_tagName);
    }
    return isAggregateTag;
  }

  /**
   * Create a new playlist and insert it into this playlist.
   * 
   * @param _name the name of the playlist to create
   * @param _index the index to create the new playlist
   * @param _type the type of this playlist
   */
  public synchronized int getPlaylistIndex(String _name, int _type, int _containedType, IFIDNode _becauseOf, boolean _allowDuplicateNames, boolean _createIfMissing, CollationKeyCache _cache) {
    int playlistIndex = -1;

    boolean createPlaylist = _createIfMissing;
    if (!_allowDuplicateNames) {
      CollationKeySortableContainer cacheContainer = new CollationKeySortableContainer(_cache, this);
      if (mySortTag == NodeTag.TITLE_TAG) {
        playlistIndex = FIDPlaylist.binarySearch(cacheContainer, NodeTag.TITLE_TAG, mySortAscending, _name);
      }
      else {
        playlistIndex = FIDPlaylist.linearSearch(cacheContainer, NodeTag.TITLE_TAG, mySortAscending, _name);
      }

      if (playlistIndex < 0) {
        if (mySortTag == NodeTag.TITLE_TAG) {
          playlistIndex = (playlistIndex + 1) * -1;
        }
      }
      else {
        createPlaylist = false;
      }
    }

    if (createPlaylist) {
      FIDPlaylist newChildPlaylist = new FIDPlaylist(getDB(), myTransient);
      newChildPlaylist.getTags().setValue(DatabaseTags.TITLE_TAG, _name);
      newChildPlaylist.updateLength();
      newChildPlaylist.setDirty(true);
      newChildPlaylist.setType(_type);
      newChildPlaylist.setContainedType(_containedType);

      boolean isSoup = isSoup();
      newChildPlaylist.setSoup(isSoup);

      if (!isSoup) {
        NodeTag sortTag = getSortTag();
        if (sortTag != null) {
          newChildPlaylist.getTags().setValue(DatabaseTags.SORT_TAG, sortTag.getName());
          newChildPlaylist.setSortTag(sortTag, false);
        }
      }

      if (_becauseOf != null) {
        newChildPlaylist.copyAggregateTags(_becauseOf.getTags(), false);
      }

      if (playlistIndex < 0) {
        playlistIndex = addNode(newChildPlaylist, false, _cache);
      }
      else {
        insertNodeAt(newChildPlaylist, playlistIndex, false);
      }
      newChildPlaylist.setIdentified(true);
    }

    return playlistIndex;
  }

  /**
   * Create a new playlist and add it to the end of this playlist (with type TYPE_PLAYLIST)
   * 
   * @param _name the name of the playlist to create
   * @return the new playlist index
   */
  public synchronized int getPlaylistIndex(String _name, boolean _allowDuplicateNames, boolean _createIfMissing, CollationKeyCache _cache) {
    int playlistIndex = getPlaylistIndex(_name, IFIDNode.TYPE_PLAYLIST, myContainedType, null, _allowDuplicateNames, _createIfMissing, _cache);
    return playlistIndex;
  }

  public synchronized int createPlaylist(String _name) {
    return getPlaylistIndex(_name, true, true, CollationKeyCache.createDefaultCache());
  }

  public void nodeRemovedFromPlaylist(FIDPlaylist _playlist) {
    super.nodeRemovedFromPlaylist(_playlist);

    if (getReferenceCount() == 0) {
      int size = size();
      for (int i = size - 1; i >= 0; i -- ) {
        removeFID(null, i, false);
      }
      delete();
    }
  }

  public Object getSortValue(NodeTag _sortOnNodeTag, Object _value) {
    Object sortValue;
    if (_value instanceof IFIDNode) {
      sortValue = TagValueRetriever.getValue((IFIDNode) _value, _sortOnNodeTag.getName());
    }
    else {
      sortValue = _value;
    }
    return sortValue;
  }

  public Object getSortValueAt(NodeTag _sortOnNodeTag, int _index) {
    Object sortValue = getSortValue(_sortOnNodeTag, getNodeAt(_index));
    return sortValue;
  }

  public String getName() {
    return getTitle();
  }

  /**
   * @see org.jempeg.empeg.model.IContainer#getSize()
   */
  public int getSize() {
    return size();
  }

  /**
   * @see org.jempeg.empeg.model.IContainer#getValueAt(int)
   */
  public Object getValueAt(int _index) {
    return getNodeAt(_index);
  }

  /**
   * @see org.jempeg.empeg.model.IFIDPlaylistWrapper#getPlaylist()
   */
  public FIDPlaylist getPlaylist() {
    return this;
  }

  public String toVerboseString(int _depth) {
    StringBuffer sb = new StringBuffer();
    StringUtils.repeat(sb, "  ", _depth);
    sb.append("[FIDPlaylist: ");
    sb.append(getTitle());
    sb.append("; fid = ");
    sb.append(getFID());
    sb.append("; childCount = ");
    sb.append(size());
    sb.append("; children = ");
    for (int i = 0; i < size(); i ++ ) {
      sb.append("\n");
      sb.append(getNodeAt(i).toVerboseString(_depth + 1));
    }
    if (size() > 0) {
      sb.append("\n");
      StringUtils.repeat(sb, "  ", _depth);
    }
    sb.append("]");
    return sb.toString();
  }

  public String toString() {
    return "[FIDPlaylist: " + getTitle() + "; fid = " + getFID() + "; childCount = " + size() + "]";
  }

  /**
   * Sets the FIDs for this playlist and updates the playlist length.
   * 
   * @param _fids the FIDs to put in this playlist
   */
  public void populate(PlaylistPair[] _fids) {
    myFIDs.clear();
    myFIDs.ensureCapacity(_fids.length);
    for (int i = 0; i < _fids.length; i ++ ) {
      myFIDs.add(_fids[i]);
    }
    updateLength();
  }

  /**
   * Returns a byte array that represents this playlist in the Protocol 1 (Empeg) format.
   * 
   * @return a byte array in Protocol 1 format
   */
  public byte[] toProtocol1Format() {
    try {
      int childCount = size();

      // need the length of the playlist
      RefByteArrayOutputStream baos = new RefByteArrayOutputStream(childCount * 4);
      LittleEndianOutputStream eos = new LittleEndianOutputStream(baos);

      // transfer the FIDs list to the long buffer
      // yes, I know you can do this in one line of STL with raw iterators, but I can't be bothered
      for (int i = 0; i < childCount; i ++ ) {
        long fid = getFIDAt(i);
        eos.writeUnsigned32(fid);
      }
      return baos.getByteArray();
    }
    catch (IOException e) {
      throw new ChainedRuntimeException("Unable to create Protocol 1 format playlist.", e);
    }
  }

  /**
   * Check a playlist for loops -- recursive function Note: using a throw FoundLoop(fid, ref); would bypass unrolling and be MUCH faster assumption: visits contains parent fid
   */
  protected PlaylistLoop checkNoLoops(LongVector _visits, int _depth) {
    PlayerDatabase db = getDB();

    long fid = getFID();
    if (_visits.contains(fid)) {
      return new PlaylistLoop(this, fid);
    }

    int vlen = _visits.size();
    int len = myFIDs.size();
    for (int i = 0; i < len; i ++ ) {
      long entryFID = getFIDAt(i);
      IFIDNode node = db.getNode(entryFID);
      if (!(node instanceof FIDPlaylist)) {
        continue;
      }
      FIDPlaylist playlist = (FIDPlaylist) node;

      for (int j = 0; j < vlen; j ++ ) {
        long visitFid = _visits.elementAt(j);
        if (visitFid == entryFID) {
          return new PlaylistLoop(this, entryFID);
        }
      }

      _visits.addElement(fid);
      PlaylistLoop loop = playlist.checkNoLoops(_visits, _depth + 1);
      _visits.removeElementAt(_visits.size() - 1);
      if (loop != null) {
        return loop;
      }
    }

    return null;
  }

  /**
   * Updates the length (in bytes) of this playlist. This should be called after any operation that changes the size of this playlist.
   */
  protected void updateLength() {
    int length = myFIDs.size() * 4; // size * length of int
    getTags().setIntValue(DatabaseTags.LENGTH_TAG, length);
  }

  /**
   * Removes all the nodes with the given FID or the node at the given index.
   * 
   * @param _fid the FID to remove (if _index == -1)
   * @param _index the index of the node to remove (or -1 to remove FID)
   */
  protected boolean removeFID(IFIDNode _node, int _index, boolean _refreshAggregateTags) {
    boolean nodeRemoved = false;
    if (_index == -1) {
      for (int i = size() - 1; i >= 0; i -- ) {
        if (getFIDAt(i) == _node.getFID()) {
          removeNode0(_node, i);
          nodeRemoved = true;
        }
      }
    }
    else {
      if (_node == null) {
        _node = getNodeAt(_index);
      }
      removeNode0(_node, _index);
      nodeRemoved = true;
    }

    if (nodeRemoved && _refreshAggregateTags) {
      refreshAggregateTags(_node, false);
    }

    return nodeRemoved;
  }

  private void removeNode0(IFIDNode _node, int _index) {
    myFIDs.remove(_index);
    if (_node != null) {
      _node.nodeRemovedFromPlaylist(this);
      setDirty(true);
      updateLength();

      firePlaylistNodeRemoved(_node, _index);
    }
  }

  private static int binarySearch(CollationKeySortableContainer _cacheContainer, NodeTag _sortOnNodeTag, boolean _sortAscending, Object _searchFor) {
    Comparable key = (Comparable) _cacheContainer.getSortValue(_sortOnNodeTag, _searchFor);
    boolean requestIsPlaylist = (_searchFor instanceof FIDPlaylist || _searchFor instanceof String);

    int low = 0;
    int high = _cacheContainer.getSize() - 1;

    while (low <= high) {
      int mid = (low + high) >> 1;
      IFIDNode currentNode = (IFIDNode) _cacheContainer.getValueAt(mid);
      boolean currentIsPlaylist = (currentNode instanceof FIDPlaylist);
      int cmp;
      if (!FIDPlaylist.SORT_PLAYLISTS_ABOVE_TUNES || currentIsPlaylist == requestIsPlaylist) {
        Comparable midValKey = (Comparable) _cacheContainer.getSortValue(_sortOnNodeTag, currentNode);
        cmp = midValKey.compareTo(key);
      }
      else if (currentIsPlaylist && !requestIsPlaylist) {
        cmp = -1;
      }
      else {
        cmp = 1;
      }

      if (!_sortAscending) {
        cmp = -cmp;
      }

      if (cmp < 0) {
        low = mid + 1;
      }
      else if (cmp > 0) {
        high = mid - 1;
      }
      else {
        return mid; // key found
      }
    }
    return -(low + 1); // key not found
  }

  private static int linearSearch(CollationKeySortableContainer _cacheContainer, NodeTag _sortOnNodeTag, boolean _sortAscending, Object _searchFor) {
    Comparable key = (Comparable) _cacheContainer.getSortValue(_sortOnNodeTag, _searchFor);
    boolean requestIsPlaylist = (_searchFor instanceof FIDPlaylist || _searchFor instanceof String);

    int index = -1;
    int size = _cacheContainer.getSize();
    for (int i = 0; index == -1 && i < size; i ++ ) {
      IFIDNode currentNode = (IFIDNode) _cacheContainer.getValueAt(i);
      if (currentNode != null) {
        boolean currentIsPlaylist = (currentNode instanceof FIDPlaylist);
        int cmp;
        if (!FIDPlaylist.SORT_PLAYLISTS_ABOVE_TUNES || currentIsPlaylist == requestIsPlaylist) {
          Comparable keyVal = (Comparable) _cacheContainer.getSortValue(_sortOnNodeTag, currentNode);
          cmp = keyVal.compareTo(key);
        }
        else if (currentIsPlaylist && !requestIsPlaylist) {
          cmp = -1;
        }
        else {
          cmp = 1;
        }

        if (!_sortAscending) {
          cmp = -cmp;
        }

        if (cmp == 0) {
          index = i;
        }
      }
    }
    return index;
  }

  protected class PlaylistLoop {
    public FIDPlaylist playlist;
    public long fid;

    public PlaylistLoop(FIDPlaylist _playlist, long _fid) {
      playlist = _playlist;
      fid = _fid;
    }

    public String toString() {
      return "[PlaylistLoop: playlist = " + playlist + "; fid = " + fid + "]";
    }
  }
}