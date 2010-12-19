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

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.SynchronizeException;
import org.jempeg.nodestore.WendyFilters;

import com.inzyme.ui.CloseDialogListener;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;
import com.inzyme.util.Debug;

/**
* ReasonsDialog provides a GUI for presenting
* a set of reasons.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class WendyDialog extends JDialog {
	private ConfirmationPanel myConfirmationPanel;
	private WendyPanel myWendyPanel;
	private ApplicationContext myContext;

	public WendyDialog(ApplicationContext _context) throws SynchronizeException {
		super(_context.getFrame(), "Wendy Filters", true);

		PlayerDatabase playerDatabase = _context.getPlayerDatabase();
		WendyFilters filters = playerDatabase.getDeviceSettings().getWendyFilters();
		myWendyPanel = new WendyPanel(filters);
		myContext = _context;

		myConfirmationPanel = new ConfirmationPanel(myWendyPanel);
		myConfirmationPanel.addOkListener(new CloseDialogListener(this) {
			public void actionPerformed(ActionEvent _event) {
				try {
					ok();
				}
				catch (SynchronizeException e) {
					Debug.handleError((JFrame) getParent(), e, true);
				}
				super.actionPerformed(_event);
			}
		});
		myConfirmationPanel.addCancelListener(new CloseDialogListener(this) {
			public void actionPerformed(ActionEvent _event) {
				cancel();
				super.actionPerformed(_event);
			}
		});

		getContentPane().add(myConfirmationPanel);

		pack();
		setSize(Math.max(420, getSize().width), 350);
		DialogUtils.centerWindow(this);
	}

	public void ok() throws SynchronizeException {
		WendyFilters filters = myWendyPanel.getWendyFilters();
		PlayerDatabase playerDatabase = myContext.getPlayerDatabase();
		playerDatabase.getDeviceSettings().setWendyFilters(filters, playerDatabase);
	}

	public void cancel() {
	}

	public void addOkListener(ActionListener _listener) {
		myConfirmationPanel.addOkListener(_listener);
	}

	public void addCancelListener(ActionListener _listener) {
		myConfirmationPanel.addCancelListener(_listener);
	}
}
