package com.inzyme.ui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * CloseDialogListener is an ActionListener that can 
 * close a window when the action is performed.
 * 
 * @author Mike Schrag
 */
public class CloseDialogListener implements ActionListener {
	private Window myWindow;

	public CloseDialogListener(Window _window) {
		myWindow = _window;
	}

	public void actionPerformed(ActionEvent _event) {
		myWindow.setVisible(false);
	}
}
