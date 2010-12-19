/* FillTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.PixelGrabber;

public class FillTool implements ToolIfc
{
    public String getName() {
	return "Fill";
    }
    
    public void start(LogoEditPanel _editPanel, Graphics _g) {
	/* empty */
    }
    
    public void stop(LogoEditPanel _editPanel, Graphics _g) {
	/* empty */
    }
    
    public void click(LogoEditPanel _editPanel, Graphics _g, int _x, int _y,
		      int _modifiers) {
	_editPanel.saveUndo();
	if (_g != null) {
	    _editPanel.setChanged(true);
	    int width = _editPanel.getImageWidth();
	    int height = _editPanel.getImageHeight();
	    Image image = _editPanel.getInternalImage();
	    int[] pixels = new int[width * height];
	    PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height,
					       pixels, 0, width);
	    try {
		pg.grabPixels();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    Color color = _editPanel.getColor(_editPanel.isErase(_modifiers));
	    _g.setColor(color);
	    floodFill(_editPanel, _g, _x, _y, color.getRGB(), pixels, width,
		      height);
	    _editPanel.repaint();
	}
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
	/* empty */
    }
    
    public void paintOverlay(LogoEditPanel _editPanel, Graphics _g) {
	/* empty */
    }
    
    protected void floodFill(LogoEditPanel _editPanel, Graphics _g, int _x,
			     int _y, int _newColor, int[] _pixels, int _width,
			     int _height) {
	PixelQueue queue = new PixelQueue();
	queue.enqueue(_x, _y);
	int oldColor = _pixels[_y * _width + _x];
	while (!queue.isEmpty()) {
	    PixelQueue.Pixel item = queue.dequeue();
	    int color = _pixels[item.y * _width + item.x];
	    if (color == oldColor && color != _newColor) {
		_g.drawLine(item.x, item.y, item.x, item.y);
		_pixels[item.y * _width + item.x] = _newColor;
		if (item.x < _width - 1)
		    queue.enqueue(item.x + 1, item.y);
		if (item.x > 0)
		    queue.enqueue(item.x - 1, item.y);
		if (item.y < _height - 1)
		    queue.enqueue(item.x, item.y + 1);
		if (item.y > 0)
		    queue.enqueue(item.x, item.y - 1);
	    }
	}
    }
    
    public String toString() {
	return getName();
    }
}
