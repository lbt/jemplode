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

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.jempeg.ApplicationContext;

/**
* TableDragListener implements simple drag-and-drop support for the playlist table
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class TableDragListener extends AbstractDragListener {
//	private EmplodeContext myContext;
	
	public TableDragListener(ApplicationContext _context) {
		super(_context.getFrame());
//		myContext = _context;
//		myTracker = _tracker;
//		myTracker.getTable().addMouseListener(this);
//		myTracker.getTable().addMouseMotionListener(this);
//		setCanDragOutside(false);
	}
	
	protected Object getDragSource(MouseEvent _event) {
//		Object dragSource = getValueAtPoint(_event.getPoint());
//		return dragSource;
    return null;
	}
	
	protected Object getValueAtPoint(Point _p) {
//		JTable table = myTracker.getTable();
//    PlaylistTableModelIfc tableModel = (PlaylistTableModelIfc)table.getModel();
//		int row = table.rowAtPoint(_p);
//		TreeNodeIfc node;
//		if (row != -1) {
//			node = tableModel.getValueAt(row);
//		} else {
//			node = null;
//		}
//		return node;
    return null;
	}
	
	protected Object getDropTarget(MouseEvent _event) {
//		Object dropTarget = getNodeAtPoint(_event.getPoint());
//		return dropTarget;
    return null;
	}
	
	protected String getDragTitle() {
//		AbstractFIDTreeNode dragSource = (AbstractFIDTreeNode)getDragSource();
//		String title = dragSource.toString();
//		return title;
    return null;
	}
	
	protected void drop(Object _dragSource, Object _dropTarget) {
//		AbstractFIDTreeNode dragSource = (AbstractFIDTreeNode)_dragSource;
//		AbstractFIDTreeNode dropTarget = (AbstractFIDTreeNode)_dropTarget;
//		AbstractFIDTreeNode parentNode = (AbstractFIDTreeNode)dragSource.getParent();
//		try {
//			int targetIndex = parentNode.getIndex(dropTarget) + 1;
//			int index = myEmplode.move(dragSource, parentNode, targetIndex);
//			JTable table = myTracker.getTable();
//			table.setRowSelectionInterval(index, index);
//			//myEmplode.updateView(parentNode);//(AbstractFIDTreeNode)parentNode.getChildAt(index));
//		}
//			catch (EmplodeException e) {
//				myEmplode.handleError(e);
//			}
	}
}
