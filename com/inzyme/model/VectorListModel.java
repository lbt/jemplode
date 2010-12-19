/* VectorListModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.model;
import java.util.Vector;

import javax.swing.AbstractListModel;

public class VectorListModel extends AbstractListModel
{
    private Vector myVector;
    
    public VectorListModel(Vector _vector) {
	myVector = _vector;
    }
    
    public int getSize() {
	return myVector.size();
    }
    
    public Object getElementAt(int _index) {
	return myVector.elementAt(_index);
    }
    
    public void fireIntervalAdded(Object _source, int _index0, int _index1) {
	super.fireIntervalAdded(_source, _index0, _index1);
    }
    
    public void fireIntervalRemoved(Object _source, int _index0, int _index1) {
	super.fireIntervalRemoved(_source, _index0, _index1);
    }
    
    public void fireContentsChanged(Object _source, int _index0, int _index1) {
	super.fireContentsChanged(_source, _index0, _index1);
    }
}
