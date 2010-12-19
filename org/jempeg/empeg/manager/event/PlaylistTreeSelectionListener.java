/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package org.jempeg.empeg.manager.event;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDPlaylistWrapper;
import org.jempeg.nodestore.model.FIDPlaylistTreeNode;

import com.inzyme.container.ContainerSelection;
import com.inzyme.tree.IContainerTreeNode;

/**
* PlaylistTreeSelectionListener hooks tree selection events up to the 
* right-table so that as the tree selection changes, the table 
* model will be updated with the contents of the corresponding 
* tree node.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class PlaylistTreeSelectionListener implements TreeSelectionListener, FocusListener {
	private ApplicationContext myContext;
	private JTree myTree;
	
	public PlaylistTreeSelectionListener(ApplicationContext _context, JTree _tree) {
		myContext = _context;
		myTree = _tree;
	}
	
	public void valueChanged(TreeSelectionEvent _event) {
		selectionChanged(_event.isAddedPath());
	}
	
	private void selectionChanged(boolean _addedPath) {
		TreeSelectionModel selectionModel = myTree.getSelectionModel();
		IContainerTreeNode parentTreeNode;
		IContainerTreeNode selectedTreeNode;
		int childIndex;
		if (_addedPath) {
			TreePath selectionPath = selectionModel.getSelectionPath();
			if (selectionPath == null) {
				selectedTreeNode = null;
				parentTreeNode = null;
				childIndex = -1;
			} else {
				selectedTreeNode = (IContainerTreeNode)selectionPath.getLastPathComponent();
				parentTreeNode = (IContainerTreeNode)selectedTreeNode.getParent();
				if (parentTreeNode != null) {
					childIndex = parentTreeNode.getIndex(selectedTreeNode);
				} else {
					childIndex = -1;
				}				
			}
		} else {
			selectedTreeNode = null;
			parentTreeNode = null;
			childIndex = -1;
		}
		
		myContext.setSelectedContainer(selectedTreeNode);
		if (parentTreeNode == null) {
			// MODIFIED
			myContext.setSelection(myTree, null);
		} else {
			if (parentTreeNode instanceof IFIDPlaylistWrapper) {
				FIDPlaylist playlist = ((IFIDPlaylistWrapper)parentTreeNode).getPlaylist();
				childIndex = ((FIDPlaylistTreeNode)selectedTreeNode).getPlaylistIndex();
				// MODIFIED
				myContext.setSelection(myTree, new ContainerSelection(myContext, playlist, new int[] { childIndex }));
			} else {
				// MODIFIED
				myContext.setSelection(myTree, new ContainerSelection(myContext, parentTreeNode, new int[] { childIndex }));
			}
		}
	}
	
	public void focusGained(FocusEvent _event) {
		selectionChanged(true);
	}

	public void focusLost(FocusEvent _event) {
		// myContext.setSelection(myTree, null);
	}
}
