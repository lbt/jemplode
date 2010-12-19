/* ToStringSortableContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.container;
import java.util.Vector;

import org.jempeg.nodestore.model.NodeTag;

public class ToStringSortableContainer extends AbstractSortableContainer
{
    private String myName;
    private Vector myVector;
    
    public ToStringSortableContainer(Vector _vector) {
	this("VectorSortableContainer", _vector);
    }
    
    public ToStringSortableContainer(String _name, Vector _vector) {
	myName = _name;
	myVector = _vector;
    }
    
    protected Object getSortValue0(NodeTag _sortOnTag, Object _value) {
	return _value;
    }
    
    public String getName() {
	return myName;
    }
    
    public int getSize() {
	return myVector.size();
    }
    
    public Object getValueAt(int _index) {
	return myVector.elementAt(_index);
    }
}
