package org.jempeg.nodestore;

public interface ICancelableDatabaseChange extends IDatabaseChange {
	public void cancel();
}
