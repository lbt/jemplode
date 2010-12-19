package com.inzyme.container;

/**
 * An IMutableTypeContainer is a container whose type
 * can be modified.
 * 
 * @author Mike Schrag
 */
public interface IMutableTypeContainer extends IContainer {
  public static final int TYPE_ROOT     = 0;
  public static final int TYPE_FOLDER   = 1;
  
	/**
	 * Returns the type of this container.  This is
	 * primarily used to determine the type of icon
	 * that is displayed next to the node.
	 * 
	 * @return the type of this tree node
	 */
	public int getType();
	
	/**
	 * Sets the type of this container.
	 * 
	 * @param _type the type of this container
	 */
	public void setType(int _type);
	
	public void setContainedType(int _containedType);
	
	public int getContainedType();
}
