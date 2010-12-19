/* TableDragListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.jempeg.ApplicationContext;

public class TableDragListener extends AbstractDragListener
{
    public TableDragListener(ApplicationContext _context) {
	super(_context.getFrame());
    }
    
    protected Object getDragSource(MouseEvent _event) {
	return null;
    }
    
    protected Object getValueAtPoint(Point _p) {
	return null;
    }
    
    protected Object getDropTarget(MouseEvent _event) {
	return null;
    }
    
    protected String getDragTitle() {
	return null;
    }
    
    protected void drop(Object _dragSource, Object _dropTarget) {
	/* empty */
    }
}
