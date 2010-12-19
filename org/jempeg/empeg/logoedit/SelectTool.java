/* SelectTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class SelectTool implements ToolIfc
{
    private Image myClipboardImage;
    private Image myDragImage;
    private Rectangle myHoleRect;
    private Rectangle myDragRect;
    private int myClickX;
    private int myClickY;
    private boolean mySelecting;
    
    public String getName() {
	return "Select";
    }
    
    public void start(LogoEditPanel _editPanel, Graphics _g) {
	clear();
    }
    
    public void stop(LogoEditPanel _editPanel, Graphics _g) {
	clear();
    }
    
    public void click(LogoEditPanel _editPanel, Graphics _g, int _x, int _y,
		      int _modifiers) {
	if (myDragRect == null || !myDragRect.contains(_x, _y)) {
	    mySelecting = true;
	    myClickX = _x;
	    myClickY = _y;
	    myDragImage = null;
	    myDragRect = new Rectangle(_x, _y, 0, 0);
	} else {
	    myClickX = _x - myDragRect.x;
	    myClickY = _y - myDragRect.y;
	}
    }
    
    public void drag(LogoEditPanel _editPanel, Graphics _g, int _x, int _y,
		     int _modifiers) {
	if (myDragRect != null && myDragImage == null && !mySelecting) {
	    if ((_modifiers & 0x1) != 0)
		cutHole();
	    grabSelection(_editPanel);
	}
	dragSelectionTo(_editPanel, _x, _y);
    }
    
    public void release(LogoEditPanel _editPanel, Graphics _g, int _x, int _y,
			int _modifiers) {
	mySelecting = false;
	_editPanel.repaint();
    }
    
    public void type(LogoEditPanel _editPanel, Graphics _g, KeyEvent _event) {
	char ch = _event.getKeyChar();
	if (ch == '\n')
	    commitDrag(_editPanel, _g);
	else if (ch == '\033')
	    rollbackDrag(_editPanel, _g);
	else if (_event.isControlDown() && _event.getKeyChar() == '\003')
	    copySelection(_editPanel, _g);
	else if (_event.isControlDown() && _event.getKeyChar() == '\030')
	    cutSelection(_editPanel, _g);
	else if (_event.isControlDown() && _event.getKeyChar() == '\026')
	    pasteSelection(_editPanel, _g);
    }
    
    public void paintOverlay(LogoEditPanel _editPanel, Graphics _g) {
	int scale = _editPanel.getScale();
	if (myHoleRect != null) {
	    _g.setColor(_editPanel.getBackgroundColor());
	    _g.fillRect(myHoleRect.x * scale, myHoleRect.y * scale,
			myHoleRect.width * scale, myHoleRect.height * scale);
	}
	if (myDragImage != null)
	    _g.drawImage(myDragImage, myDragRect.x * scale,
			 myDragRect.y * scale, myDragRect.width * scale,
			 myDragRect.height * scale, _editPanel);
	if (myDragRect != null && isValidSelection()) {
	    _g.setColor(Color.red);
	    _g.drawRect(myDragRect.x * scale, myDragRect.y * scale,
			myDragRect.width * scale, myDragRect.height * scale);
	}
    }
    
    protected boolean isValidSelection() {
	boolean validSelection
	    = myDragRect.width >= 1 && myDragRect.height >= 1;
	return validSelection;
    }
    
    protected void grabSelection(LogoEditPanel _editPanel) {
	if (isValidSelection()) {
	    myDragImage
		= _editPanel.createImage(myDragRect.width, myDragRect.height);
	    Graphics dragImageGraphics = myDragImage.getGraphics();
	    dragImageGraphics.drawImage(_editPanel.getInternalImage(),
					-myDragRect.x, -myDragRect.y,
					_editPanel);
	} else
	    clear();
    }
    
    protected void cutHole() {
	myHoleRect = (Rectangle) myDragRect.clone();
    }
    
    protected void fillHole(Graphics _g) {
	_g.fillRect(myHoleRect.x, myHoleRect.y, myHoleRect.width,
		    myHoleRect.height);
    }
    
    protected synchronized void pasteSelection(LogoEditPanel _editPanel,
					       Graphics _g) {
	if (myClipboardImage != null && myDragImage == null) {
	    myDragImage = myClipboardImage;
	    myDragRect = new Rectangle(0, 0, myDragImage.getWidth(_editPanel),
				       myDragImage.getHeight(_editPanel));
	    myClipboardImage = null;
	    myHoleRect = null;
	    mySelecting = false;
	    _editPanel.repaint();
	}
    }
    
    protected synchronized void copySelection(LogoEditPanel _editPanel,
					      Graphics _g) {
	if (isValidSelection() && myDragImage == null) {
	    grabSelection(_editPanel);
	    myClipboardImage = myDragImage;
	    clear();
	    _editPanel.repaint();
	}
    }
    
    protected synchronized void cutSelection(LogoEditPanel _editPanel,
					     Graphics _g) {
	if (isValidSelection() && myDragImage == null) {
	    cutHole();
	    grabSelection(_editPanel);
	    myClipboardImage = myDragImage;
	    _editPanel.saveUndo();
	    _editPanel.setChanged(true);
	    fillHole(_g);
	    clear();
	    _editPanel.repaint();
	}
    }
    
    protected void dragSelectionTo(LogoEditPanel _editPanel, int _x, int _y) {
	if (myDragImage == null) {
	    if (_x < myClickX) {
		myDragRect.x = _x;
		myDragRect.width = myClickX - _x;
	    } else {
		myDragRect.x = myClickX;
		myDragRect.width = _x - myDragRect.x;
	    }
	    if (_y < myClickY) {
		myDragRect.y = _y;
		myDragRect.height = myClickY - _y;
	    } else {
		myDragRect.y = myClickY;
		myDragRect.height = _y - myDragRect.y;
	    }
	} else {
	    myDragRect.x = _x - myClickX;
	    myDragRect.y = _y - myClickY;
	}
	_editPanel.repaint();
    }
    
    protected void commitDrag(LogoEditPanel _editPanel, Graphics _g) {
	if (myDragImage != null) {
	    _editPanel.saveUndo();
	    _editPanel.setChanged(true);
	    if (myHoleRect != null)
		fillHole(_g);
	    _g.drawImage(myDragImage, myDragRect.x, myDragRect.y, _editPanel);
	}
	clear();
	_editPanel.repaint();
    }
    
    protected void rollbackDrag(LogoEditPanel _editPanel, Graphics _g) {
	clear();
	_editPanel.repaint();
    }
    
    protected void clear() {
	myDragRect = null;
	myDragImage = null;
	myHoleRect = null;
    }
    
    public String toString() {
	return getName();
    }
}
