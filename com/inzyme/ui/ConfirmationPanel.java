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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import com.inzyme.event.ActionSource;
import com.inzyme.text.ResourceBundleUtils;

/**
 * ConfirmationPanel is used by anything that needs
 * an OK and Cancel button
 *
 * @author Mike Schrag
 * @version $Revision: 1.4 $
 */
public class ConfirmationPanel extends JPanel {
	public static final int OK_OPTION = 1;
	public static final int CANCEL_OPTION = 0;

	private ActionSource myOkActionSource;
	private ActionSource myCancelActionSource;
	private int mySelectedOption;

	private JButton myOkButton;
	private JButton myCancelButton;

	/**
	 * Constructs a new ConfirmationPanel.
	 *
	 * @param _contentPane the panel to use as the "ContentPane"
	 */
	public ConfirmationPanel(JComponent _contentPane) {
		this(_contentPane, true);
	}

	public ConfirmationPanel(JComponent _contentPane, boolean _showCancelButton) {
		this(_contentPane, true, _showCancelButton);
	}

	public ConfirmationPanel(JComponent _contentPane, boolean _showOkButton, boolean _showCancelButton) {
		myOkActionSource = new ActionSource(this);
		myCancelActionSource = new ActionSource(this);
		mySelectedOption = -1;

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		Box buttonBox = Box.createHorizontalBox();
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
		fillInButtonPanel(buttonPanel, _showOkButton, _showCancelButton);
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(buttonPanel);
		add(_contentPane);
		add(Box.createVerticalStrut(5));
		add(new JSeparator());
		add(Box.createVerticalStrut(10));
		add(buttonBox);

		registerKeyboardAction(new CancelListener(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), WHEN_IN_FOCUSED_WINDOW);
	}
	
	public void fillInButtonPanel(JPanel _buttonPanel, boolean _showOkButton, boolean _showCancelButton) {
		myOkButton = new JButton(ResourceBundleUtils.getUIString("ok"));
		if (_showOkButton) {
			myOkButton.addActionListener(new OkListener());
			_buttonPanel.add(myOkButton);
		}
		
		myCancelButton = new JButton(ResourceBundleUtils.getUIString("cancel"));
		if (_showCancelButton) {
			myCancelButton.addActionListener(new CancelListener());
			_buttonPanel.add(myCancelButton);
		}
	}
	
	public void setEnterIsOK() {
		registerKeyboardAction(new OkListener(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), WHEN_IN_FOCUSED_WINDOW);
	}

	public void setCancelVisible(boolean _visible) {
		myCancelButton.setVisible(_visible);
	}

	public void setOkVisible(boolean _visible) {
		myOkButton.setVisible(_visible);
	}

	public int getSelectedOption() {
		return mySelectedOption;
	}

	public void setOkText(String _okText) {
		myOkButton.setText(_okText);
	}

	public void setCancelText(String _cancelText) {
		myCancelButton.setText(_cancelText);
	}

	/**
	 * Programmatically click "Ok".
	 */
	public void ok() {
		mySelectedOption = ConfirmationPanel.OK_OPTION;
		myOkActionSource.fireActionPerformed();
	}

	/**
	 * Programmatically click "Cancel".
	 */
	public void cancel() {
		mySelectedOption = ConfirmationPanel.CANCEL_OPTION;
		myCancelActionSource.fireActionPerformed();
	}

	/**
	 * Add an action listener to "Ok".
	 *
	 * @param _listener the listener to add
	 */
	public void addOkListener(ActionListener _listener) {
		myOkActionSource.addActionListener(_listener);
	}

	/**
	 * Add an action listener to "Cancel".
	 *
	 * @param _listener the listener to add
	 */
	public void addCancelListener(ActionListener _listener) {
		myCancelActionSource.addActionListener(_listener);
	}

	protected class OkListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ok();
		}
	}

	protected class CancelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cancel();
		}
	}
}
