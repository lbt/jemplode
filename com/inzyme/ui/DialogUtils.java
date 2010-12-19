/* DialogUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.ui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class DialogUtils
{
    public static void busyWait(JComponent _component, boolean _on) {
	java.awt.Container topLevelAncestor = _component.getTopLevelAncestor();
	if (topLevelAncestor instanceof JFrame) {
	    JFrame frame = (JFrame) topLevelAncestor;
	    Component glassPane = frame.getGlassPane();
	    if (!(glassPane instanceof BusyGlassPanel)) {
		glassPane = new BusyGlassPanel();
		frame.setGlassPane(glassPane);
	    }
	    glassPane.setVisible(_on);
	} else if (topLevelAncestor instanceof JWindow) {
	    JWindow window = (JWindow) topLevelAncestor;
	    Component glassPane = window.getGlassPane();
	    if (!(glassPane instanceof BusyGlassPanel)) {
		glassPane = new BusyGlassPanel();
		window.setGlassPane(glassPane);
	    }
	    glassPane.setVisible(_on);
	}
    }
    
    public static void setInitiallyFocusedComponent
	(Window _window, final JComponent _focusedComponent) {
	_window.addWindowListener(new WindowAdapter() {
	    public void windowActivated(WindowEvent _event) {
		super.windowActivated(_event);
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
			_focusedComponent.requestFocus();
		    }
		});
	    }
	});
    }
    
    public static void addRow(JComponent _left, JComponent _right,
			      GridBagLayout _gridbag, JComponent _container) {
	GridBagConstraints c = new GridBagConstraints();
	c.anchor = 13;
	c.gridwidth = -1;
	c.fill = 0;
	c.weightx = 0.0;
	c.insets = new Insets(0, 0, 3, 10);
	_gridbag.setConstraints(_left, c);
	_container.add(_left);
	c.gridwidth = 0;
	c.fill = 2;
	c.weightx = 1.0;
	c.insets = new Insets(0, 0, 3, 0);
	_gridbag.setConstraints(_right, c);
	_container.add(_right);
    }
    
    public static void centerWindow(Window _window) {
	centerWindowRelativeTo(_window, new Point(0, 0),
			       Toolkit.getDefaultToolkit().getScreenSize());
    }
    
    public static void centerWindowRelativeTo
	(Window _window, Point _parentOffset, Dimension _parentSize) {
	Dimension size = _window.getSize();
	int x = (_parentSize.width - size.width) / 2;
	int y = (_parentSize.height - size.height) / 2;
	y /= (1.0 + Math.sqrt(5.0)) / 2.0;
	_window.setLocation(_parentOffset.x + x, _parentOffset.y + y);
    }
}
