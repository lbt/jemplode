/* TreeComboBoxModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.tree;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.inzyme.model.Range;

public class TreeComboBoxModel implements ComboBoxModel
{
    private EventListenerList myEventListenerList = new EventListenerList();
    private TreeNode myRootNode;
    private Vector myOpenNodes = new Vector();
    private TreeNode mySelectedItem;
    private boolean myRootVisible;
    /*synthetic*/ static Class class$0;
    
    public TreeComboBoxModel(TreeNode _rootNode, boolean _rootVisible) {
	myRootNode = _rootNode;
	myRootVisible = _rootVisible;
	closeAllNodes();
    }
    
    public TreeNode getRootNode() {
	return myRootNode;
    }
    
    public void closeAllNodes() {
	openNode(myRootNode, true);
    }
    
    protected void closeAll(boolean _keepRootOpen) {
	while (myOpenNodes.size() > 0) {
	    TreeNode node = (TreeNode) myOpenNodes.elementAt(0);
	    closeNode(node);
	}
	if (_keepRootOpen)
	    openNode(myRootNode, false);
    }
    
    public void openNode(TreeNode _node, boolean _exclusive) {
	boolean nodesChanged = false;
	if (_exclusive) {
	    closeAll(true);
	    nodesChanged = true;
	}
	for (TreeNode node = _node; node != null; node = node.getParent()) {
	    if (node != null && !myOpenNodes.contains(node)) {
		myOpenNodes.addElement(node);
		nodeOpened(node);
		nodesChanged = true;
	    }
	}
	if (nodesChanged)
	    notifyStructureChanged();
    }
    
    public void closeNode(TreeNode _node) {
	if (closeNode0(_node))
	    notifyStructureChanged();
    }
    
    protected boolean closeNode0(TreeNode _node) {
	boolean nodesChanged = false;
	if (myOpenNodes.removeElement(_node)) {
	    nodesChanged = true;
	    nodeClosed(_node);
	    int childCount = _node.getChildCount();
	    for (int i = 0; i < childCount; i++)
		nodesChanged |= closeNode0(_node.getChildAt(i));
	}
	return nodesChanged;
    }
    
    protected void nodeOpened(TreeNode _node) {
	/* empty */
    }
    
    protected void nodeClosed(TreeNode _node) {
	/* empty */
    }
    
    public void addListDataListener(ListDataListener _listener) {
	EventListenerList eventlistenerlist = myEventListenerList;
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_0_;
	    try {
		var_class_0_
		    = Class.forName("javax.swing.event.ListDataListener");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_0_;
	}
	eventlistenerlist.add(var_class, _listener);
    }
    
    public Object getElementAt(int _index) {
	Object element;
	if (_index == 0)
	    element = myRootNode;
	else
	    element = getElementAt(myRootNode, myRootVisible ? 1 : 0, _index);
	return element;
    }
    
    public Object getSelectedItem() {
	return mySelectedItem;
    }
    
    public int getSize() {
	int size = getSize(myRootNode) + (myRootVisible ? 1 : 0);
	return size;
    }
    
    protected boolean isOpen(TreeNode _node) {
	return myOpenNodes.contains(_node);
    }
    
    protected Object getElementAt(TreeNode _startingNode, int _curIndex,
				  int _desiredIndex) {
	Object obj = null;
	int index = 0;
	int childCount = _startingNode.getChildCount();
	for (int i = 0; obj == null && i < childCount; i++) {
	    TreeNode childNode = _startingNode.getChildAt(i);
	    index++;
	    if (_curIndex + index - 1 == _desiredIndex)
		return childNode;
	    if (isOpen(childNode)) {
		Object o = getElementAt(childNode, _curIndex + index,
					_desiredIndex);
		if (o instanceof TreeNode)
		    obj = o;
		else {
		    Integer skipCount = (Integer) o;
		    index += skipCount.intValue();
		}
	    }
	}
	if (obj == null)
	    obj = new Integer(index);
	return obj;
    }
    
    protected int getSize(TreeNode _startingNode) {
	int size = 0;
	int childCount = _startingNode.getChildCount();
	for (int i = 0; i < childCount; i++) {
	    TreeNode childNode = _startingNode.getChildAt(i);
	    size++;
	    if (isOpen(childNode))
		size += getSize(childNode);
	}
	return size;
    }
    
    protected void notifyContentsChanged(int _startIndex, int _endIndex) {
	ListDataEvent event = null;
	Object[] listeners = myEventListenerList.getListenerList();
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    Object object = listeners[i];
	    Class var_class = class$0;
	    if (var_class == null) {
		Class var_class_1_;
		try {
		    var_class_1_
			= Class.forName("javax.swing.event.ListDataListener");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class = class$0 = var_class_1_;
	    }
	    if (object == var_class) {
		if (event == null)
		    event = new ListDataEvent(this, 0, _startIndex, _endIndex);
		((ListDataListener) listeners[i + 1]).contentsChanged(event);
	    }
	}
    }
    
    protected void notifyStructureChanged() {
	notifyContentsChanged(0, getSize());
    }
    
    protected void notifyChildrenWereInserted(int[] _indexes) {
	ListDataEvent event = null;
	Object[] listeners = myEventListenerList.getListenerList();
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    Object object = listeners[i];
	    Class var_class = class$0;
	    if (var_class == null) {
		Class var_class_2_;
		try {
		    var_class_2_
			= Class.forName("javax.swing.event.ListDataListener");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class = class$0 = var_class_2_;
	    }
	    if (object == var_class) {
		if (event == null) {
		    Range range = new Range(_indexes);
		    event = new ListDataEvent(this, 1, range.getStart(),
					      range.getEnd());
		}
		((ListDataListener) listeners[i + 1]).intervalAdded(event);
	    }
	}
    }
    
    protected void notifyChildrenWereRemoved(int[] _indexes) {
	ListDataEvent event = null;
	Object[] listeners = myEventListenerList.getListenerList();
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    Object object = listeners[i];
	    Class var_class = class$0;
	    if (var_class == null) {
		Class var_class_3_;
		try {
		    var_class_3_
			= Class.forName("javax.swing.event.ListDataListener");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class = class$0 = var_class_3_;
	    }
	    if (object == var_class) {
		if (event == null) {
		    Range range = new Range(_indexes);
		    event = new ListDataEvent(this, 2, range.getStart(),
					      range.getEnd());
		}
		((ListDataListener) listeners[i + 1]).intervalRemoved(event);
	    }
	}
    }
    
    public void removeListDataListener(ListDataListener _listener) {
	EventListenerList eventlistenerlist = myEventListenerList;
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_4_;
	    try {
		var_class_4_
		    = Class.forName("javax.swing.event.ListDataListener");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_4_;
	}
	eventlistenerlist.remove(var_class, _listener);
    }
    
    public void setSelectedItem(Object _selectedItem) {
	if (mySelectedItem != null && !mySelectedItem.equals(_selectedItem)
	    || mySelectedItem == null && _selectedItem != null) {
	    mySelectedItem = (TreeNode) _selectedItem;
	    notifyContentsChanged(-1, -1);
	}
    }
    
    public static void main(String[] _args) {
	DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("node1");
	DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("node2");
	DefaultMutableTreeNode node3 = new DefaultMutableTreeNode("node3");
	DefaultMutableTreeNode node4 = new DefaultMutableTreeNode("node4");
	DefaultMutableTreeNode node5 = new DefaultMutableTreeNode("node5");
	DefaultMutableTreeNode node6 = new DefaultMutableTreeNode("node6");
	DefaultMutableTreeNode node7 = new DefaultMutableTreeNode("node7");
	DefaultMutableTreeNode node8 = new DefaultMutableTreeNode("node8");
	DefaultMutableTreeNode node9 = new DefaultMutableTreeNode("node9");
	DefaultMutableTreeNode node10 = new DefaultMutableTreeNode("node10");
	DefaultMutableTreeNode node11 = new DefaultMutableTreeNode("node11");
	DefaultMutableTreeNode node12 = new DefaultMutableTreeNode("node12");
	DefaultMutableTreeNode node13 = new DefaultMutableTreeNode("node13");
	DefaultMutableTreeNode node14 = new DefaultMutableTreeNode("node14");
	DefaultMutableTreeNode node15 = new DefaultMutableTreeNode("node15");
	node1.add(node2);
	node2.add(node6);
	node2.add(node10);
	node10.add(node11);
	node1.add(node3);
	node3.add(node7);
	node1.add(node4);
	node4.add(node8);
	node4.add(node12);
	node12.add(node14);
	node12.add(node15);
	node4.add(node13);
	node1.add(node5);
	node5.add(node9);
	final TreeComboBoxModel comboBoxModel
	    = new TreeComboBoxModel(node1, true);
	JComboBox comboBox = new JComboBox(comboBoxModel);
	comboBox.setRenderer(new DefaultListCellRenderer() {
	    public Component getListCellRendererComponent
		(JList list, Object value, int index, boolean isSelected,
		 boolean cellHasFocus) {
		JLabel label
		    = ((JLabel)
		       super.getListCellRendererComponent(list, value, index,
							  isSelected,
							  cellHasFocus));
		TreeNode treeNode = (TreeNode) value;
		if (treeNode != null) {
		    StringBuffer sb = new StringBuffer();
		    for (TreeNode node = treeNode; node != null;
			 node = node.getParent())
			sb.append("    ");
		    label.setText(sb.toString() + treeNode.toString());
		}
		return label;
	    }
	});
	comboBox.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent _event) {
		if (_event.getStateChange() == 1) {
		    TreeNode selectedNode = (TreeNode) _event.getItem();
		    comboBoxModel.openNode(selectedNode, true);
		}
	    }
	});
	JFrame jf = new JFrame("Test");
	jf.setDefaultCloseOperation(3);
	jf.getContentPane().add(comboBox);
	jf.pack();
	jf.show();
    }
}
