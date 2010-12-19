package com.inzyme.container;

import org.jempeg.nodestore.model.NodeTag;

/**
 * ISortableContainer is implemented by any container that wants to be sortable.
 * 
 * @author Mike Schrag
 */
public interface ISortableContainer extends IContainer {
	/**
	 * Returns the value to sort on at the given index.
	 * 
	 * @param _index the index to lookup
	 * @return Object the value to sort on
	 */
	public Object getSortValueAt(NodeTag _sortOnNodeTag, int _index);
	
	/**
	 * Returns the sort value for the given object.
	 * 
	 * @param _value the value to get a sort value for
	 * @return Object the sort value for the given object
	 */
	public Object getSortValue(NodeTag _sortOnNodeTag, Object _value);
}
