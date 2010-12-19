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
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jempeg.nodestore.IDeviceSettings;

/**
* The "user information" panel for the configure player dialog.
*
* @author Mike Schrag
* @author Daniel M. Zimmerman
* @version $Revision: 1.5 $
**/

public class ConfigUserInformationPanel extends AbstractChangeablePanel {
	private JTextField myNameTF;
	private JTextField myPhoneNumberTF;
	private JTextField myEmailAddressTF;

	public ConfigUserInformationPanel() {
		super();
		
		setName("User Information");
		
		BorderLayout bl = new BorderLayout();
		setLayout(bl);

		JLabel infoLabel = new JLabel("This information is displayed in case of theft.");

		JPanel fieldsPanel = new JPanel();
			JLabel nameLabel = new JLabel("Name:");
			myNameTF = new JTextField();

			JLabel phoneNumberLabel = new JLabel("Phone Number:");
			myPhoneNumberTF = new JTextField();

			JLabel emailAddressLabel = new JLabel("Email Address:");
			myEmailAddressTF = new JTextField();

			GridLayout gl = new GridLayout(0, 2);
			fieldsPanel.setLayout(gl);
			fieldsPanel.add(nameLabel);
			fieldsPanel.add(myNameTF);
			fieldsPanel.add(phoneNumberLabel);
			fieldsPanel.add(myPhoneNumberTF);
			fieldsPanel.add(emailAddressLabel);
			fieldsPanel.add(myEmailAddressTF);
		add(infoLabel, BorderLayout.NORTH);
		add(fieldsPanel, BorderLayout.SOUTH);
		
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	protected void read0(IDeviceSettings _configFile) {
		unlistenTo(myNameTF);
		myNameTF.setText(_configFile.getStringValue("user info", "name", ""));
		listenTo(myNameTF);

		unlistenTo(myPhoneNumberTF);
		myPhoneNumberTF.setText(_configFile.getStringValue("user info", "phone", ""));
		listenTo(myPhoneNumberTF);

		unlistenTo(myEmailAddressTF);
		myEmailAddressTF.setText(_configFile.getStringValue("user info", "email", ""));
		listenTo(myEmailAddressTF);
	}

	protected void write0(IDeviceSettings _configFile) {
		_configFile.setStringValue("user info", "name", myNameTF.getText());
		_configFile.setStringValue("user info", "phone", myPhoneNumberTF.getText());
		_configFile.setStringValue("user info", "email", myEmailAddressTF.getText());
	}
}
