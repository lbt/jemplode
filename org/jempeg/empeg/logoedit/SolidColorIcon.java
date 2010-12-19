/* SolidColorIcon - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;

public class SolidColorIcon implements Icon
{
    private JLabel myLabel;
    private Color myColor;
    private int myWidth;
    private int myHeight;
    
    public SolidColorIcon(JLabel _label, Color _color, int _width,
			  int _height) {
	myLabel = _label;
	myColor = _color;
	myWidth = _width;
	myHeight = _height;
    }
    
    public void setColor(Color _color) {
	myColor = _color;
    }
    
    public int getIconWidth() {
	return myWidth;
    }
    
    public int getIconHeight() {
	return myHeight;
    }
    
    public void paintIcon(Component _comp, Graphics _g, int _x, int _y) {
	_g.setColor(myColor);
	_g.fillRect(0, 0, myLabel.getWidth(), myHeight);
    }
}
