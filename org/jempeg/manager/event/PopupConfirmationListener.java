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
package org.jempeg.manager.event;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
* PopupConfirmationListener is a ConfirmationListener that is implemented with a
* Swing popup dialog.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class PopupConfirmationListener extends AbstractConfirmationListener {
  private JFrame myFrame;
  private JCheckBox myCheckbox;
  private String myTitle;

  public PopupConfirmationListener(JFrame _frame, String _title) {
    this(_frame, _title, AbstractConfirmationListener.OPTION_NO);
  }

  public PopupConfirmationListener(JFrame _frame, String _title, int _defaultValue) {
  	super(_defaultValue);
    myFrame = _frame;
    myCheckbox = new JCheckBox();
    myTitle = _title;
  }
  
	protected boolean isCheckboxSelected() {
		return myCheckbox.isSelected();
	}

	protected int inputConfirmation(String _message, String _checkboxMessage) {
		JComponent messageComp;
		if (_message.indexOf('\n') == -1) {
			messageComp = new JLabel(_message);
		} else {
			JTextArea textArea = new JTextArea(_message);
			textArea.setEditable(false);
			textArea.setBackground(myFrame.getBackground());
			textArea.setFont(myCheckbox.getFont());
			messageComp = textArea;
		}
		messageComp.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

		JComponent comp;

		if (_checkboxMessage != null) {
			myCheckbox.setText(_checkboxMessage);
			JPanel confirmPanel = new JPanel();
			confirmPanel.setLayout(new BorderLayout());
			confirmPanel.add(messageComp, BorderLayout.CENTER);
			confirmPanel.add(myCheckbox, BorderLayout.SOUTH);
			comp = confirmPanel;
		} else {
			comp = messageComp;
		}

		int selectedValue = JOptionPane.showOptionDialog(myFrame, comp, myTitle, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, CONFIRM_VALUES, CONFIRM_VALUES[getDefaultValue()]);
		return selectedValue;
	}
}
