package com.inzyme.container;

import org.jempeg.ApplicationContext;

import com.inzyme.model.IntVector;

/**
 * ContainerSelection represents a set of selected elements
 * of an IContainer.
 * 
 * @author Mike Schrag
 */
public class ContainerSelection implements IContainer {
	private ApplicationContext myContext;
	private IContainer myContainer;
	private int[] mySelectedIndexes;
	
	/**
	 * Constructor for ContainerSelection.
	 */
	public ContainerSelection(ApplicationContext _context, IContainer _container, int[] _selectedIndexes) {
		myContext = _context;
		myContainer = _container;
		mySelectedIndexes = _selectedIndexes;
	}
	
	public ApplicationContext getContext() {
		return myContext;
	}

	/**
	 * Returns whether or not the selection contains the given object.
	 * 
	 * @param _obj the object to check for
	 * @return whether or not the selection contains the given object
	 */
	public boolean contains(Object _obj) {
		boolean contains = false;
		int size = getSize();
		for (int i = 0; !contains && i < size; i ++) {
			Object obj = getValueAt(i);
			if (obj != null && obj.equals(_obj)) {
				contains = true;
			}
		}
		return contains;
	}
	
	/**
	 * Returns the container for this selection.
	 * 
	 * @return the container for this selection
	 */
	public IContainer getContainer() {
		return myContainer;
	}

	/**
	 * Returns the selected indexes from the container.
	 * 
	 * @return the selected indexes from the container
	 */
	public int[] getSelectedIndexes() {
		return mySelectedIndexes;
	}
	
	/**
	 * Returns the set of selected values.
	 * 
	 * @return the set of selected values
	 */
	public Object[] getSelectedValues() {
		Object[] values = new Object[mySelectedIndexes.length];
		for (int i = 0; i < values.length; i ++) {
			values[i] = getValueAt(i);
		}
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
	
	/**
	 * Returns the selection index at the given value.
	 * 
	 * @param _index the index to lookup
	 * @return the selection index at the given value
	 */
	public int getIndexAt(int _index) {
		return mySelectedIndexes[_index];
	}
	
	/**
	 * Returns a new ContainerSelection that doesn't contain
	 * any "subcontainers" in it.
	 * 
	 * @return a container that doesn't contain containers
	 */
	public ContainerSelection pruneContainers() {
		int size = getSize();
		IntVector indexesVec = new IntVector(size);
		for (int i = 0; i < size; i ++) {
			Object obj = getValueAt(i);
			if (!(obj instanceof IContainer)) {
				indexesVec.add(mySelectedIndexes[i]);
			}
		}
		int[] selectedIndexes = indexesVec.toArray();
		ContainerSelection selection = new ContainerSelection(myContext, myContainer, selectedIndexes);
		return selection; 
	}
	
	public String toString() {
		return "[ContainerSelection: container = " + myContainer + "; size = " + getSize() + "]";
	}
}
