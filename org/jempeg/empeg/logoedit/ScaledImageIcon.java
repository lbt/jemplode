/* ScaledImageIcon - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;

public class ScaledImageIcon implements Icon
{
    private Image myImage;
    private int myWidth;
    private int myHeight;
    private boolean mySelected;
    
    public ScaledImageIcon(Image _image, int _width, int _height,
			   boolean _isSelected) {
	myImage = _image;
	myWidth = _width;
	myHeight = _height;
	mySelected = _isSelected;
    }
    
    public int getIconWidth() {
	return myWidth;
    }
    
    public int getIconHeight() {
	return myHeight;
    }
    
    public void paintIcon(Component _comp, Graphics _g, int _x, int _y) {
	if (mySelected)
	    _g.setXORMode(Color.yellow);
	_g.drawImage(myImage, _x, _y, myWidth, myHeight, _comp);
	if (mySelected)
	    _g.setPaintMode();
    }
}
