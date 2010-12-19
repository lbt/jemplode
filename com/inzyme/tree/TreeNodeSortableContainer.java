/* TreeNodeSortableContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.tree;
import javax.swing.tree.TreeNode;

import com.inzyme.container.AbstractSortableContainer;

import org.jempeg.nodestore.model.NodeTag;

public class TreeNodeSortableContainer extends AbstractSortableContainer
{
    private TreeNode myTreeNode;
    
    public TreeNodeSortableContainer(TreeNode _treeNode) {
	myTreeNode = _treeNode;
    }
    
    protected Object getSortValue0(NodeTag _sortOnNodeTag, Object _value) {
	return _value;
    }
    
    public String getName() {
	return myTreeNode.toString();
    }
    
    public int getSize() {
	return myTreeNode.getChildCount();
    }
    
    public Object getValueAt(int _index) {
	return myTreeNode.getChildAt(_index);
    }
}
