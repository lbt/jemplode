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
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jempeg.nodestore.IDeviceSettings;

/**
* The "shutdown" panel for the configure player dialog.
*
* @author Mike Schrag
* @author Daniel M. Zimmerman
* @version $Revision: 1.5 $
**/

public class ConfigShutdownPanel extends AbstractChangeablePanel {
	private JTextField myShutdownTF;

	public ConfigShutdownPanel() {
		super();
		
		setName("Shutdown");
		
		setLayout(new BorderLayout());
			JLabel infoLabel = new JLabel("Sets the number of seconds to sleep before shutdown:");
		
			JPanel formPanel = new JPanel();
			BoxLayout bl = new BoxLayout(formPanel, BoxLayout.X_AXIS);
			formPanel.setLayout(bl);
				myShutdownTF = new JTextField();
				JLabel secondsLabel = new JLabel("Seconds");
			formPanel.add(myShutdownTF);
			formPanel.add(secondsLabel);
		add(infoLabel, BorderLayout.NORTH);
		add(formPanel, BorderLayout.NORTH);
		
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Player Shutdown"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	}

	protected void read0(IDeviceSettings _configFile) {
		unlistenTo(myShutdownTF);
		int timeout = Integer.parseInt(_configFile.getStringValue("power", "off_timeout", "60000")) / 1000;
		myShutdownTF.setText(String.valueOf(timeout));
		listenTo(myShutdownTF);
	}

	protected void write0(IDeviceSettings _configFile) {
		int timeout;
		try {
			timeout = Integer.parseInt(myShutdownTF.getText()) * 1000;
		}
			catch (Throwable t) {
				timeout = 60000;
			}
		_configFile.setStringValue("power", "off_timeout", String.valueOf(timeout));
	}
}
