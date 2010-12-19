/* TextTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class TextTool implements ToolIfc
{
    private StringBuffer myTextBuffer = new StringBuffer();
    private Font myFont;
    private Color myColor;
    private int myTextX;
    private int myTextY;
    
    public String getName() {
	return "Text";
    }
    
    public void setFont(Font _font) {
	myFont = _font;
    }
    
    public Font getFont() {
	return myFont;
    }
    
    public void start(LogoEditPanel _editPanel, Graphics _g) {
	myTextBuffer.setLength(0);
	myTextX = -1;
	myTextY = -1;
    }
    
    public void stop(LogoEditPanel _editPanel, Graphics _g) {
	myTextBuffer.setLength(0);
    }
    
    public void commitText(LogoEditPanel _editPanel, Graphics _g) {
	if (myTextBuffer.length() > 0) {
	    _editPanel.saveUndo();
	    _editPanel.setChanged(true);
	    _g.setColor(myColor);
	    paintText(_g, 1);
	    _editPanel.repaint();
	}
    }
    
    public void click(LogoEditPanel _editPanel, Graphics _g, int _x, int _y,
		      int _modifiers) {
	myColor = _editPanel.getColor(_editPanel.isErase(_modifiers));
	myTextX = _x;
	myTextY = _y;
	_editPanel.repaint();
    }
    
    public void drag(LogoEditPanel _editPanel, Graphics _g, int _x, int _y,
		     int _modifiers) {
	/* empty */
    }
    
    public void release(LogoEditPanel _editPanel, Graphics _g, int _x, int _y,
			int _modifiers) {
	/* empty */
    }
    
    public void type(LogoEditPanel _editPanel, Graphics _g, KeyEvent _event) {
	char ch = _event.getKeyChar();
	if (ch == '\010') {
	    if (myTextBuffer.length() > 0)
		myTextBuffer.setLength(myTextBuffer.length() - 1);
	} else if (ch == '\033')
	    myTextBuffer.setLength(0);
	else if (ch == '\n')
	    commitText(_editPanel, _g);
	else
	    myTextBuffer.append(ch);
	_editPanel.repaint();
    }
    
    public void paintOverlay(LogoEditPanel _editPanel, Graphics _g) {
	if (myTextX != -1) {
	    _g.setColor(myColor);
	    FontMetrics fm = _g.getFontMetrics();
	    int ascent = fm.getAscent();
	    int width = fm.stringWidth(myTextBuffer.toString());
	    int scale = _editPanel.getScale();
	    _g.drawLine((myTextX + width) * scale, myTextY * scale,
			(myTextX + width) * scale, (myTextY - ascent) * scale);
	    paintText(_g, scale);
	}
    }
    
    protected void paintText(Graphics _g, int _scale) {
	_g.setFont(new Font(myFont.getName(), myFont.getStyle(),
			    myFont.getSize() * _scale));
	_g.drawString(myTextBuffer.toString(), myTextX * _scale,
		      myTextY * _scale);
    }
    
    public String toString() {
	return getName();
    }
}
