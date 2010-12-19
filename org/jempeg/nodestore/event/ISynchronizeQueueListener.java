/* ISynchronizeQueueListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.event;
import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.SynchronizeQueue;

public interface ISynchronizeQueueListener
{
    public void databaseChangeEnqueued(SynchronizeQueue synchronizequeue,
				       IDatabaseChange idatabasechange);
    
    public void databaseChangeDequeued(SynchronizeQueue synchronizequeue,
				       IDatabaseChange idatabasechange);
    
    public void databaseChangeRequeued(SynchronizeQueue synchronizequeue,
				       IDatabaseChange idatabasechange);
    
    public void databaseChangesCleared(SynchronizeQueue synchronizequeue);
}
