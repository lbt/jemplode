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
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jempeg.nodestore.IDeviceSettings;

/**
* The "networking" panel for the configure player dialog.
*
* @author Mike Schrag
* @author Daniel M. Zimmerman
* @version $Revision: 1.5 $
**/

public class ConfigNetworkingPanel extends AbstractChangeablePanel {
	private JRadioButton myAutomaticRB;
	private JRadioButton myManualRB;
	private JTextField myIPAddressTF;
	private JTextField myNetmaskTF;
	private JTextField myDefaultGatewayTF;

	public ConfigNetworkingPanel() {
		super();
		
		setName("Networking");
		
		setLayout(new BorderLayout());
			myAutomaticRB = new JRadioButton("Automatic Configuration (DHCP)");
			myAutomaticRB.setAlignmentY(SwingConstants.LEFT);
			
			JPanel manualPanel = new JPanel();
			manualPanel.setLayout(new BorderLayout());
				myManualRB = new JRadioButton("Manual Configuration");
				myManualRB.setAlignmentY(SwingConstants.LEFT);
				
				ButtonGroup group = new ButtonGroup();
				group.add(myAutomaticRB);
				group.add(myManualRB);

				JPanel fieldsPanel = new JPanel();
					JLabel ipAddressLabel = new JLabel("IP Address:");
					myIPAddressTF = new JTextField();

					JLabel netmaskLabel = new JLabel("Netmask:");
					myNetmaskTF = new JTextField();

					JLabel defaultGatewayLabel = new JLabel("Default Gateway:");
					myDefaultGatewayTF = new JTextField();

				GridLayout gl = new GridLayout(0, 2, 0, 5);
				fieldsPanel.setLayout(gl);
				fieldsPanel.add(ipAddressLabel);
				fieldsPanel.add(myIPAddressTF);
				fieldsPanel.add(netmaskLabel);
				fieldsPanel.add(myNetmaskTF);
				fieldsPanel.add(defaultGatewayLabel);
				fieldsPanel.add(myDefaultGatewayTF);
			manualPanel.add(myManualRB, BorderLayout.NORTH);
			manualPanel.add(Box.createVerticalStrut(5));
			manualPanel.add(fieldsPanel, BorderLayout.SOUTH);
			manualPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		add(myAutomaticRB, BorderLayout.NORTH);
		add(manualPanel, BorderLayout.SOUTH);
		
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	protected void read0(IDeviceSettings _configFile) {
		unlistenTo(myIPAddressTF);
		myIPAddressTF.setText(_configFile.getStringValue("network", "ipaddress", ""));
		listenTo(myIPAddressTF);

		unlistenTo(myNetmaskTF);
		myNetmaskTF.setText(_configFile.getStringValue("network", "netmask", ""));
		listenTo(myNetmaskTF);

		unlistenTo(myDefaultGatewayTF);
		myDefaultGatewayTF.setText(_configFile.getStringValue("network", "gateway", ""));
		listenTo(myDefaultGatewayTF);

		unlistenTo(myAutomaticRB);
		unlistenTo(myManualRB);
		boolean automatic = _configFile.getBooleanValue("network", "dhcp", true);
		myAutomaticRB.setSelected(automatic);
		myManualRB.setSelected(!automatic);
		listenTo(myAutomaticRB);
		listenTo(myManualRB);
	}

	protected void write0(IDeviceSettings _configFile) {
		_configFile.setStringValue("network", "IPAddress", myIPAddressTF.getText());
		_configFile.setStringValue("network", "Netmask", myNetmaskTF.getText());
		_configFile.setStringValue("network", "Gateway", myDefaultGatewayTF.getText());
		_configFile.setBooleanValue("network", "DHCP", myAutomaticRB.isSelected());
	}
}
