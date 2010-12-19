package org.jempeg.nodestore.event;

import java.util.EventListener;

import org.jempeg.nodestore.IFIDNode;

/**
 * INodeTagListener is an interface that is implemented by 
 * components that want to receive notifications about
 * when a node's tags are modified (like the UI)
 * 
 * @author Mike Schrag
 */
public interface INodeTagListener extends EventListener {
	/**
	 * Fired when a node's tags are initialized.
	 * 
	 * @param _node the node that was identified
	 */
	public void nodeIdentified(IFIDNode _node);

	/**
	 * Fired before a node's tags are modified.
	 * 
	 * @param _node the node that was modified
	 * @param _tag the tag name that was modified
	 * @param _oldvalue the old value of the tag
	 * @param _newValue the new value of the tag
	 */
	public void beforeNodeTagModified(IFIDNode _node, String _tag, String _oldValue, String _newValue);
	
	/**
	 * Fired after a node's tags are modified.
	 * 
	 * @param _node the node that was modified
	 * @param _tag the tag name that was modified
	 * @param _oldvalue the old value of the tag
	 * @param _newValue the new value of the tag
	 */
	public void afterNodeTagModified(IFIDNode _node, String _tag, String _oldValue, String _newValue);
}
