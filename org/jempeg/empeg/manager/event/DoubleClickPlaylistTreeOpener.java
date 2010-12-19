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

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.FIDPlaylist;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;

/**
* DoubleClickPlaylistTreeOpener is responsible for double-clicks in the table
* opening a playlist.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class DoubleClickPlaylistTreeOpener extends MouseAdapter {
	private ApplicationContext myContext;
	private JTree myTree;

	/**
	* Constructs a OpenPlaylistTableToTreeAdapter.
	*
	* @param _context the context to work within
	* @param _tree the Tree to modify selections for
	*/
	public DoubleClickPlaylistTreeOpener(ApplicationContext _context, JTree _tree) {
		myContext = _context;
		myTree = _tree;
	}

	public void mouseClicked(MouseEvent _event) {
		if (_event.getClickCount() == 2 && ((_event.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)) {
			ContainerSelection selection = myContext.getSelection();
			if (selection != null) {
				int index = -1;
				int size = selection.getSize();
				for (int i = 0; index == -1 && i < size; i++) {
					if (selection.getValueAt(i) instanceof IContainer) {
						index = selection.getIndexAt(i);
					}
				}

				if (index != -1) {
					int newIndex;
					IContainer container = selection.getContainer();
					if (container instanceof FIDPlaylist) {
						FIDPlaylist playlist = (FIDPlaylist) container;
						int playlistCount = 0;
						for (int i = 0; i < index; i++) {
							FIDPlaylist childPlaylist = playlist.getPlaylistAt(i);
							if (childPlaylist != null) {
								playlistCount++;
							}
						}
						newIndex = playlistCount;
					} else {
						newIndex = index;
					}

					TreePath currentPath = myTree.getSelectionPath();
					TreeNode currentTreeNode = (TreeNode) currentPath.getLastPathComponent();
					Object[] currentPathObjs = currentPath.getPath();
					Object[] newPathObjs = new Object[currentPathObjs.length + 1];
					System.arraycopy(currentPathObjs, 0, newPathObjs, 0, currentPathObjs.length);
					newPathObjs[newPathObjs.length - 1] = currentTreeNode.getChildAt(newIndex);
					TreePath newPath = new TreePath(newPathObjs);
					myTree.setSelectionPath(newPath);
				}
			}
		}
	}
}
