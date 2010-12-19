/* SoupUpdater - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import com.inzyme.util.Debug;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.IDatabaseListener;
import org.jempeg.nodestore.event.INodeTagListener;
import org.jempeg.nodestore.model.NodeTag;

public class SoupUpdater implements IDatabaseListener, INodeTagListener
{
    private FIDPlaylist[] myTopLevelPlaylist;
    private ISoupLayer[] mySoupLayers;
    private Hashtable myDependentOnMap;
    private Stack myPossiblyEmptyPlaylistStack;
    
    public SoupUpdater(FIDPlaylist _soupPlaylist, ISoupLayer[] _soupLayers) {
	_soupPlaylist.getDB().addDatabaseListener(this);
	_soupPlaylist.getDB().addNodeTagListener(this);
	myTopLevelPlaylist = new FIDPlaylist[] { _soupPlaylist };
	mySoupLayers = _soupLayers;
	myDependentOnMap = new Hashtable();
	myPossiblyEmptyPlaylistStack = new Stack();
    }
    
    public ISoupLayer[] getSoupLayers() {
	return mySoupLayers;
    }
    
    public void initialize() {
	Debug.println(4, ("SoupUpdater.initialize: Initializing "
			  + mySoupLayers[0] + " ..."));
	long startTime = System.currentTimeMillis();
	FIDPlaylist topLevelPlaylist = myTopLevelPlaylist[0];
	topLevelPlaylist.isTransient();
	PlayerDatabase playerDatabase = topLevelPlaylist.getDB();
	Enumeration nodesEnum = playerDatabase.getNodeMap().elements();
	while (nodesEnum.hasMoreElements()) {
	    IFIDNode node = (IFIDNode) nodesEnum.nextElement();
	    addNodeIfQualified(myTopLevelPlaylist, node);
	}
	Debug.println(4, ("SoupUpdater.initialize: Initialized in "
			  + (System.currentTimeMillis() - startTime) + "ms"));
    }
    
    public void nodeAdded(IFIDNode _node) {
	/* empty */
    }
    
    public void nodeRemoved(IFIDNode _node) {
	List possiblyEmptyPlaylistsList = new ArrayList(5);
	removeNode(myTopLevelPlaylist, _node, 0, possiblyEmptyPlaylistsList);
	myPossiblyEmptyPlaylistStack.push(possiblyEmptyPlaylistsList);
	popAndRemovePlaylistsIfEmpty();
    }
    
    public void freeSpaceChanged(PlayerDatabase _playerDatabase,
				 long _totalSpace, long _freeSpace) {
	/* empty */
    }
    
    public void databaseCleared(PlayerDatabase _playerDatabase) {
	_playerDatabase.removeDatabaseListener(this);
	_playerDatabase.removeNodeTagListener(this);
    }
    
    public void nodeIdentified(IFIDNode _node) {
	addNodeIfQualified(myTopLevelPlaylist, _node);
    }
    
    public void beforeNodeTagModified(IFIDNode _node, String _tag,
				      String _oldValue, String _newValue) {
	if (_tag != "type" && _tag != "iconType" && isDependentOn(_tag)
	    && !_node.isTransient()) {
	    List possiblyEmptyPlaylistsList = new ArrayList(5);
	    removeNode(myTopLevelPlaylist, _node, 0,
		       possiblyEmptyPlaylistsList);
	    myPossiblyEmptyPlaylistStack.push(possiblyEmptyPlaylistsList);
	}
    }
    
    public void afterNodeTagModified(IFIDNode _node, String _tag,
				     String _oldValue, String _newValue) {
	if (!_node.isTransient() && _tag != "type" && _tag != "iconType"
	    && isDependentOn(_tag)) {
	    addNodeIfQualified(myTopLevelPlaylist, _node);
	    popAndRemovePlaylistsIfEmpty();
	}
    }
    
    private boolean isDependentOn(String _tagName) {
	boolean isDependentOn = false;
	Boolean isDependentOnBoolean
	    = (Boolean) myDependentOnMap.get(_tagName);
	if (isDependentOnBoolean == null) {
	    NodeTag tag = NodeTag.getNodeTag(_tagName);
	    for (int i = 0; !isDependentOn && i < mySoupLayers.length; i++)
		isDependentOn = mySoupLayers[i].isDependentOn(tag);
	    myDependentOnMap.put(_tagName, new Boolean(isDependentOn));
	} else
	    isDependentOn = isDependentOnBoolean.booleanValue();
	return isDependentOn;
    }
    
    private boolean addNodeIfQualified(FIDPlaylist[] _playlists,
				       IFIDNode _node) {
	boolean qualifies = true;
	for (int layerNum = 0; qualifies && layerNum < mySoupLayers.length;
	     layerNum++)
	    qualifies = mySoupLayers[layerNum].qualifies(_node);
	if (qualifies)
	    addNode(_playlists, _node, 0);
	return qualifies;
    }
    
    private void addNode(FIDPlaylist[] _playlists, IFIDNode _node,
			 int _layerNum) {
	if (_layerNum == mySoupLayers.length) {
	    ISoupLayer lastSoupLayer = mySoupLayers[_layerNum - 1];
	    for (int playlistNum = 0; playlistNum < _playlists.length;
		 playlistNum++) {
		FIDPlaylist playlist = _playlists[playlistNum];
		playlist.setSortTag(lastSoupLayer.getSortTag(), false);
		int index
		    = playlist.getIndexOf(_node, lastSoupLayer.getSortCache());
		if (index < 0)
		    playlist.insertNodeAt(_node, (index + 1) * -1, true);
		else
		    playlist.getIndexOf(_node, lastSoupLayer.getSortCache());
	    }
	} else {
	    ISoupLayer soupLayer = mySoupLayers[_layerNum];
	    if (soupLayer instanceof IPlaylistSoupLayer) {
		for (int playlistNum = 0; playlistNum < _playlists.length;
		     playlistNum++) {
		    FIDPlaylist playlist = _playlists[playlistNum];
		    playlist.setSortTag(soupLayer.getSortTag(), false);
		    IPlaylistSoupLayer playlistSoupLayer
			= (IPlaylistSoupLayer) soupLayer;
		    FIDPlaylist[] childPlaylists
			= playlistSoupLayer.getPlaylists(playlist, _node,
							 true);
		    addNode(childPlaylists, _node, _layerNum + 1);
		}
	    } else
		addNode(_playlists, _node, _layerNum + 1);
	}
    }
    
    private void removeNode(FIDPlaylist[] _playlists, IFIDNode _node,
			    int _layerNum, List _possiblyEmptyPlaylistList) {
	if (_layerNum == mySoupLayers.length) {
	    for (int playlistNum = 0; playlistNum < _playlists.length;
		 playlistNum++) {
		_playlists[playlistNum].removeNode(_node);
		_possiblyEmptyPlaylistList.add(_playlists[playlistNum]);
	    }
	} else {
	    ISoupLayer soupLayer = mySoupLayers[_layerNum];
	    if (soupLayer.qualifies(_node)) {
		if (soupLayer instanceof IPlaylistSoupLayer) {
		    IPlaylistSoupLayer playlistSoupLayer
			= (IPlaylistSoupLayer) soupLayer;
		    for (int playlistNum = 0; playlistNum < _playlists.length;
			 playlistNum++) {
			FIDPlaylist playlist = _playlists[playlistNum];
			FIDPlaylist[] childPlaylists
			    = playlistSoupLayer.getPlaylists(playlist, _node,
							     false);
			removeNode(childPlaylists, _node, _layerNum + 1,
				   _possiblyEmptyPlaylistList);
			_possiblyEmptyPlaylistList.add(playlist);
		    }
		} else
		    removeNode(_playlists, _node, _layerNum + 1,
			       _possiblyEmptyPlaylistList);
	    }
	}
    }
    
    private void popAndRemovePlaylistsIfEmpty() {
	List possiblyEmptyPlaylists
	    = (List) myPossiblyEmptyPlaylistStack.pop();
	int size = possiblyEmptyPlaylists.size();
	for (int i = 0; i < size; i++) {
	    FIDPlaylist playlist = (FIDPlaylist) possiblyEmptyPlaylists.get(i);
	    if (playlist != myTopLevelPlaylist[0] && playlist.getSize() == 0)
		playlist.delete();
	}
    }
}
