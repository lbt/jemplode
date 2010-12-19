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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jempeg.empeg.manager.DiscoveryManager;

import com.inzyme.ui.DialogUtils;
import com.inzyme.util.Debug;

/**
* This is the dialog that allows the user to configure his/her connection discovery
* preferences (serial, broadcast, static ip, etc).
*
* @author Mike Schrag
* @author Daniel M. Zimmerman
* @version 1.0a1 $Date: 2003/11/22 13:31:36 $ 
**/
public class ConnectionOptionsDialog extends JDialog {
	private boolean myApproved;
	private JCheckBox mySerialPort;
	private JCheckBox myUsb;
	private JCheckBox myEthernetBroadcast;
	private JCheckBox mySpecificAddress;
	private JTextField myInetAddress;
	private DiscoveryManager myDiscoveryManager;
	 
	public ConnectionOptionsDialog(JFrame _frame, DiscoveryManager _discoveryManager) {
		super(_frame, "Connection Options", true);
		myDiscoveryManager = _discoveryManager;
		
    ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			JPanel optionsPanel = new JPanel(new GridLayout(0, 1));
				mySerialPort = new JCheckBox("Serial Port");
				mySerialPort.setSelected(_discoveryManager.isSerialEnabled());
				mySerialPort.setHorizontalAlignment(SwingConstants.LEFT);
        
				myUsb = new JCheckBox("USB");
				myUsb.setSelected(_discoveryManager.isUsbEnabled());
				myUsb.setHorizontalAlignment(SwingConstants.LEFT);

				myEthernetBroadcast = new JCheckBox("Ethernet Broadcast");
				myEthernetBroadcast.setSelected(_discoveryManager.isEthernetEnabled());
				myEthernetBroadcast.setHorizontalAlignment(SwingConstants.LEFT);
        
				JPanel specificAddressPanel = new JPanel();
					mySpecificAddress = new JCheckBox("Specific Address");
					mySpecificAddress.setSelected(_discoveryManager.isUnicastEnabled());
					mySpecificAddress.addActionListener(new SpecificAddressAction());
					
					myInetAddress = new JTextField();
					myInetAddress.setEnabled(_discoveryManager.isUnicastEnabled());
					myInetAddress.setColumns(11);
					InetAddress unicastAddress = _discoveryManager.getUnicastAddress();
					if (unicastAddress != null) {
						myInetAddress.setText(unicastAddress.getHostAddress());
					}
				specificAddressPanel.setLayout(new BorderLayout());
				specificAddressPanel.add(mySpecificAddress, BorderLayout.WEST);
				specificAddressPanel.add(myInetAddress, BorderLayout.CENTER);
			optionsPanel.add(mySerialPort);
			optionsPanel.add(myUsb);
			optionsPanel.add(myEthernetBroadcast);
			optionsPanel.add(specificAddressPanel);

			Box buttonBox = Box.createHorizontalBox();
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new OKAction());
				
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new CancelAction());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(buttonPanel);      
    Box vbox = Box.createVerticalBox();
    vbox.add(optionsPanel);
    vbox.add(Box.createVerticalStrut(15));
    vbox.add(buttonBox);

		getContentPane().add(vbox);
		pack();
		
    DialogUtils.centerWindow(this);
	}
	
	public boolean isApproved() {
		return myApproved;
	}
	
	public void ok() throws IOException {
		myDiscoveryManager.setSerialEnabled(mySerialPort.isSelected());
		myDiscoveryManager.setUsbEnabled(myUsb.isSelected());
		myDiscoveryManager.setEthernetEnabled(myEthernetBroadcast.isSelected());
		myDiscoveryManager.setUnicastEnabled(mySpecificAddress.isSelected());
		if (myDiscoveryManager.isUnicastEnabled()) {
			InetAddress unicastAddress = InetAddress.getByName(myInetAddress.getText());
			myDiscoveryManager.setUnicastAddress(unicastAddress);
		} else {
			myDiscoveryManager.setUnicastAddress(null);
		}
		myDiscoveryManager.saveSettings();
		myApproved = true;
	}
	
	public void cancel() {
		myApproved = false;
	}
	
	protected class SpecificAddressAction implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			myInetAddress.setEnabled(mySpecificAddress.isSelected());
		}
	}
	
	protected class OKAction extends AbstractAction {
		public void actionPerformed(ActionEvent _event) {
			try {
				ok();
			}
			catch (IOException e) {
				Debug.handleError((JFrame)getParent(), e, true);
			}
			finally {
				setVisible(false);
			}
		}
	}
	
	protected class CancelAction extends AbstractAction {
		public void actionPerformed(ActionEvent _event) {
			cancel();
			setVisible(false);
		}
	}
}
