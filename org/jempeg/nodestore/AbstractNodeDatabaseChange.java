package org.jempeg.nodestore;

import com.inzyme.util.ReflectionUtils;

public abstract class AbstractNodeDatabaseChange implements INodeDatabaseChange {
	private IFIDNode myNode;
	private int myAttempt;
	
	public AbstractNodeDatabaseChange(IFIDNode _node) {
		myNode = _node;
	}
	
	public void setAttempt(int _attempt) {
		myAttempt = _attempt;
	}
	
	public int getAttempt() {
		return myAttempt;
	}
	
	public void incrementAttempt() {
		myAttempt ++;
	}
	
	public long getFID() {
		return myNode.getFID();
	}
	
	public IFIDNode getNode() {
		return myNode;
	}
	
	public String getName() {
		return myNode.getTitle();
	}
	
	public boolean equals(Object _obj) {
		return _obj == this;
	}
	
	public boolean nodeEquals(IFIDNode _node) {
		return myNode.equals(_node);
	}
	
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
