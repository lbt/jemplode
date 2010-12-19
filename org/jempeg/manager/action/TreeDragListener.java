/* TreeDragListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;

import org.jempeg.ApplicationContext;

public class TreeDragListener implements DragSourceListener
{
    public TreeDragListener(ApplicationContext _context) {
	/* empty */
    }
    
    public void dragDropEnd(DragSourceDropEvent dsde) {
	/* empty */
    }
    
    public void dragEnter(DragSourceDragEvent dsde) {
	/* empty */
    }
    
    public void dragOver(DragSourceDragEvent dsde) {
	/* empty */
    }
    
    public void dropActionChanged(DragSourceDragEvent dsde) {
	/* empty */
    }
    
    public void dragExit(DragSourceEvent dse) {
	/* empty */
    }
    
    protected Object getDragSource(MouseEvent _event) {
	return null;
    }
    
    protected Object getDropTarget(MouseEvent _event) {
	return null;
    }
    
    protected String getDragTitle() {
	return "";
    }
    
    protected void drop(Object _dragSource, Object _dropTarget) {
	/* empty */
    }
}
