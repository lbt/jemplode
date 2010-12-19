/* ContainerSelection - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.container;
import com.inzyme.model.IntVector;

import org.jempeg.ApplicationContext;

public class ContainerSelection implements IContainer
{
    private ApplicationContext myContext;
    private IContainer myContainer;
    private int[] mySelectedIndexes;
    
    public ContainerSelection(ApplicationContext _context,
			      IContainer _container, int[] _selectedIndexes) {
	myContext = _context;
	myContainer = _container;
	mySelectedIndexes = _selectedIndexes;
    }
    
    public ApplicationContext getContext() {
	return myContext;
    }
    
    public boolean contains(Object _obj) {
	boolean contains = false;
	int size = getSize();
	for (int i = 0; !contains && i < size; i++) {
	    Object obj = getValueAt(i);
	    if (obj != null && obj.equals(_obj))
		contains = true;
	}
	return contains;
    }
    
    public IContainer getContainer() {
	return myContainer;
    }
    
    public int[] getSelectedIndexes() {
	return mySelectedIndexes;
    }
    
    public Object[] getSelectedValues() {
	Object[] values = new Object[mySelectedIndexes.length];
	for (int i = 0; i < values.length; i++)
	    values[i] = getValueAt(i);
	return values;
    }
    
    public String getName() {
	return "Selection from " + myContainer.getName();
    }
    
    public int getSize() {
	return mySelectedIndexes.length;
    }
    
    public Object getValueAt(int _index) {
	Object obj = myContainer.getValueAt(mySelectedIndexes[_index]);
	return obj;
    }
    
    public int getIndexAt(int _index) {
	return mySelectedIndexes[_index];
    }
    
    public ContainerSelection pruneContainers() {
	int size = getSize();
	IntVector indexesVec = new IntVector(size);
	for (int i = 0; i < size; i++) {
	    Object obj = getValueAt(i);
	    if (!(obj instanceof IContainer))
		indexesVec.add(mySelectedIndexes[i]);
	}
	int[] selectedIndexes = indexesVec.toArray();
	ContainerSelection selection
	    = new ContainerSelection(myContext, myContainer, selectedIndexes);
	return selection;
    }
    
    public String toString() {
	return ("[ContainerSelection: container = " + myContainer + "; size = "
		+ getSize() + "]");
    }
}
