/* AutoSyncSynchronizeQueueListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import com.inzyme.util.Timer;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.SynchronizeUI;
import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.SynchronizeQueue;
import org.jempeg.nodestore.event.ISynchronizeQueueListener;

public class AutoSyncSynchronizeQueueListener
    implements ISynchronizeQueueListener
{
    private ApplicationContext myContext;
    private Timer myTimer;
    
    public AutoSyncSynchronizeQueueListener(ApplicationContext _context) {
	myContext = _context;
	myTimer = new Timer(1000, this, "synchronize");
    }
    
    public void synchronize() {
	System.out.println
	    ("AutoSyncSynchronizeQueueListener.synchronize: autosyncing!");
	SynchronizeUI syncManager
	    = new SynchronizeUI(myContext.getPlayerDatabase(),
				myContext.getSynchronizeClient(),
				myContext.getFrame());
	syncManager.synchronizeInBackground
	    (myContext.getSynchronizeProgressListener());
    }
    
    public void databaseChangeEnqueued(SynchronizeQueue _queue,
				       IDatabaseChange _databaseChange) {
	myTimer.mark();
    }
    
    public void databaseChangeRequeued(SynchronizeQueue _queue,
				       IDatabaseChange _databaseChange) {
	/* empty */
    }
    
    public void databaseChangeDequeued(SynchronizeQueue _queue,
				       IDatabaseChange _databaseChange) {
	/* empty */
    }
    
    public void databaseChangesCleared(SynchronizeQueue _queue) {
	/* empty */
    }
}
