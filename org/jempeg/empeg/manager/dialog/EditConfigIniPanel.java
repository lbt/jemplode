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

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jempeg.nodestore.IDeviceSettings;

/**
* The panel for editing config.ini.
*
* @author Mike Schrag
* @version $Revision: 1.5 $
**/
public class EditConfigIniPanel extends AbstractChangeablePanel {
	private JTextArea myConfigTA;
	private String myInitialText;

	public EditConfigIniPanel() {
		super();
		
		setName("config.ini");
	
		JLabel warningLabel = new JLabel("Changes here will override other tabs.");
	
		BorderLayout bl = new BorderLayout();
		setLayout(bl);

		myConfigTA = new JTextArea();
		JScrollPane jsp = new JScrollPane(myConfigTA);

		add(warningLabel, BorderLayout.NORTH);
		add(jsp, BorderLayout.CENTER);
		
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	protected void read0(IDeviceSettings _configFile) {
		unlistenTo(myConfigTA);
		myConfigTA.setText(_configFile.toString());
		// in case Swing modifies the original string,
		// let's save the "swing version" of the text
		// (i.e. if newlines were modifed, or something).
		// this is just a safety net
		myInitialText = myConfigTA.getText();
		listenTo(myConfigTA);
	}

	protected void write0(IDeviceSettings _configFile) {
		// since this overrides other panels, we want to be
		// be careful to only save the changes if this panel
		// has been modified
		String text = myConfigTA.getText();
		if (!text.equals(myInitialText)) {
			_configFile.fromString(myConfigTA.getText());
		}
	}
}
