package org.jempeg.nodestore.event;

import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.SynchronizeQueue;

public interface ISynchronizeQueueListener {
	public void databaseChangeEnqueued(SynchronizeQueue _queue, IDatabaseChange _databaseChange);

	public void databaseChangeDequeued(SynchronizeQueue _queue, IDatabaseChange _databaseChange);

	public void databaseChangeRequeued(SynchronizeQueue _queue, IDatabaseChange _databaseChange);

	public void databaseChangesCleared(SynchronizeQueue _queue);
}
