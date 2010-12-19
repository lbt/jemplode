package com.inzyme.container;

import org.jempeg.nodestore.model.NodeTag;

/**
 * @author Mike Schrag
 */
public abstract class AbstractSortableContainer implements ISortableContainer {
	public Object getSortValueAt(NodeTag _sortOnNodeTag, int _index) {
		Object sortValue = getSortValue(_sortOnNodeTag, getValueAt(_index));
		return sortValue;
	}
	
	public Object getSortValue(NodeTag _sortOnNodeTag, Object _value) {
		Object sortValue = getSortValue0(_sortOnNodeTag, _value);
		if (sortValue instanceof IContainer) {
			sortValue = ((IContainer)sortValue).getName();
		}
		return sortValue;
	}
	
	protected abstract Object getSortValue0(NodeTag _sortOnNodeTag, Object _value);
}
