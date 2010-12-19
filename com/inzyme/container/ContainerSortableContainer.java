/* ContainerSortableContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.container;
import org.jempeg.nodestore.model.NodeTag;

public class ContainerSortableContainer extends AbstractSortableContainer
{
    private IContainer myContainer;
    private ISortableContainer mySortableContainer;
    
    public ContainerSortableContainer(IContainer _container) {
	myContainer = _container;
	if (_container instanceof ISortableContainer)
	    mySortableContainer = (ISortableContainer) _container;
    }
    
    protected Object getSortValue0(NodeTag _sortOnNodeTag, Object _value) {
	Object sortValue;
	if (mySortableContainer != null)
	    sortValue
		= mySortableContainer.getSortValue(_sortOnNodeTag, _value);
	else
	    sortValue = _value;
	return sortValue;
    }
    
    public String getName() {
	return myContainer.getName();
    }
    
    public int getSize() {
	return myContainer.getSize();
    }
    
    public Object getValueAt(int _index) {
	return myContainer.getValueAt(_index);
    }
}
