package com.inzyme.tree;

import javax.swing.tree.TreeNode;

import org.jempeg.nodestore.model.NodeTag;

import com.inzyme.container.AbstractSortableContainer;

/**
 * @author Mike Schrag
 */
public class TreeNodeSortableContainer extends AbstractSortableContainer {
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
