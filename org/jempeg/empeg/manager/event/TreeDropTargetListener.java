/* TreeDropTargetListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.event;
import java.awt.Point;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.inzyme.container.IContainer;
import com.inzyme.util.Timer;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.AbstractFileDropTargetListener;

public class TreeDropTargetListener extends AbstractFileDropTargetListener
{
    private JTree myTree;
    private Timer myExpandTimer;
    private int myRow;
    
    public TreeDropTargetListener(ApplicationContext _context, JTree _tree) {
	super(_context);
	myTree = _tree;
	myExpandTimer = new Timer(1000, this, "open");
	myRow = -1;
    }
    
    public void dragEnter(DropTargetDragEvent _event) {
	super.dragEnter(_event);
	myTree.requestFocus();
	myRow = -1;
    }
    
    public void dragOver(DropTargetDragEvent _event) {
	super.dragOver(_event);
	getTreeNode(_event);
    }
    
    public void dragExit(DropTargetDragEvent _event) {
	super.dragExit(_event);
	myExpandTimer.stop();
	myRow = -1;
    }
    
    protected boolean isValid(DropTargetEvent _event) {
	TreeNode treeNode = getTreeNode(_event);
	boolean valid = treeNode != null;
	return valid;
    }
    
    protected TreeNode getTreeNode(DropTargetEvent _event) {
	TreeNode treeNode = null;
	Point loc = null;
	if (_event instanceof DropTargetDragEvent)
	    loc = ((DropTargetDragEvent) _event).getLocation();
	else if (_event instanceof DropTargetDropEvent)
	    loc = ((DropTargetDropEvent) _event).getLocation();
	int row = -1;
	if (loc != null) {
	    row = myTree.getRowForLocation(loc.x, loc.y);
	    if (row != myRow)
		myExpandTimer.mark();
	    TreePath dropTargetPath
		= myTree.getPathForLocation((int) loc.getX(),
					    (int) loc.getY());
	    if (dropTargetPath != null)
		treeNode = (TreeNode) dropTargetPath.getLastPathComponent();
	}
	myRow = row;
	return treeNode;
    }
    
    protected IContainer getTargetContainer(DropTargetDropEvent _event) {
	TreeNode treeNode = getTreeNode(_event);
	IContainer container = (IContainer) treeNode;
	return container;
    }
    
    public void open() {
	if (myRow != -1)
	    myTree.expandRow(myRow);
    }
}
