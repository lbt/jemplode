package org.jempeg.nodestore.soup;

import org.jempeg.nodestore.FIDPlaylist;

/**
 * Implemented by anything that wants to be notified when the soup is finished updating.
 * 
 * @author Mike Schrag
 */
public interface ISoupListener {
	/**
	 * Called after a soup is finished initializing.
	 * 
	 * @param _soupUpdater the soup updater that finished
	 * @param _soupPlaylist the playlist that was "souped"
	 */
	public void soupInitialized(SoupUpdater _soupUpdater, FIDPlaylist _soupPlaylist);
}
