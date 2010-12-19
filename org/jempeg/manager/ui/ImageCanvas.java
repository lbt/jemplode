/* ImageCanvas - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.ui;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;

public class ImageCanvas extends Canvas
{
    private Image myImage;
    private Dimension mySize;
    
    public ImageCanvas() {
	this((Image) null);
    }
    
    public ImageCanvas(Image _image) {
	setImage(_image);
    }
    
    public void setImage(Image _image) {
	myImage = _image;
	if (myImage == null)
	    mySize = new Dimension(0, 0);
	else {
	    MediaTracker mt = new MediaTracker(this);
	    mt.addImage(_image, 0);
	    try {
		mt.waitForID(0);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    mySize = new Dimension(myImage.getWidth(this),
				   myImage.getHeight(this));
	}
    }
    
    public Image getImage() {
	return myImage;
    }
    
    public Dimension getPreferredSize() {
	return mySize;
    }
    
    public Dimension getMinimumSize() {
	return mySize;
    }
    
    public Dimension getMaximumSize() {
	return mySize;
    }
    
    public void paint(Graphics _g) {
	if (myImage != null) {
	    Rectangle bounds = getBounds();
	    int x = (bounds.width - mySize.width) / 2;
	    int y = (bounds.height - mySize.height) / 2;
	    _g.drawImage(myImage, x, y, this);
	}
    }
}
