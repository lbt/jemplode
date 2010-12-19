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
package org.jempeg.manager.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jempeg.nodestore.IFIDNode;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

/**
* The abstract superclass of the various properties dialogs 
* (like tune and playlist).  This builds a tabbed pane inside of
* a modal dialog along with the OK/cancel buttons.
*
* @author Mike Schrag
* @author Daniel M. Zimmerman
* @version $Revision: 1.4 $
*/
public abstract class AbstractPropertiesDialog extends JDialog {
	private IFIDNode[] myNodes;
	
	public AbstractPropertiesDialog(JFrame _frame, String _title) {
		super(_frame, _title, true);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		addTabs(tabbedPane);

    ConfirmationPanel confirmationPanel = createConfirmationPanel(tabbedPane);
    confirmationPanel.addOkListener(new OkListener());
    confirmationPanel.addCancelListener(new CancelListener());
    confirmationPanel.setEnterIsOK();

		getContentPane().add(confirmationPanel);
		pack();
    setSize(500, Math.min(400, getHeight()));
	  DialogUtils.centerWindow(this);
	}
	
	protected abstract ConfirmationPanel createConfirmationPanel(JTabbedPane _tabbedPane);

	protected ConfirmationPanel createRecursiveConfirmationPanel(JTabbedPane _tabbedPane) {
		ConfirmationPanel confirmationPanel = new ConfirmationPanel(_tabbedPane) {
			public void fillInButtonPanel(JPanel _buttonPanel, boolean _showOkButton, boolean _showCancelButton) {
				JButton recursiveOkButton = new JButton(ResourceBundleUtils.getUIString("properties.recursiveOK"));
				recursiveOkButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent _e) {
						AbstractPropertiesDialog.this.ok(true);
					}
				});
				_buttonPanel.add(recursiveOkButton);
				super.fillInButtonPanel(_buttonPanel, _showOkButton, _showCancelButton);
			}
		};
		return confirmationPanel;
	}

	public void setNodes(IFIDNode[] _nodes) {
		myNodes = _nodes;
	}

	protected IFIDNode[] getNodes() {
		return myNodes;
	}
	
	protected abstract void addTabs(JTabbedPane _tabbedPane);
	
	public abstract void ok(boolean _recursive);
	
	public abstract void cancel();
	
	protected class OkListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			ok(false);
		}
	}
	
	protected class CancelListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			cancel();
		}
	}
}	
