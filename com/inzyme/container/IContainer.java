package com.inzyme.container;

/**
 * IContainer is the parent of either a playlist 
 * tree node or a playlist table model
 * 
 * @author Mike Schrag
 */
public interface IContainer {
	/**
	 * Returns the name of this container.
	 * 
	 * @return the name of this container
	 */
	public String getName();
	
	/**
	 * Returns the size of this container.
	 * 
	 * @return the size of this container
	 */
	public int getSize();

	/**
	 * Returns the value at the given index.
	 * 
	 * @param _index the index to lookup
	 * @return the value at the given index
	 */
	public Object getValueAt(int _index);
}
