/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package com.inzyme.ui;

import java.awt.Component;
import java.awt.Container;
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

/**
* Handy utilities for setting up dialogs.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class DialogUtils {
  public static void busyWait(JComponent _component, boolean _on) {
    Container topLevelAncestor = _component.getTopLevelAncestor();
    if (topLevelAncestor instanceof JFrame) {
      JFrame frame = (JFrame)topLevelAncestor;
      Component glassPane = frame.getGlassPane();
      if (!(glassPane instanceof BusyGlassPanel)) {
        glassPane = new BusyGlassPanel();
        frame.setGlassPane(glassPane);
      }
      glassPane.setVisible(_on);
    }
    else if (topLevelAncestor instanceof JWindow) {
      JWindow window = (JWindow)topLevelAncestor;
      Component glassPane = window.getGlassPane();
      if (!(glassPane instanceof BusyGlassPanel)) {
        glassPane = new BusyGlassPanel();
        window.setGlassPane(glassPane);
      }
      glassPane.setVisible(_on);
    }
  }
  
	public static void setInitiallyFocusedComponent(Window _window, final JComponent _focusedComponent) {
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
	
  /**
  * Adds a row to a container that will layout 
  * like a two column form (labels on left,
  * stretching textbox/editable component on right.
  * This is adapted from a Sun example.
  *
  * @param _left the left component to add
  * @param _right the right component to add
  * @param _gridbag the gridbag layout
  * @param _container the container
  */
  public static void addRow(JComponent _left, JComponent _right, GridBagLayout _gridbag, JComponent _container) {
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.EAST;

    c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
    c.fill = GridBagConstraints.NONE;      //reset to default
    c.weightx = 0.0;                       //reset to default
    c.insets = new Insets(0, 0, 3, 10);
    _gridbag.setConstraints(_left, c);
    _container.add(_left);

    c.gridwidth = GridBagConstraints.REMAINDER;     //end row
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    c.insets = new Insets(0, 0, 3, 0);
    _gridbag.setConstraints(_right, c);
    _container.add(_right);
  }

  /**
  * Centers the given window on the screen.
  *
  * @param _window the window to center
  */
  public static void centerWindow(Window _window) {
  	centerWindowRelativeTo(_window, new Point(0, 0), Toolkit.getDefaultToolkit().getScreenSize());
  }

	/**
	* Centers the given window on the screen.
	*
	* @param _window the window to center
	*/
	public static void centerWindowRelativeTo(Window _window, Point _parentOffset, Dimension _parentSize) {
		Dimension size = _window.getSize();
		int x = (_parentSize.width - size.width) / 2;
		int y = (_parentSize.height - size.height) / 2;
		y /= ((1.0 + Math.sqrt(5)) / 2.0);
		_window.setLocation(_parentOffset.x + x, _parentOffset.y + y);
	}
}
