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
package org.jempeg.empeg.manager.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jempeg.ApplicationContext;

import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

/**
* The options dialog that contains the Options panel
* for configuring JEmplode.
*
* @author Mike Schrag
* @version $Revision: 1.2 $
**/
public class OptionsDialog extends JDialog {
	private OptionsPanel myOptionsPanel;
	private boolean myChanged;

	public OptionsDialog(ApplicationContext _context, JFrame _frame) {
		super(_frame, "Options", true);

    myOptionsPanel = new OptionsPanel(_context);		
		
		ActionListener changeListener = new ChangeListener();
    myOptionsPanel.addActionListener(changeListener);
		
    ConfirmationPanel confirmationPanel = new ConfirmationPanel(new JScrollPane(myOptionsPanel));
    confirmationPanel.addOkListener(new OKListener());
    confirmationPanel.addCancelListener(new CancelListener());
    
		getContentPane().add(confirmationPanel);
		pack();
    read();

    setSize(620, 400);
    DialogUtils.centerWindow(this);
	}
	
	public void setChanged(boolean _changed) {
		myChanged = _changed;
	}
	
	public boolean isChanged() {
		return myChanged;
	}
	
	protected void read() {
    myOptionsPanel.read(null);
		setChanged(false);
	}
	
	protected void apply() {
    myOptionsPanel.write(null);
	}
	
	protected void cancel() {
	}
	
	protected class ChangeListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			setChanged(true);
		}
	}
	
	protected class OKListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			apply();
			setVisible(false);
		}
	}
	
	protected class CancelListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			cancel();
			setVisible(false);
		}
	}
}	
