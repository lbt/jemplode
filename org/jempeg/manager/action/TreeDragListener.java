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
package org.jempeg.manager.action;

import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;

import org.jempeg.ApplicationContext;

/**
* TreeDragListener implements simple drag-and-drop support for the playlist tree
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class TreeDragListener implements DragSourceListener {
//	private EmplodeContext myContext;
	
	public TreeDragListener(ApplicationContext _context) {
//		super(_frame);
//		myEmplode = _emplode;
//		myTracker = _tracker;
//		myTracker.getTree().addMouseListener(this);
//		myTracker.getTree().addMouseMotionListener(this);
//		setCanDragOutside(false);
	}
	
  public void dragDropEnd(DragSourceDropEvent dsde) {
  }

  public void dragEnter(DragSourceDragEvent dsde) {
  }

  public void dragOver(DragSourceDragEvent dsde) {
  }

  public void dropActionChanged(DragSourceDragEvent dsde) {
  }

  public void dragExit(DragSourceEvent dse) {
  }

	protected Object getDragSource(MouseEvent _event) {
//		JTree tree = myTracker.getTree();
//		TreePath dragSource = tree.getPathForLocation(_event.getX(), _event.getY());
//		if (dragSource != null) {
//			AbstractFIDTreeNode node = (AbstractFIDTreeNode)dragSource.getLastPathComponent();
//			return node;
//		} else {
//			return null;
//		}
    return null;
	}
	
	protected Object getDropTarget(MouseEvent _event) {
//		JTree tree = myTracker.getTree();
//		Object dropTarget = tree.getPathForLocation(_event.getX(), _event.getY());
//		return dropTarget;
    return null;
	}
	
	protected String getDragTitle() {
//		AbstractFIDTreeNode dragSource = (AbstractFIDTreeNode)getDragSource();
//		String title = dragSource.toString();
//		return title;
    return "";
	}
	
	protected void drop(Object _dragSource, Object _dropTarget) {
//		TreePath dropTargetPath = (TreePath)getDropTarget();
//		TreeNodeIfc dropTarget = (TreeNodeIfc)dropTargetPath.getLastPathComponent();
//		int targetIndex;
//		if (!(dropTarget instanceof PlaylistTreeNodeIfc)) {
//			AbstractFIDTreeNode dropTargetParent = (AbstractFIDTreeNode)dropTarget.getParent();
//			targetIndex = dropTargetParent.getIndex(dropTarget) + 1;
//			dropTarget = dropTargetParent;
//		} else {
//			targetIndex = dropTarget.getRealChildCount();
//		}
//		
//		AbstractFIDTreeNode dragSource = (AbstractFIDTreeNode)getDragSource();
//		AbstractFIDTreeNode parentNode = (AbstractFIDTreeNode)dragSource.getParent();
//		if (parentNode != null) {
//			try {
//				int index = myEmplode.move(dragSource, dropTarget, targetIndex);
//				//myEmplode.updateView(dropTarget);
//			}
//				catch (EmplodeException e) {
//					myEmplode.handleError(e);
//				}
//		}
	}
}
