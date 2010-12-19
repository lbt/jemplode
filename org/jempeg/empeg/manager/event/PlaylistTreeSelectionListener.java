/* PlaylistTreeSelectionListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.event;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.inzyme.container.ContainerSelection;
import com.inzyme.tree.IContainerTreeNode;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.IFIDPlaylistWrapper;
import org.jempeg.nodestore.model.FIDPlaylistTreeNode;

public class PlaylistTreeSelectionListener
    implements TreeSelectionListener, FocusListener
{
    private ApplicationContext myContext;
    private JTree myTree;
    
    public PlaylistTreeSelectionListener(ApplicationContext _context,
					 JTree _tree) {
	myContext = _context;
	myTree = _tree;
    }
    
    public void valueChanged(TreeSelectionEvent _event) {
	selectionChanged(_event.isAddedPath());
    }
    
    private void selectionChanged(boolean _addedPath) {
	TreeSelectionModel selectionModel = myTree.getSelectionModel();
	IContainerTreeNode selectedTreeNode;
	IContainerTreeNode parentTreeNode;
	int childIndex;
	if (_addedPath) {
	    TreePath selectionPath = selectionModel.getSelectionPath();
	    if (selectionPath == null) {
		selectedTreeNode = null;
		parentTreeNode = null;
		childIndex = -1;
	    } else {
		selectedTreeNode = ((IContainerTreeNode)
				    selectionPath.getLastPathComponent());
		parentTreeNode
		    = (IContainerTreeNode) selectedTreeNode.getParent();
		if (parentTreeNode != null)
		    childIndex = parentTreeNode.getIndex(selectedTreeNode);
		else
		    childIndex = -1;
	    }
	} else {
	    selectedTreeNode = null;
	    parentTreeNode = null;
	    childIndex = -1;
	}
	myContext.setSelectedContainer(selectedTreeNode);
	if (parentTreeNode == null)
	    myContext.setSelection(myTree, null);
	else if (parentTreeNode instanceof IFIDPlaylistWrapper) {
	    org.jempeg.nodestore.FIDPlaylist playlist
		= ((IFIDPlaylistWrapper) parentTreeNode).getPlaylist();
	    childIndex
		= ((FIDPlaylistTreeNode) selectedTreeNode).getPlaylistIndex();
	    myContext.setSelection(myTree,
				   new ContainerSelection(myContext, playlist,
							  (new int[]
							   { childIndex })));
	} else
	    myContext.setSelection(myTree,
				   new ContainerSelection(myContext,
							  parentTreeNode,
							  (new int[]
							   { childIndex })));
    }
    
    public void focusGained(FocusEvent _event) {
	selectionChanged(true);
    }
    
    public void focusLost(FocusEvent _event) {
	/* empty */
    }
}
