/* FIDPlaylist - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.tree.TreePath;

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

import org.jempeg.nodestore.event.IPlaylistListener;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.model.TagValueRetriever;

public class FIDPlaylist extends AbstractFIDNode
    implements IMutableTypeContainer, ISortableContainer, IFIDPlaylistWrapper
{
    static final long serialVersionUID = 8208806883442920321L;
    public static final String[] AGGREGATE_TAGS
	= { "artist", "source", "genre", "year" };
    private static boolean SORT_PLAYLISTS_ABOVE_TUNES
	= PropertiesManager.getInstance()
	      .getBooleanProperty("sortPlaylistsFirst");
    private int myContainedType;
    private ArrayList myFIDs;
    private boolean myTransient;
    private boolean mySoup;
    private NodeTag mySortTag;
    private boolean mySortAscending = true;
    private transient List myPlaylistListeners;
    private transient boolean myReordering;
    
    protected class PlaylistLoop
    {
	public FIDPlaylist playlist;
	public long fid;
	
	public PlaylistLoop(FIDPlaylist _playlist, long _fid) {
	    playlist = _playlist;
	    fid = _fid;
	}
	
	public String toString() {
	    return ("[PlaylistLoop: playlist = " + playlist + "; fid = " + fid
		    + "]");
	}
    }
    
    public static void setSortPlaylistsAboveTunes
	(PlayerDatabase _playerDatabase, boolean _sortPlaylistsAboveTunes) {
	if (SORT_PLAYLISTS_ABOVE_TUNES != _sortPlaylistsAboveTunes) {
	    SORT_PLAYLISTS_ABOVE_TUNES = _sortPlaylistsAboveTunes;
	    if (_playerDatabase != null)
		_playerDatabase.getRootPlaylist().resort(true);
	}
    }
    
    public FIDPlaylist(PlayerDatabase _db, boolean _transient) {
	this(_db, _transient, true);
    }
    
    public FIDPlaylist(PlayerDatabase _db, boolean _transient,
		       boolean _addToDatabase) {
	super(_db, _db.getNodeMap().findFree());
	init(_transient, null, true, true, _addToDatabase);
    }
    
    public FIDPlaylist(PlayerDatabase _db, long _fid, NodeTags _tags,
		       boolean _transient,
		       boolean _generateDatabaseChangeEvent) {
	super(_db, _fid, _tags);
	init(_transient, null, false, _generateDatabaseChangeEvent, true);
	setIdentified(true);
    }
    
    public FIDPlaylist(PlayerDatabase _db, long _fid, NodeTags _tags,
		       boolean _transient, PlaylistPair[] _members) {
	super(_db, _fid, _tags);
	init(_transient, _members, false, false, true);
	setDirty(false);
	setIdentified(true);
    }
    
    protected void init(boolean _transient, PlaylistPair[] _members,
			boolean _initializeGeneration,
			boolean _generateDatabaseChange,
			boolean _addToDatabase) {
	myTransient = _transient;
	myFIDs = new ArrayList();
	myContainedType = 3;
	if (_members != null)
	    populate(_members);
	if (!_transient && _initializeGeneration)
	    initializeGeneration();
	if (_addToDatabase)
	    addToDatabase(!_transient && _generateDatabaseChange);
	setType(2);
	getTags().setValue("type", "playlist");
	String sortTag = getTags().getValue("sort");
	if (sortTag.length() > 0)
	    setSortTag(NodeTag.getNodeTag(sortTag), false);
    }
    
    protected boolean isAddedToDatabase() {
	return true;
    }
    
    public synchronized void addPlaylistListener(IPlaylistListener _listener) {
	if (myPlaylistListeners == null)
	    myPlaylistListeners = new ArrayList();
	myPlaylistListeners.add(_listener);
    }
    
    public void removePlaylistListener(IPlaylistListener _listener) {
	if (myPlaylistListeners != null)
	    myPlaylistListeners.remove(_listener);
    }
    
    public void setSortTag(NodeTag _sortTag, boolean _resortIfNecessary) {
	if (_sortTag != null && _sortTag.getName().length() == 0)
	    _sortTag = null;
	if (mySortTag != _sortTag) {
	    mySortTag = _sortTag;
	    if (_resortIfNecessary)
		resort(false);
	}
    }
    
    public NodeTag getSortTag() {
	return mySortTag;
    }
    
    public void afterNodeTagModified(IFIDNode _node, String _tag,
				     String _oldValue, String _newValue) {
	super.afterNodeTagModified(_node, _tag, _oldValue, _newValue);
	if (isIdentified() && "sort".equals(_tag))
	    setSortTag(NodeTag.getNodeTag(_newValue), true);
    }
    
    void firePlaylistStructureChanged() {
	if (!isTransient())
	    getDB().getSynchronizeQueue()
		.enqueue(new PlaylistMembersDatabaseChange(this));
	if (myPlaylistListeners != null) {
	    for (int i = myPlaylistListeners.size() - 1; i >= 0; i--) {
		IPlaylistListener listener
		    = (IPlaylistListener) myPlaylistListeners.get(i);
		listener.playlistStructureChanged(this);
	    }
	}
    }
    
    void firePlaylistNodeInserted(IFIDNode _childNode, int _index) {
	if (!isTransient())
	    getDB().getSynchronizeQueue()
		.enqueue(new PlaylistMembersDatabaseChange(this));
	if (myPlaylistListeners != null) {
	    for (int i = myPlaylistListeners.size() - 1; i >= 0; i--) {
		IPlaylistListener listener
		    = (IPlaylistListener) myPlaylistListeners.get(i);
		listener.playlistNodeInserted(this, _childNode, _index);
	    }
	}
    }
    
    void firePlaylistNodeRemoved(IFIDNode _childNode, int _index) {
	if (!isTransient())
	    getDB().getSynchronizeQueue()
		.enqueue(new PlaylistMembersDatabaseChange(this));
	if (myPlaylistListeners != null) {
	    for (int i = myPlaylistListeners.size() - 1; i >= 0; i--) {
		IPlaylistListener listener
		    = (IPlaylistListener) myPlaylistListeners.get(i);
		listener.playlistNodeRemoved(this, _childNode, _index);
	    }
	}
    }
    
    public void setContainedType(int _containedType) {
	myContainedType = _containedType;
    }
    
    public int getContainedType() {
	return myContainedType;
    }
    
    public boolean isSoup() {
	return mySoup;
    }
    
    public void setSoup(boolean _soup) {
	mySoup = _soup;
    }
    
    public boolean isTransient() {
	return myTransient;
    }
    
    public void setTransient(boolean _transient) {
	myTransient = _transient;
    }
    
    public int getAttributes(boolean _recursive) {
	int attributes = super.getAttributes(_recursive);
	if (_recursive) {
	    int size = getSize();
	    for (int i = 0; i < size; i++) {
		IFIDNode node = getNodeAt(i);
		if (node != null)
		    attributes |= node.getAttributes(_recursive);
	    }
	} else
	    attributes &= ~0x1;
	return attributes;
    }
    
    public synchronized void sortBy(NodeTag _sortNodeTag,
				    boolean _sortAscending) {
	if (_sortNodeTag != null
	    && (mySortTag == null
		|| mySortTag.equals(_sortNodeTag) && _sortAscending)) {
	    boolean oldSortAscending = mySortAscending;
	    NodeTag oldSortTag = mySortTag;
	    ArrayList oldFIDs = myFIDs;
	    try {
		try {
		    myReordering = true;
		    mySortAscending = _sortAscending;
		    mySortTag = _sortNodeTag;
		    if (getSize() > 0) {
			myFIDs = new ArrayList();
			PlayerDatabase db = getDB();
			int size = oldFIDs.size();
			CollationKeyCache cache
			    = CollationKeyCache.createDefaultCache();
			for (int i = 0; i < size; i++) {
			    PlaylistPair playlistPair
				= (PlaylistPair) oldFIDs.get(i);
			    IFIDNode node = db.getNode(playlistPair.getFID());
			    addNode(node, false, cache);
			}
			setDirty(true);
			firePlaylistStructureChanged();
		    }
		} catch (RuntimeException t) {
		    myFIDs = oldFIDs;
		    throw t;
		}
	    } catch (Object object) {
		myReordering = false;
		mySortAscending = oldSortAscending;
		mySortTag = oldSortTag;
		throw object;
	    }
	    myReordering = false;
	    mySortAscending = oldSortAscending;
	    mySortTag = oldSortTag;
	}
    }
    
    public synchronized void resort(boolean _recursive) {
	if (mySortTag != null)
	    sortBy(mySortTag, true);
	if (_recursive) {
	    int size = getSize();
	    for (int i = 0; i < size; i++) {
		FIDPlaylist playlist = getPlaylistAt(i);
		if (playlist != null)
		    playlist.resort(_recursive);
	    }
	}
    }
    
    public synchronized int[] reposition(int[] _indexes, int _distance) {
	if (mySortTag != null)
	    _distance = 0;
	int[] newIndexes;
	if (_indexes.length > 0 && _distance != 0) {
	    newIndexes = new int[_indexes.length];
	    IntQuickSort quickSort = new IntQuickSort();
	    quickSort.sort(_indexes);
	    if (_distance < 0)
		_distance = Math.max(-_indexes[0], _distance);
	    else
		_distance
		    = Math.min(getSize() - _indexes[_indexes.length - 1] - 1,
			       _distance);
	    if (_distance != 0) {
		if (_distance < 0) {
		    for (int i = 0; i < _indexes.length; i++) {
			int index = _indexes[i];
			newIndexes[i] = index + _distance;
			PlaylistPair playlistPair
			    = (PlaylistPair) myFIDs.get(index);
			myFIDs.remove(index);
			myFIDs.add(newIndexes[i], playlistPair);
		    }
		} else {
		    for (int i = _indexes.length - 1; i >= 0; i--) {
			int index = _indexes[i];
			newIndexes[i] = index + _distance;
			PlaylistPair playlistPair
			    = (PlaylistPair) myFIDs.get(index);
			myFIDs.remove(index);
			myFIDs.add(newIndexes[i], playlistPair);
		    }
		}
		setDirty(true);
		firePlaylistStructureChanged();
	    } else
		newIndexes = _indexes;
	} else
	    newIndexes = _indexes;
	return newIndexes;
    }
    
    public int size() {
	if (myFIDs == null)
	    return -1;
	return myFIDs.size();
    }
    
    public int getTrackCount() {
	int trackCount = 0;
	int size = size();
	for (int i = 0; i < size; i++) {
	    IFIDNode child = getNodeAt(i);
	    if (child instanceof FIDPlaylist)
		trackCount += ((FIDPlaylist) child).getTrackCount();
	    else
		trackCount++;
	}
	return trackCount;
    }
    
    public long getFIDAt(int _pos) {
	PlaylistPair fid = getPlaylistPairAt(_pos);
	return fid.getFID();
    }
    
    public PlaylistPair getPlaylistPairAt(int _pos) {
	PlaylistPair pair = (PlaylistPair) myFIDs.get(_pos);
	return pair;
    }
    
    public IFIDNode getNodeAt(int _pos) {
	return getDB().getNode(getFIDAt(_pos));
    }
    
    public synchronized IFIDNode[] toArray() {
	IFIDNode[] nodes = new IFIDNode[getSize()];
	for (int i = 0; i < nodes.length; i++)
	    nodes[i] = getNodeAt(i);
	return nodes;
    }
    
    public synchronized PlaylistPair[] getPlaylistPairs() {
	PlaylistPair[] playlistPairs = new PlaylistPair[getSize()];
	myFIDs.toArray(playlistPairs);
	return playlistPairs;
    }
    
    public FIDPlaylist getPlaylistAt(int _pos) {
	IFIDNode node = getNodeAt(_pos);
	if (node instanceof FIDPlaylist)
	    return (FIDPlaylist) node;
	return null;
    }
    
    public boolean contains(IFIDNode _node) {
	boolean contains
	    = myFIDs.contains(new PlaylistPair(_node.getFID(), -1));
	return contains;
    }
    
    public int getIndexOf(IFIDNode _node) {
	long fid = _node.getFID();
	int index = myFIDs.indexOf(new PlaylistPair(fid, -1));
	return index;
    }
    
    public int getIndexOf(IFIDNode _node, CollationKeyCache _cache) {
	CollationKeySortableContainer cacheContainer
	    = new CollationKeySortableContainer(_cache, this);
	int matchIndex
	    = binarySearch(cacheContainer, mySortTag, mySortAscending, _node);
	if (matchIndex >= 0) {
	    IFIDNode matchingNode = getNodeAt(matchIndex);
	    if (!matchingNode.equals(_node)) {
		int size = myFIDs.size();
		int foundIndex = -1;
		if (size > 0) {
		    Comparable nodeKey
			= ((Comparable)
			   cacheContainer.getSortValue(mySortTag, _node));
		    long fid = _node.getFID();
		    boolean done = false;
		    int lowIndex = matchIndex - 1;
		    int highIndex = matchIndex + 1;
		    while (foundIndex == -1 && !done) {
			done = true;
			if (lowIndex >= 0) {
			    if (getFIDAt(lowIndex) == fid)
				foundIndex = lowIndex;
			    else if (nodeKey.compareTo(cacheContainer
							   .getSortValueAt
						       (mySortTag, lowIndex))
				     != 0) {
				lowIndex = -1;
				done = false;
			    } else {
				lowIndex--;
				done = false;
			    }
			}
			if (foundIndex == -1 && highIndex < size) {
			    if (getFIDAt(highIndex) == fid)
				foundIndex = highIndex;
			    else if (nodeKey.compareTo(cacheContainer
							   .getSortValueAt
						       (mySortTag, highIndex))
				     != 0) {
				highIndex = 2147483647;
				done = false;
			    } else {
				highIndex++;
				done = false;
			    }
			}
		    }
		}
		if (foundIndex != -1)
		    matchIndex = foundIndex;
		else
		    matchIndex = -(matchIndex + 1);
	    }
	}
	return matchIndex;
    }
    
    public synchronized int addPlaylistChildren(FIDPlaylist _childPlaylist,
						CollationKeyCache _cache) {
	int firstIndex = -1;
	int size = _childPlaylist.size();
	for (int i = 0; i < size; i++) {
	    IFIDNode childNode = _childPlaylist.getNodeAt(i);
	    int index;
	    if (childNode instanceof FIDPlaylist)
		index = addPlaylistChildren((FIDPlaylist) childNode, _cache);
	    else
		index = addNode(childNode, false, _cache);
	    if (firstIndex == -1)
		firstIndex = index;
	}
	return firstIndex;
    }
    
    public synchronized int addNode(IFIDNode _node, boolean _checkForLoops,
				    CollationKeyCache _cache) {
	int index;
	if (mySortTag != null) {
	    CollationKeySortableContainer cacheContainer
		= new CollationKeySortableContainer(_cache, this);
	    index = binarySearch(cacheContainer, mySortTag, mySortAscending,
				 _node);
	    if (index < 0)
		index = (index + 1) * -1;
	} else
	    index = myFIDs.size();
	insertNodeAt(_node, index, _checkForLoops);
	return index;
    }
    
    protected void makePersistent(FIDPlaylist _transientPlaylist) {
	if (_transientPlaylist.isTransient()) {
	    _transientPlaylist.setTransient(false);
	    _transientPlaylist.setDirty(true);
	    int size = _transientPlaylist.getSize();
	    for (int i = 0; i < size; i++) {
		IFIDNode childNode = _transientPlaylist.getNodeAt(i);
		if (childNode instanceof FIDPlaylist) {
		    FIDPlaylist childPlaylist = (FIDPlaylist) childNode;
		    makePersistent(childPlaylist);
		}
	    }
	    firePlaylistStructureChanged();
	}
    }
    
    public void makeSoupy() {
	makeSoupy(this);
    }
    
    protected void makeSoupy(FIDPlaylist _playlist) {
	_playlist.setSoup(true);
	int size = _playlist.getSize();
	for (int i = 0; i < size; i++) {
	    FIDPlaylist childPlaylist = _playlist.getPlaylistAt(i);
	    if (childPlaylist != null)
		makeSoupy(childPlaylist);
	}
    }
    
    public synchronized void insertNodeAt(IFIDNode _node, int _index,
					  boolean _checkForLoops) {
	PlayerDatabase db = getDB();
	long fid = _node.getFID();
	if (!db.getNode(fid).isMarkedForDeletion()) {
	    if (_node instanceof FIDPlaylist) {
		FIDPlaylist playlist = (FIDPlaylist) _node;
		if (_node.isTransient() && !isTransient())
		    makePersistent(playlist);
		if (_checkForLoops) {
		    Set ancestorsSet = getAncestors();
		    FIDPlaylist loop = playlist.getLoop(ancestorsSet);
		    if (loop != null) {
			Debug.handleError(("You can't add " + _node.getTitle()
					   + " to " + getTitle()
					   + " because it creates a loop."),
					  true);
			return;
		    }
		}
	    }
	    int insertIndex;
	    if (_index == -1)
		insertIndex = myFIDs.size();
	    else
		insertIndex = _index;
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
	if (_ancestorsSet.contains(this))
	    loop = this;
	else {
	    int size = getSize();
	    for (int i = 0; loop == null && i < size; i++) {
		FIDPlaylist childPlaylist = getPlaylistAt(i);
		if (childPlaylist != null)
		    loop = childPlaylist.getLoop(_ancestorsSet);
	    }
	}
	return loop;
    }
    
    public synchronized boolean removeNode(IFIDNode _node) {
	boolean nodeRemoved = removeFID(_node, -1, true);
	return nodeRemoved;
    }
    
    public synchronized void removeNodeAt(int _pos) {
	removeFID(null, _pos, true);
    }
    
    public synchronized Reason[] checkForProblems(boolean _repair,
						  TreePath _path) {
	List reasonsVec = new LinkedList();
	PlayerDatabase db = getDB();
	int i = 0;
	while (i < myFIDs.size()) {
	    PlaylistPair pair = getPlaylistPairAt(i);
	    long curFID = pair.getFID();
	    IFIDNode node = db.getNode(curFID);
	    if (node == null) {
		Debug.println
		    (8, (getTitle() + " (fid = " + getFID()
			 + ") referred to a missing tune or playlist with ID "
			 + curFID));
		if (_repair)
		    removeFID(null, i, false);
		else
		    i++;
	    } else {
		node.nodeAddedToPlaylist(this);
		if (pair.isStale(node))
		    removeFID(null, i, false);
		else {
		    boolean moveToNextNode = true;
		    if (node instanceof FIDPlaylist) {
			FIDPlaylist playlist = (FIDPlaylist) node;
			FIDPlaylist loopParentPlaylist = null;
			int pathCount = _path.getPathCount();
			for (int pathNum = 0;
			     loopParentPlaylist == null && pathNum < pathCount;
			     pathNum++) {
			    FIDPlaylist pathPlaylist
				= ((FIDPlaylist)
				   _path.getPathComponent(pathNum));
			    if (pathPlaylist.equals(playlist))
				loopParentPlaylist = pathPlaylist;
			}
			if (loopParentPlaylist != null) {
			    reasonsVec.add
				(new Reason("Removed a loop created by "
					    + playlist.getTitle()
					    + " inside of " + getTitle()
					    + "."));
			    removeFID(null, i, false);
			    moveToNextNode = false;
			} else if (playlist.getReferenceCount() == 1) {
			    Reason[] reasons
				= (playlist.checkForProblems
				   (_repair,
				    _path.pathByAddingChild(playlist)));
			    Reason.fromArray(reasons, reasonsVec);
			}
		    }
		    if (moveToNextNode)
			i++;
		}
	    }
	}
	return Reason.toArray(reasonsVec);
    }
    
    public synchronized boolean refreshAggregateTags(IFIDNode _becauseOf,
						     boolean _resort) {
	boolean tagValuesChanged;
	if (areAggregateTagsDifferent(_becauseOf)) {
	    String variousTagValue = NodeTag.getVariousTagValue();
	    NodeTags newPlaylistTags = new NodeTags();
	    boolean done = false;
	    int size = getSize();
	    for (int i = 0; !done && i < size; i++) {
		IFIDNode node = getNodeAt(i);
		NodeTags nodeTags = node.getTags();
		boolean newTagValuesChanged
		    = copyAggregateTags(newPlaylistTags, nodeTags, i > 0);
		if (newTagValuesChanged) {
		    done = true;
		    for (int aggregateTagNum = 0;
			 done && aggregateTagNum < AGGREGATE_TAGS.length;
			 aggregateTagNum++) {
			String newPlaylistTagValue
			    = newPlaylistTags
				  .getValue(AGGREGATE_TAGS[aggregateTagNum]);
			done = newPlaylistTagValue.equals(variousTagValue);
		    }
		}
	    }
	    tagValuesChanged = copyAggregateTags(newPlaylistTags, false);
	    if (_resort && mySortTag != null) {
		int currentIndex = getIndexOf(_becauseOf);
		Object obj = myFIDs.remove(currentIndex);
		CollationKeySortableContainer cacheContainer
		    = (new CollationKeySortableContainer
		       (CollationKeyCache.createDefaultCache(), this));
		int newIndex
		    = binarySearch(cacheContainer, mySortTag, mySortAscending,
				   mySortTag.getValue(_becauseOf));
		if (newIndex < 0)
		    newIndex = (newIndex + 1) * -1;
		if (newIndex == currentIndex)
		    myFIDs.add(currentIndex, obj);
		else {
		    myFIDs.add(newIndex, obj);
		    setDirty(true);
		    firePlaylistNodeRemoved(_becauseOf, currentIndex);
		    firePlaylistNodeInserted(_becauseOf, newIndex);
		}
	    }
	} else
	    tagValuesChanged = false;
	return tagValuesChanged;
    }
    
    private boolean copyAggregateTags(NodeTags _becauseOfTags,
				      boolean _aggregate) {
	NodeTags playlistTags = getTags();
	boolean tagValuesChanged
	    = copyAggregateTags(playlistTags, _becauseOfTags, _aggregate);
	return tagValuesChanged;
    }
    
    private boolean areAggregateTagsDifferent(IFIDNode _node) {
	NodeTags nodeTags = _node.getTags();
	NodeTags playlistTags = getTags();
	boolean tagValuesDifferent = false;
	for (int i = 0; !tagValuesDifferent && i < AGGREGATE_TAGS.length;
	     i++) {
	    String playlistTagValue = playlistTags.getValue(AGGREGATE_TAGS[i]);
	    String nodeTagValue = nodeTags.getValue(AGGREGATE_TAGS[i]);
	    tagValuesDifferent = !nodeTagValue.equals(playlistTagValue);
	}
	return tagValuesDifferent;
    }
    
    private static boolean copyAggregateTags
	(NodeTags _playlistTags, NodeTags _becauseOfTags, boolean _aggregate) {
	String variousTagValue = NodeTag.getVariousTagValue();
	boolean tagValuesChanged = false;
	for (int i = 0; i < AGGREGATE_TAGS.length; i++) {
	    String tagName = AGGREGATE_TAGS[i];
	    boolean tagValueChanged = false;
	    String playlistTagValue = _playlistTags.getValue(tagName);
	    if (!_aggregate || !playlistTagValue.equals(variousTagValue)) {
		String newTagValue = _becauseOfTags.getValue(tagName);
		if (!_aggregate) {
		    tagValueChanged = true;
		    _playlistTags.setValue(tagName, newTagValue);
		} else if (!playlistTagValue.equals(newTagValue)) {
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
	for (int i = 0; !isAggregateTag && i < AGGREGATE_TAGS.length; i++)
	    isAggregateTag = AGGREGATE_TAGS[i].equals(_tagName);
	return isAggregateTag;
    }
    
    public synchronized int getPlaylistIndex
	(String _name, int _type, int _containedType, IFIDNode _becauseOf,
	 boolean _allowDuplicateNames, boolean _createIfMissing,
	 CollationKeyCache _cache) {
	int playlistIndex = -1;
	boolean createPlaylist = _createIfMissing;
	if (!_allowDuplicateNames) {
	    CollationKeySortableContainer cacheContainer
		= new CollationKeySortableContainer(_cache, this);
	    if (mySortTag == NodeTag.TITLE_TAG)
		playlistIndex = binarySearch(cacheContainer, NodeTag.TITLE_TAG,
					     mySortAscending, _name);
	    else
		playlistIndex = linearSearch(cacheContainer, NodeTag.TITLE_TAG,
					     mySortAscending, _name);
	    if (playlistIndex < 0) {
		if (mySortTag == NodeTag.TITLE_TAG)
		    playlistIndex = (playlistIndex + 1) * -1;
	    } else
		createPlaylist = false;
	}
	if (createPlaylist) {
	    FIDPlaylist newChildPlaylist
		= new FIDPlaylist(getDB(), myTransient);
	    newChildPlaylist.getTags().setValue("title", _name);
	    newChildPlaylist.updateLength();
	    newChildPlaylist.setDirty(true);
	    newChildPlaylist.setType(_type);
	    newChildPlaylist.setContainedType(_containedType);
	    boolean isSoup = isSoup();
	    newChildPlaylist.setSoup(isSoup);
	    if (!isSoup) {
		NodeTag sortTag = getSortTag();
		if (sortTag != null) {
		    newChildPlaylist.getTags().setValue("sort",
							sortTag.getName());
		    newChildPlaylist.setSortTag(sortTag, false);
		}
	    }
	    if (_becauseOf != null)
		newChildPlaylist.copyAggregateTags(_becauseOf.getTags(),
						   false);
	    if (playlistIndex < 0)
		playlistIndex = addNode(newChildPlaylist, false, _cache);
	    else
		insertNodeAt(newChildPlaylist, playlistIndex, false);
	    newChildPlaylist.setIdentified(true);
	}
	return playlistIndex;
    }
    
    public synchronized int getPlaylistIndex
	(String _name, boolean _allowDuplicateNames, boolean _createIfMissing,
	 CollationKeyCache _cache) {
	int playlistIndex
	    = getPlaylistIndex(_name, 2, myContainedType, null,
			       _allowDuplicateNames, _createIfMissing, _cache);
	return playlistIndex;
    }
    
    public synchronized int createPlaylist(String _name) {
	return getPlaylistIndex(_name, true, true,
				CollationKeyCache.createDefaultCache());
    }
    
    public void nodeRemovedFromPlaylist(FIDPlaylist _playlist) {
	super.nodeRemovedFromPlaylist(_playlist);
	if (getReferenceCount() == 0) {
	    int size = size();
	    for (int i = size - 1; i >= 0; i--)
		removeFID(null, i, false);
	    delete();
	}
    }
    
    public Object getSortValue(NodeTag _sortOnNodeTag, Object _value) {
	Object sortValue;
	if (_value instanceof IFIDNode)
	    sortValue = TagValueRetriever.getValue((IFIDNode) _value,
						   _sortOnNodeTag.getName());
	else
	    sortValue = _value;
	return sortValue;
    }
    
    public Object getSortValueAt(NodeTag _sortOnNodeTag, int _index) {
	Object sortValue = getSortValue(_sortOnNodeTag, getNodeAt(_index));
	return sortValue;
    }
    
    public String getName() {
	return getTitle();
    }
    
    public int getSize() {
	return size();
    }
    
    public Object getValueAt(int _index) {
	return getNodeAt(_index);
    }
    
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
	for (int i = 0; i < size(); i++) {
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
	return ("[FIDPlaylist: " + getTitle() + "; fid = " + getFID()
		+ "; childCount = " + size() + "]");
    }
    
    public void populate(PlaylistPair[] _fids) {
	myFIDs.clear();
	myFIDs.ensureCapacity(_fids.length);
	for (int i = 0; i < _fids.length; i++)
	    myFIDs.add(_fids[i]);
	updateLength();
    }
    
    public byte[] toProtocol1Format() {
	try {
	    int childCount = size();
	    RefByteArrayOutputStream baos
		= new RefByteArrayOutputStream(childCount * 4);
	    LittleEndianOutputStream eos = new LittleEndianOutputStream(baos);
	    for (int i = 0; i < childCount; i++) {
		long fid = getFIDAt(i);
		eos.writeUnsigned32(fid);
	    }
	    return baos.getByteArray();
	} catch (IOException e) {
	    throw new ChainedRuntimeException
		      ("Unable to create Protocol 1 format playlist.", e);
	}
    }
    
    protected PlaylistLoop checkNoLoops(LongVector _visits, int _depth) {
	PlayerDatabase db = getDB();
	long fid = getFID();
	if (_visits.contains(fid))
	    return new PlaylistLoop(this, fid);
	int vlen = _visits.size();
	int len = myFIDs.size();
	for (int i = 0; i < len; i++) {
	    long entryFID = getFIDAt(i);
	    IFIDNode node = db.getNode(entryFID);
	    if (node instanceof FIDPlaylist) {
		FIDPlaylist playlist = (FIDPlaylist) node;
		for (int j = 0; j < vlen; j++) {
		    long visitFid = _visits.elementAt(j);
		    if (visitFid == entryFID)
			return new PlaylistLoop(this, entryFID);
		}
		_visits.addElement(fid);
		PlaylistLoop loop = playlist.checkNoLoops(_visits, _depth + 1);
		_visits.removeElementAt(_visits.size() - 1);
		if (loop != null)
		    return loop;
	    }
	}
	return null;
    }
    
    protected void updateLength() {
	int length = myFIDs.size() * 4;
	getTags().setIntValue("length", length);
    }
    
    protected boolean removeFID(IFIDNode _node, int _index,
				boolean _refreshAggregateTags) {
	boolean nodeRemoved = false;
	if (_index == -1) {
	    for (int i = size() - 1; i >= 0; i--) {
		if (getFIDAt(i) == _node.getFID()) {
		    removeNode0(_node, i);
		    nodeRemoved = true;
		}
	    }
	} else {
	    if (_node == null)
		_node = getNodeAt(_index);
	    removeNode0(_node, _index);
	    nodeRemoved = true;
	}
	if (nodeRemoved && _refreshAggregateTags)
	    refreshAggregateTags(_node, false);
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
    
    private static int binarySearch
	(CollationKeySortableContainer _cacheContainer, NodeTag _sortOnNodeTag,
	 boolean _sortAscending, Object _searchFor) {
	Comparable key
	    = ((Comparable)
	       _cacheContainer.getSortValue(_sortOnNodeTag, _searchFor));
	boolean requestIsPlaylist = (_searchFor instanceof FIDPlaylist
				     || _searchFor instanceof String);
	int low = 0;
	int high = _cacheContainer.getSize() - 1;
	while (low <= high) {
	    int mid = low + high >> 1;
	    IFIDNode currentNode = (IFIDNode) _cacheContainer.getValueAt(mid);
	    boolean currentIsPlaylist = currentNode instanceof FIDPlaylist;
	    int cmp;
	    if (!SORT_PLAYLISTS_ABOVE_TUNES
		|| currentIsPlaylist == requestIsPlaylist) {
		Comparable midValKey
		    = (Comparable) _cacheContainer.getSortValue(_sortOnNodeTag,
								currentNode);
		cmp = midValKey.compareTo(key);
	    } else if (currentIsPlaylist && !requestIsPlaylist)
		cmp = -1;
	    else
		cmp = 1;
	    if (!_sortAscending)
		cmp = -cmp;
	    if (cmp < 0)
		low = mid + 1;
	    else if (cmp > 0)
		high = mid - 1;
	    else
		return mid;
	}
	return -(low + 1);
    }
    
    private static int linearSearch
	(CollationKeySortableContainer _cacheContainer, NodeTag _sortOnNodeTag,
	 boolean _sortAscending, Object _searchFor) {
	Comparable key
	    = ((Comparable)
	       _cacheContainer.getSortValue(_sortOnNodeTag, _searchFor));
	boolean requestIsPlaylist = (_searchFor instanceof FIDPlaylist
				     || _searchFor instanceof String);
	int index = -1;
	int size = _cacheContainer.getSize();
	for (int i = 0; index == -1 && i < size; i++) {
	    IFIDNode currentNode = (IFIDNode) _cacheContainer.getValueAt(i);
	    if (currentNode != null) {
		boolean currentIsPlaylist = currentNode instanceof FIDPlaylist;
		int cmp;
		if (!SORT_PLAYLISTS_ABOVE_TUNES
		    || currentIsPlaylist == requestIsPlaylist) {
		    Comparable keyVal
			= ((Comparable)
			   _cacheContainer.getSortValue(_sortOnNodeTag,
							currentNode));
		    cmp = keyVal.compareTo(key);
		} else if (currentIsPlaylist && !requestIsPlaylist)
		    cmp = -1;
		else
		    cmp = 1;
		if (!_sortAscending)
		    cmp = -cmp;
		if (cmp == 0)
		    index = i;
	    }
	}
	return index;
    }
}
