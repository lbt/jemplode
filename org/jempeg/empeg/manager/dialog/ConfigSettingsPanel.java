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
* The "misc. settings" panel for the configure player dialog.
*
* @author Mike Schrag
* @author Daniel M. Zimmerman
* @version $Revision: 1.5 $
**/

public class ConfigSettingsPanel extends AbstractChangeablePanel {
	private JTextField myPlayerNameTF;

	public ConfigSettingsPanel() {
		super();
		
		setName("Settings");
		
		setLayout(new BorderLayout());
		JPanel formPanel = new JPanel();
			GridLayout gl = new GridLayout(0, 2, 0, 5);
			formPanel.setLayout(gl);
				JLabel playerNameLabel = new JLabel("Player Name:");
				myPlayerNameTF = new JTextField();
			formPanel.add(playerNameLabel);
			formPanel.add(myPlayerNameTF);
		add(formPanel, BorderLayout.NORTH);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	protected void read0(IDeviceSettings _configFile) {
		unlistenTo(myPlayerNameTF);
		myPlayerNameTF.setText(_configFile.getName());
		listenTo(myPlayerNameTF);
	}

	protected void write0(IDeviceSettings _configFile) {
		_configFile.setName(myPlayerNameTF.getText());
	}
}
