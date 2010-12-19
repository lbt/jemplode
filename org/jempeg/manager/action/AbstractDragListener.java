/* AbstractDragListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
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

public abstract class AbstractDragListener
    implements MouseListener, MouseMotionListener
{
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
    
    protected abstract Object getDragSource(MouseEvent mouseevent);
    
    protected abstract Object getDropTarget(MouseEvent mouseevent);
    
    protected abstract String getDragTitle();
    
    protected abstract void drop(Object object, Object object_0_);
    
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
	/* empty */
    }
    
    public void mouseDragged(MouseEvent _event) {
	if (myDragSource != null && !myDragging
	    && (Math.abs(myStartingPoint.x - _event.getX()) > 10
		|| Math.abs(myStartingPoint.y - _event.getY()) > 10))
	    myDragging = true;
	if (myDragging) {
	    JComponent comp = (JComponent) _event.getSource();
	    int ex = _event.getX();
	    int ey = _event.getY();
	    int x = Math.min(Math.max(0, ex), comp.getWidth());
	    int y = Math.min(Math.max(0, ey), comp.getHeight());
	    myDragRectangle.setBounds(x, y, 1, 1);
	    comp.scrollRectToVisible(myDragRectangle);
	    if (!myCanDragOutside && (ex < 0 || ex > comp.getWidth() || ey < 0
				      || ey > comp.getHeight()))
		removeDragLabel();
	    else {
		if (myDragLabel == null) {
		    myDragLabel = new JLabel(getDragTitle());
		    myDragLabel.setSize(myDragLabel.getPreferredSize());
		    myDragLabel.setOpaque(true);
		    myFrame.getLayeredPane().add(myDragLabel,
						 JLayeredPane.DRAG_LAYER);
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
	/* empty */
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
		if (myDropTarget != null)
		    drop(myDragSource, myDropTarget);
	    }
	    myDragging = false;
	    removeDragLabel();
	}
	myDragSource = null;
	myDropTarget = null;
    }
    
    public void mouseEntered(MouseEvent _event) {
	/* empty */
    }
    
    public void mouseExited(MouseEvent _event) {
	/* empty */
    }
}
