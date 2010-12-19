/* PlaylistTabSelector - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite.action;
import javax.swing.JTabbedPane;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.IDatabaseListener;

public class PlaylistTabSelector implements IDatabaseListener
{
    private JTabbedPane myTabbedPane;
    private int myPlaylistsTabIndex;
    
    public PlaylistTabSelector(JTabbedPane _tabbedPane,
			       int _playlistsTabIndex) {
	myTabbedPane = _tabbedPane;
	myPlaylistsTabIndex = _playlistsTabIndex;
    }
    
    public void freeSpaceChanged(PlayerDatabase _playerDatabase,
				 long _totalSpace, long _freeSpace) {
	/* empty */
    }
    
    public void databaseCleared(PlayerDatabase _playerDatabase) {
	/* empty */
    }
    
    public void nodeAdded(IFIDNode _node) {
	if (!_node.isTransient() && _node instanceof FIDPlaylist)
	    myTabbedPane.setSelectedIndex(myPlaylistsTabIndex);
    }
    
    public void nodeRemoved(IFIDNode _node) {
	/* empty */
    }
}
