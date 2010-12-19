/* AbstractPenTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public abstract class AbstractPenTool implements ToolIfc
{
    private int myStrokeWidth = 1;
    
    public void start(LogoEditPanel _editPanel, Graphics _g) {
	/* empty */
    }
    
    public void stop(LogoEditPanel _editPanel, Graphics _g) {
	/* empty */
    }
    
    public void setStrokeWidth(int _strokeWidth) {
	myStrokeWidth = _strokeWidth;
    }
    
    public int getStrokeWidth() {
	return myStrokeWidth;
    }
    
    public void click(LogoEditPanel _editPanel, Graphics _g, int _x, int _y,
		      int _modifiers) {
	_editPanel.saveUndo();
	paintPoint(_editPanel, _g, _x, _y, _editPanel.isErase(_modifiers));
    }
    
    public void drag(LogoEditPanel _editPanel, Graphics _g, int _x, int _y,
		     int _modifiers) {
	paintPoint(_editPanel, _g, _x, _y, _editPanel.isErase(_modifiers));
    }
    
    public void release(LogoEditPanel _editPanel, Graphics _g, int _x, int _y,
			int _modifiers) {
	/* empty */
    }
    
    public void type(LogoEditPanel _editPanel, Graphics _g, KeyEvent _event) {
	/* empty */
    }
    
    public void paintOverlay(LogoEditPanel _editPanel, Graphics _g) {
	/* empty */
    }
    
    protected void paintPoint(LogoEditPanel _editPanel, Graphics _g, int _x,
			      int _y, boolean _erase) {
	if (_g != null) {
	    _editPanel.setChanged(true);
	    int strokeWidth = getStrokeWidth();
	    int scale = _editPanel.getScale();
	    int x = _x - strokeWidth / 2;
	    int y = _y - strokeWidth / 2;
	    int w = strokeWidth;
	    int h = strokeWidth;
	    _g.setColor(_editPanel.getColor(_erase));
	    paintPoint(_editPanel, _g, x, y, w, h);
	    _editPanel.repaint(0L, x * scale, y * scale, w * scale, h * scale);
	}
    }
    
    protected abstract void paintPoint(LogoEditPanel logoeditpanel,
				       Graphics graphics, int i, int i_0_,
				       int i_1_, int i_2_);
    
    public String toString() {
	return getName();
    }
}
