/* DoubleClickPlaylistTreeOpener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.event;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.FIDPlaylist;

public class DoubleClickPlaylistTreeOpener extends MouseAdapter
{
    private ApplicationContext myContext;
    private JTree myTree;
    
    public DoubleClickPlaylistTreeOpener(ApplicationContext _context,
					 JTree _tree) {
	myContext = _context;
	myTree = _tree;
    }
    
    public void mouseClicked(MouseEvent _event) {
	if (_event.getClickCount() == 2
	    && (_event.getModifiers() & 0x10) == 16) {
	    ContainerSelection selection = myContext.getSelection();
	    if (selection != null) {
		int index = -1;
		int size = selection.getSize();
		for (int i = 0; index == -1 && i < size; i++) {
		    if (selection.getValueAt(i) instanceof IContainer)
			index = selection.getIndexAt(i);
		}
		if (index != -1) {
		    IContainer container = selection.getContainer();
		    int newIndex;
		    if (container instanceof FIDPlaylist) {
			FIDPlaylist playlist = (FIDPlaylist) container;
			int playlistCount = 0;
			for (int i = 0; i < index; i++) {
			    FIDPlaylist childPlaylist
				= playlist.getPlaylistAt(i);
			    if (childPlaylist != null)
				playlistCount++;
			}
			newIndex = playlistCount;
		    } else
			newIndex = index;
		    TreePath currentPath = myTree.getSelectionPath();
		    TreeNode currentTreeNode
			= (TreeNode) currentPath.getLastPathComponent();
		    Object[] currentPathObjs = currentPath.getPath();
		    Object[] newPathObjs
			= new Object[currentPathObjs.length + 1];
		    System.arraycopy(currentPathObjs, 0, newPathObjs, 0,
				     currentPathObjs.length);
		    newPathObjs[newPathObjs.length - 1]
			= currentTreeNode.getChildAt(newIndex);
		    TreePath newPath = new TreePath(newPathObjs);
		    myTree.setSelectionPath(newPath);
		}
	    }
	}
    }
}
