/* TransientSoupAttacher - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.ISynchronizeClientListener;

public class TransientSoupAttacher implements ISynchronizeClientListener
{
    private boolean myThreaded;
    
    public TransientSoupAttacher(boolean _threaded) {
	myThreaded = _threaded;
    }
    
    public void downloadStarted(PlayerDatabase _playerDatabase) {
	/* empty */
    }
    
    public void downloadCompleted(PlayerDatabase _playerDatabase) {
	SoupUtils.loadTransientSoupPlaylists(_playerDatabase, myThreaded);
    }
    
    public void synchronizeCompleted(IDatabaseChange _databaseChange,
				     boolean _successfully) {
	/* empty */
    }
    
    public void synchronizeCompleted(PlayerDatabase _playerDatabase,
				     boolean _succesfully) {
	/* empty */
    }
    
    public void synchronizeInProgress(IDatabaseChange _databaseChange,
				      long _current, long _total) {
	/* empty */
    }
    
    public void synchronizeStarted(IDatabaseChange _databaseChange) {
	/* empty */
    }
    
    public void synchronizeStarted(PlayerDatabase _playerDatabase) {
	/* empty */
    }
}
