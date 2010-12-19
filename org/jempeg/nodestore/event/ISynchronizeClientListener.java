package org.jempeg.nodestore.event;

import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.PlayerDatabase;

public interface ISynchronizeClientListener {
	public void downloadStarted(PlayerDatabase _playerDatabase);
	public void downloadCompleted(PlayerDatabase _playerDatabase);
	public void synchronizeStarted(PlayerDatabase _playerDatabase);
	public void synchronizeStarted(IDatabaseChange _databaseChange);
	public void synchronizeInProgress(IDatabaseChange _databaseChange, long _current, long _total);
	public void synchronizeCompleted(IDatabaseChange _databaseChange, boolean _successfully);
	public void synchronizeCompleted(PlayerDatabase _playerDatabase, boolean _succesfully);
}
