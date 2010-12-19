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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

/**
* AbstractDragListener implements simple drag-and-drop support for components.  This
* handles autoscrolling.  If only we could use J2SE for "real" dnd :(
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public abstract class AbstractDragListener implements MouseListener, MouseMotionListener {
	private static final int DRAG_THRESHOLD = 10;
	
	private JFrame myFrame;
	private JLabel myDragLabel;
	private Object myDragSource;
	private Object myDropTarget;
	private Rectangle myDragRectangle;
	private boolean myCanDragOutside;
	private boolean myDragging;
	private Point myStartingPoint;
	
	public AbstractDragListener(JFrame _frame) {
		myFrame = _frame;
		myDragRectangle = new Rectangle();
	}
	
	protected abstract Object getDragSource(MouseEvent _event);
	
	protected abstract Object getDropTarget(MouseEvent _event);
	
	protected abstract String getDragTitle();
	
	protected abstract void drop(Object _dragSource, Object _dropTarget);
	
	protected Object getDragSource() {
		return myDragSource;
	}
	
	protected Object getDropTarget() {
		return myDropTarget;
	}
	
	protected void setCanDragOutside(boolean _canDragOutside) {
		myCanDragOutside = _canDragOutside;
	}
	
	public void mouseMoved(MouseEvent _event) {
	}
	
	public void mouseDragged(MouseEvent _event) {
		if (myDragSource != null) {
			if (!myDragging && (Math.abs(myStartingPoint.x - _event.getX()) > DRAG_THRESHOLD || Math.abs(myStartingPoint.y - _event.getY()) > DRAG_THRESHOLD)) {
				myDragging = true;
			}
		}
		if (myDragging) {
			JComponent comp = (JComponent)_event.getSource();
			int ex = _event.getX();
			int ey = _event.getY();
			int x = Math.min(Math.max(0, ex), comp.getWidth());
			int y = Math.min(Math.max(0, ey), comp.getHeight());
			myDragRectangle.setBounds(x, y, 1, 1);
			comp.scrollRectToVisible(myDragRectangle);
			
			if (!myCanDragOutside && ((ex < 0 || ex > comp.getWidth()) || (ey < 0 || ey > comp.getHeight()))) {
				removeDragLabel();
			} else {
				if (myDragLabel == null) {
					myDragLabel = new JLabel(getDragTitle());
					myDragLabel.setSize(myDragLabel.getPreferredSize());
					myDragLabel.setOpaque(true);
					myFrame.getLayeredPane().add(myDragLabel, JLayeredPane.DRAG_LAYER);
				}
				Point np = SwingUtilities.convertPoint(comp, ex, ey, myFrame);
				myDragLabel.setLocation(np);
			}
		}
	}
	
	protected void removeDragLabel() {
		if (myDragLabel != null) {
			myFrame.getLayeredPane().remove(myDragLabel);
			myFrame.getLayeredPane().repaint();
			myDragLabel = null;
			myDropTarget = null;
		}
	}
	
	public void mouseClicked(MouseEvent _event) {
	}
		
	public void mousePressed(MouseEvent _event) {
		if (myDragSource == null) {
			myStartingPoint = _event.getPoint();
			myDragSource = getDragSource(_event);
			myDropTarget = null;
		}
	}
	
	public void mouseReleased(MouseEvent _event) {
		if (myDragging) {
			if (myDragSource != null) {
				myDropTarget = getDropTarget(_event);
				if (myDropTarget != null) {
					drop(myDragSource, myDropTarget);
				}
			}
			myDragging = false;
			removeDragLabel();
		}
		myDragSource = null;
		myDropTarget = null;
	}
	
	public void mouseEntered(MouseEvent _event) {
	}
	
	public void mouseExited(MouseEvent _event) {
	}
}
