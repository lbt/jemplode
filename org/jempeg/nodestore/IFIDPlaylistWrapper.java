package org.jempeg.nodestore;


/**
 * IFIDPlaylistWrapper is implemented by any element that
 * can wrap an FIDPlaylist and provide some additional
 * interface for it.  This exists so that many different elements
 * that might actually represent a playlist can be passed into the
 * same methods (like PlaylistTreeNodes, PlaylistComboBoxes, Playlists themselves, etc)
 * 
 * @author Mike Schrag
 */
public interface IFIDPlaylistWrapper {
	/**
	 * Returns the FIDPlaylist that this element is proxying.
	 * 
	 * @return the FIDPlaylist that this element is proxying
	 */
	public FIDPlaylist getPlaylist();
}
