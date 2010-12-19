package com.inzyme.tree;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * BasicContainerTreeNode is a DefaultMutableTreeNode that
 * implements the IContainerTreeNode interface.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.2 $
 */
public class BasicContainerTreeNode extends DefaultMutableTreeNode implements IContainerTreeNode {
	private int myType;
	private int myContainedType;
	
	/**
	 * Constructor for BasicContainerTreeNode.
	 * 
	 * @param _title the title of this node
	 * @param _type the type of this node
	 */
	public BasicContainerTreeNode(String _title, int _type, int _containedType) {
		super(_title);
		myType = _type;
		myContainedType = _containedType;
	}

	public int getType() {
		return myType;
	}
	
	public void setType(int _type) {
		myType = _type;
	}
	
	public void setContainedType(int _containedType) {
		myContainedType = _containedType;
	}
	
	public int getContainedType() {
		return myContainedType;
	}
	
	public int getSize() {
		return getChildCount();
	}

	public Object getValueAt(int _index) {
		return getChildAt(_index);
	}

	public String getName() {
		return (String)getUserObject();
	}
}
