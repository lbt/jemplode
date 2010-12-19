package org.jempeg.nodestore.soup;

import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.ISynchronizeClientListener;

/**
 * TransientSoupAttacher is responsible for 
 * listening for the tree model to change so it can 
 * create and attach the transient soups (i.e. soups
 * that only exist in Emplode vs on the Empeg)
 * 
 * @author Mike Schrag
 */
public class TransientSoupAttacher implements ISynchronizeClientListener {
	private boolean myThreaded;
	
	public TransientSoupAttacher(boolean _threaded) {
		myThreaded = _threaded;
	}
	
	public void downloadStarted(PlayerDatabase _playerDatabase) {
	}

	public void downloadCompleted(PlayerDatabase _playerDatabase) {
		SoupUtils.loadTransientSoupPlaylists(_playerDatabase, myThreaded);
	}
	
	public void synchronizeCompleted(IDatabaseChange _databaseChange, boolean _successfully) {
	}
	
	public void synchronizeCompleted(PlayerDatabase _playerDatabase, boolean _succesfully) {
	}
	
	public void synchronizeInProgress(IDatabaseChange _databaseChange, long _current, long _total) {
	}
	
	public void synchronizeStarted(IDatabaseChange _databaseChange) {
	}
	
	public void synchronizeStarted(PlayerDatabase _playerDatabase) {
	}
}
