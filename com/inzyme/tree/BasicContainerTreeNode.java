/* BasicContainerTreeNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.tree;
import javax.swing.tree.DefaultMutableTreeNode;

public class BasicContainerTreeNode extends DefaultMutableTreeNode
    implements IContainerTreeNode
{
    private int myType;
    private int myContainedType;
    
    public BasicContainerTreeNode(String _title, int _type,
				  int _containedType) {
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
	return (String) getUserObject();
    }
}
