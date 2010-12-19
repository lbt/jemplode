package org.jempeg.nodestore;

/**
 * INodeDatabaseChange represents a database change that is 
 * associated with a particular node.
 * 
 * @author Mike Schrag
 */
public interface INodeDatabaseChange extends IDatabaseChange {
	/**
	 * Returns the node that this change is associated with.
	 * 
	 * @return the node that this change is associated with
	 */
	public IFIDNode getNode();
	
	/**
	 * Convenience method for comparing this change's node to the specified node.
	 * 
	 * @param _node the node to compare against
	 * @return whether or not this change's node equals _node
	 */
	public boolean nodeEquals(IFIDNode _node);
}
