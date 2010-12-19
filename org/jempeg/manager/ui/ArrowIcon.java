package org.jempeg.manager.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * ArrowIcon is an Icon implementation that paints 
 * an arrow.
 * 
 * @author Mike Schrag
 */
public class ArrowIcon implements Icon {
	private int myWidth;
	private int myHeight;
	private int myDirection;
	private Color myFill;
	private Color myHighlight;
	private Color myShadow;
	
	/**
	 * Constructor for ArrowIcon.
	 * 
	 * @param _width the width of the arrow
	 * @param _height the height of the arrow
	 * @param _direction the direction of the arrow (SwingConstants.NORTH/SOUTH/EAST/WEST);
	 */
	public ArrowIcon(int _width, int _height, int _direction, Color _highlight, Color _shadow, Color _fill) {
		myWidth = _width;
		myHeight = _height;
		myDirection = _direction;
		myHighlight = _highlight;
		myShadow = _shadow;
		myFill = _fill;
	}

	/**
	 * @see javax.swing.Icon#paintIcon(Component, Graphics, int, int)
	 */
	public void paintIcon(Component _comp, Graphics _g, int _x, int _y) {
		Polygon polygon = new Polygon();
		int midX = _x + (int)((double)myWidth * 0.5);
		if (myDirection == SwingConstants.NORTH) {
			polygon.addPoint(midX, _y);
			polygon.addPoint(_x + myWidth, _y + myHeight);
			polygon.addPoint(_x, _y + myHeight);
			
			_g.setColor(myFill);
			_g.fillPolygon(polygon);
			
			_g.setColor(myHighlight);
			_g.drawLine(midX, _y, _x + myWidth, _y + myHeight);
			_g.drawLine(_x, _y + myHeight, _x + myWidth, _y + myHeight);
			_g.setColor(myShadow);
			_g.drawLine(midX, _y, _x, _y + myHeight);
		} else if (myDirection == SwingConstants.SOUTH) {
			polygon.addPoint(_x, _y);
			polygon.addPoint(_x + myWidth, _y);
			polygon.addPoint(midX, _y + myHeight);
			
			_g.setColor(myFill);
			_g.fillPolygon(polygon);
			
			_g.setColor(myHighlight);
			_g.drawLine(_x + myWidth, _y, midX, _y + myHeight);
			_g.setColor(myShadow);
			_g.drawLine(_x, _y, _x + myWidth, _y);
			_g.drawLine(_x, _y, midX, _y + myHeight);
		}
	}

	/**
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return myWidth;
	}

	/**
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return myHeight;
	}
}
