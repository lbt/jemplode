/* PlayerDatabase - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.tree.TreePath;

import com.inzyme.container.IContainer;
import com.inzyme.filesystem.ImportFileFactory;
import com.inzyme.model.Reason;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.util.Debug;

import org.jempeg.filesystem.MusicImportFileFactory;
import org.jempeg.nodestore.event.IDatabaseListener;
import org.jempeg.nodestore.event.INodeTagListener;

public class PlayerDatabase implements IContainer
{
    public static final String FREE_SPACE_PROPERTY
	= "jempeg.playerDatabase.freeSpace";
    private Hashtable myFileToNodeMap;
    private Vector myDatabaseListeners;
    private Vector myNodeTagListeners;
    private boolean myTrackFreeSpace = true;
    private DatabaseTags myTagNames;
    private FIDNodeMap myFIDs;
    private long myTotalSpace;
    private long myFreeSpace;
    private SynchronizeQueue mySynchronizeQueue;
    private IDeviceSettings myDeviceSettings;
    private boolean myCreateUnattachedItems;
    private boolean myNestedPlaylistAllowed = true;
    
    static {
	ImportFileFactory.addImportFileFactory(new MusicImportFileFactory());
    }
    
    public PlayerDatabase() {
	myFIDs = new FIDNodeMap();
	myTagNames = new DatabaseTags();
	myDatabaseListeners = new Vector();
	myNodeTagListeners = new Vector();
	mySynchronizeQueue = new SynchronizeQueue();
	myDeviceSettings = new BasicDeviceSettings();
	myFileToNodeMap = new Hashtable();
    }
    
    public void setCreateUnattachedItems(boolean _createUnattachedItems) {
	myCreateUnattachedItems = _createUnattachedItems;
    }
    
    public boolean isNestedPlaylistAllowed() {
	return myNestedPlaylistAllowed;
    }
    
    public void setNestedPlaylistAllowed(boolean _nestedPlaylistAllowed) {
	myNestedPlaylistAllowed = _nestedPlaylistAllowed;
    }
    
    public IDeviceSettings getDeviceSettings() {
	return myDeviceSettings;
    }
    
    public void setDeviceSettings(IDeviceSettings _deviceSettings) {
	myDeviceSettings = _deviceSettings;
    }
    
    public String getName() {
	return "PlayerDatabase";
    }
    
    public int getSize() {
	return myFIDs.size();
    }
    
    public Object getValueAt(int _index) {
	IFIDNode node = myFIDs.getNodeAt(_index);
	return node;
    }
    
    public void clear() {
	myDeviceSettings = new BasicDeviceSettings();
	myTotalSpace = 0L;
	myFreeSpace = 0L;
	myTagNames.clear();
	myFIDs.clear();
	myFileToNodeMap.clear();
	myFIDs.reset();
	fireDatabaseCleared(this);
    }
    
    public SynchronizeQueue getSynchronizeQueue() {
	return mySynchronizeQueue;
    }
    
    public boolean isDirty() {
	if (!myDeviceSettings.isDirty() && mySynchronizeQueue.isEmpty()
	    && !mySynchronizeQueue.isSynchronizingNow())
	    return false;
	return true;
    }
    
    public void addDatabaseListener(IDatabaseListener _listener) {
	myDatabaseListeners.addElement(_listener);
    }
    
    public void removeDatabaseListener(IDatabaseListener _listener) {
	myDatabaseListeners.removeElement(_listener);
    }
    
    public void addNodeTagListener(INodeTagListener _listener) {
	myNodeTagListeners.addElement(_listener);
    }
    
    public void removeNodeTagListener(INodeTagListener _listener) {
	myNodeTagListeners.removeElement(_listener);
    }
    
    public void removeAllListeners() {
	myDatabaseListeners.removeAllElements();
	myNodeTagListeners.removeAllElements();
    }
    
    public IFIDNode getNode(long _fid) {
	return myFIDs.getNode(_fid);
    }
    
    public FIDPlaylist getPlaylist(long _fid) {
	IFIDNode node = getNode(_fid);
	if (node instanceof FIDPlaylist)
	    return (FIDPlaylist) node;
	return null;
    }
    
    public DatabaseTags getDatabaseTags() {
	return myTagNames;
    }
    
    public FIDNodeMap getNodeMap() {
	return myFIDs;
    }
    
    public FIDPlaylist getRootPlaylist() {
	FIDPlaylist rootPlaylist = (FIDPlaylist) getNode(256L);
	if (rootPlaylist == null) {
	    checkRootLists();
	    rootPlaylist = (FIDPlaylist) getNode(256L);
	}
	return rootPlaylist;
    }
    
    void addFID(long _fid, IFIDNode _node,
		boolean _generateDatabaseChangeEvent) {
	myFIDs.addNode(_fid, _node);
	if (_generateDatabaseChangeEvent) {
	    if (_node instanceof FIDLocalFile)
		mySynchronizeQueue.enqueue
		    (new NodeAddedDatabaseChange((FIDLocalFile) _node));
	    else if (_node instanceof FIDPlaylist)
		mySynchronizeQueue.enqueue
		    (new PlaylistAddedDatabaseChange((FIDPlaylist) _node));
	    else
		throw new IllegalArgumentException
			  ("Unable to add a database change event for " + _node
			   + ".");
	}
	fireNodeAdded(_node);
    }
    
    boolean removeFID(long _fid) {
	IFIDNode node = myFIDs.removeNode(_fid);
	boolean existed = node != null && !(node instanceof FIDStub);
	if (existed) {
	    if (!node.isTransient())
		mySynchronizeQueue
		    .enqueue(new NodeRemovedDatabaseChange(node));
	    fireNodeRemoved(node);
	}
	return existed;
    }
    
    void fireNodeAdded(IFIDNode _node) {
	for (int i = myDatabaseListeners.size() - 1; i >= 0; i--) {
	    IDatabaseListener listener
		= (IDatabaseListener) myDatabaseListeners.elementAt(i);
	    listener.nodeAdded(_node);
	}
    }
    
    void fireNodeRemoved(IFIDNode _node) {
	for (int i = myDatabaseListeners.size() - 1; i >= 0; i--) {
	    IDatabaseListener listener
		= (IDatabaseListener) myDatabaseListeners.elementAt(i);
	    listener.nodeRemoved(_node);
	}
    }
    
    void fireFreeSpaceChanged(PlayerDatabase _playerDatabase, long _totalSpace,
			      long _freeSpace) {
	for (int i = myDatabaseListeners.size() - 1; i >= 0; i--) {
	    IDatabaseListener listener
		= (IDatabaseListener) myDatabaseListeners.elementAt(i);
	    listener.freeSpaceChanged(_playerDatabase, _totalSpace,
				      _freeSpace);
	}
    }
    
    void fireDatabaseCleared(PlayerDatabase _playerDatabase) {
	for (int i = myDatabaseListeners.size() - 1; i >= 0; i--) {
	    IDatabaseListener listener
		= (IDatabaseListener) myDatabaseListeners.elementAt(i);
	    listener.databaseCleared(_playerDatabase);
	}
    }
    
    void fireNodeIdentified(IFIDNode _node) {
	for (int i = myNodeTagListeners.size() - 1; i >= 0; i--) {
	    INodeTagListener listener
		= (INodeTagListener) myNodeTagListeners.elementAt(i);
	    listener.nodeIdentified(_node);
	}
    }
    
    void fireBeforeNodeTagsModified(IFIDNode _node, String _tag,
				    String _oldValue, String _newValue) {
	for (int i = myNodeTagListeners.size() - 1; i >= 0; i--) {
	    INodeTagListener listener
		= (INodeTagListener) myNodeTagListeners.elementAt(i);
	    listener.beforeNodeTagModified(_node, _tag, _oldValue, _newValue);
	}
    }
    
    void fireAfterNodeTagsModified(IFIDNode _node, String _tag,
				   String _oldValue, String _newValue) {
	for (int i = myNodeTagListeners.size() - 1; i >= 0; i--) {
	    INodeTagListener listener
		= (INodeTagListener) myNodeTagListeners.elementAt(i);
	    listener.afterNodeTagModified(_node, _tag, _oldValue, _newValue);
	}
    }
    
    protected FIDPlaylist createRootPlaylist() {
	NodeTags tags = new NodeTags();
	tags.setValue("type", "playlist");
	tags.setValue("title",
		      ResourceBundleUtils.getUIString("rootPlaylist.title"));
	FIDPlaylist playlist = new FIDPlaylist(this, 256L, tags, false, true);
	playlist.setDirty(true);
	return playlist;
    }
    
    protected FIDPlaylist createUnattachedItems() {
	NodeTags tags = new NodeTags();
	tags.setValue("type", "playlist");
	tags.setValue("title",
		      ResourceBundleUtils
			  .getUIString("unattachedItemsPlaylist.title"));
	FIDPlaylist playlist = new FIDPlaylist(this, 272L, tags, true, false);
	playlist.setDirty(false);
	return playlist;
    }
    
    Hashtable getFileToNodeMap() {
	return myFileToNodeMap;
    }
    
    public Reason checkRootLists() {
	Reason reason = null;
	IFIDNode rootNode = getNode(256L);
	if (rootNode == null)
	    rootNode = createRootPlaylist();
	else if (!(rootNode instanceof FIDPlaylist)) {
	    rootNode.delete();
	    rootNode = createRootPlaylist();
	}
	if (myCreateUnattachedItems) {
	    IFIDNode unattachedItemsNode = getNode(272L);
	    if (unattachedItemsNode == null)
		unattachedItemsNode = createUnattachedItems();
	    else if (!(unattachedItemsNode instanceof FIDPlaylist)) {
		unattachedItemsNode.delete();
		FIDPlaylist fidplaylist = createUnattachedItems();
	    }
	}
	return reason;
    }
    
    public Reason[] checkForProblems0(boolean _repair) {
	Vector reasonsVec = new Vector();
	Enumeration elements = myFIDs.elements();
	while (elements.hasMoreElements()) {
	    IFIDNode node = (IFIDNode) elements.nextElement();
	    node.clearParentPlaylists();
	}
	Debug.println(4, "Checking root playlist...");
	Reason rootPlaylistReason = checkRootLists();
	if (rootPlaylistReason != null) {
	    reasonsVec.addElement(rootPlaylistReason);
	    Debug.println(8, rootPlaylistReason.getReason());
	}
	Debug.println(4, "Starting tree->Repair() recursion...");
	FIDPlaylist rootPlaylist = getRootPlaylist();
	Reason[] reasons
	    = rootPlaylist.checkForProblems(_repair,
					    new TreePath(rootPlaylist));
	Reason.fromArray(reasons, reasonsVec);
	Debug.println(4, "Completed tree->Repair() recursion...");
	Debug.println(4, "Setting refcounts...");
	CollationKeyCache cache = CollationKeyCache.createDefaultCache();
	Enumeration fidsEnum = myFIDs.elements();
	while (fidsEnum.hasMoreElements()) {
	    IFIDNode node = (IFIDNode) fidsEnum.nextElement();
	    if (node instanceof FIDPlaylist) {
		FIDPlaylist playlist = (FIDPlaylist) node;
		if (!playlist.isTransient()
		    && playlist.getReferenceCount() == 0) {
		    rootPlaylist.addNode(playlist, false, cache);
		    Reason[] zeroRefCountReasons
			= playlist.checkForProblems(true,
						    new TreePath(playlist));
		    Reason.fromArray(zeroRefCountReasons, reasonsVec);
		    Debug.println
			(8, ("Playlist '" + playlist.getTitle() + " (fid = "
			     + playlist.getFID()
			     + ")' had a zero refcount.  Adding to root."));
		    reasonsVec.addElement
			(new Reason(ResourceBundleUtils.getUIString
				    ("database.repair.zeroRefCountPlaylist",
				     new Object[] { playlist.getTitle() })));
		}
	    }
	}
	Debug.println(4, "Completed setting refcounts...");
	Debug.println(4, "Database checked for problems...");
	return Reason.toArray(reasonsVec);
    }
    
    public void setFreeSpace(long _totalSpace, long _freeSpace) {
	myTotalSpace = _totalSpace;
	myFreeSpace = _freeSpace;
	fireFreeSpaceChanged(this, myTotalSpace, myFreeSpace);
    }
    
    public long getTotalSpace() {
	return myTotalSpace;
    }
    
    public long getFreeSpace() {
	return myFreeSpace;
    }
    
    public long getUsedSpace() {
	return myTotalSpace - myFreeSpace;
    }
    
    public void setTrackFreeSpace(boolean _trackFreeSpace) {
	myTrackFreeSpace = _trackFreeSpace;
    }
    
    void consumeSpace(IFIDNode _node) {
	adjustFreeSpace(_node, false);
    }
    
    void checkFreeSpace(IFIDNode _node) {
	if (myTrackFreeSpace && (long) _node.getLength() > myFreeSpace)
	    throw new RuntimeException(ResourceBundleUtils.getErrorString
				       ("insufficientSpace"));
    }
    
    void freeSpace(IFIDNode _node) {
	adjustFreeSpace(_node, true);
    }
    
    protected void adjustFreeSpace(IFIDNode _node, boolean _freeSpace) {
	if (myTrackFreeSpace) {
	    long size = (long) _node.getLength();
	    if (_freeSpace)
		myFreeSpace += size;
	    else
		myFreeSpace -= size;
	    fireFreeSpaceChanged(this, myTotalSpace, myFreeSpace);
	}
    }
}
