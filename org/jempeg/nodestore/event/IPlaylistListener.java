package org.jempeg.nodestore.event;

import java.util.EventListener;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

/**
 * IPlaylistListener is an interface that is implemented by 
 * components that want to receive notifications about
 * when nodes are added or removed from a playlist.
 * 
 * @author Mike Schrag
 */
public interface IPlaylistListener extends EventListener {
	/**
	 * Fired when a playlist's structure changes completely.
	 * 
	 * @param _parentPlaylist the parent playlist
	 */
	public void playlistStructureChanged(FIDPlaylist _parentPlaylist);
	
	/**
	 * Fired when a node is added to a playlist
	 * 
	 * @param _parentPlaylist the parent playlist
	 * @param _childNode the node that was inserted
	 * @param _index the index of the nodes was inserted
	 */
	public void playlistNodeInserted(FIDPlaylist _parentPlaylist, IFIDNode _childNode, int _index);
	
	/**
	 * Fired when a node is removed from a playlist
	 * 
	 * @param _parentPlaylist the parent playlist
	 * @param _childNode the child node that was removed
	 * @param _index the index of the node that was removed
	 */
	public void playlistNodeRemoved(FIDPlaylist _parentPlaylist, IFIDNode _childNode, int _index);
}
