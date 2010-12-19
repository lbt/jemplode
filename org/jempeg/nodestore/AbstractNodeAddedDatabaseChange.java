package org.jempeg.nodestore;

public abstract class AbstractNodeAddedDatabaseChange extends AbstractNodeDatabaseChange implements ICancelableDatabaseChange {
	public AbstractNodeAddedDatabaseChange(IFIDNode _node) {
		super(_node);
	}
	
	public long getLength() {
		return getNode().getLength();
	}
	
	public void cancel() {
		getNode().delete();
	}
}
