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

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jempeg.nodestore.IDeviceSettings;

/**
* The "mute" panel for the configure player dialog.
*
* @author Mike Schrag
* @author Daniel M. Zimmerman
* @version $Revision: 1.5 $
**/

public class ConfigMutePanel extends AbstractChangeablePanel {
	private static final String[] MUTE_OPTIONS = new String[] { "Never", "When Input is 0 Volts", "When Input is +12 Volts" };

	private JComboBox myMutePlayerCB;

	public ConfigMutePanel() {
		super();
		
		setName("Mute");
		
		JPanel formPanel = new JPanel();
			GridLayout gl = new GridLayout(0, 2, 0, 5);
			formPanel.setLayout(gl);
				JLabel mutePlayerLabel = new JLabel("Mute Player:");
				myMutePlayerCB = new JComboBox(MUTE_OPTIONS);
			formPanel.add(mutePlayerLabel);
			formPanel.add(myMutePlayerCB);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "External Mute (Mobile Phone Mute)"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		add(formPanel);
	}

	protected void read0(IDeviceSettings _configFile) {
		unlistenTo(myMutePlayerCB);
		int selectedIndex = Integer.parseInt(_configFile.getStringValue("sense", "mute", "-1")) + 1;
		myMutePlayerCB.setSelectedIndex(selectedIndex);
		listenTo(myMutePlayerCB);
	}

	protected void write0(IDeviceSettings _configFile) {
		int muteValue = myMutePlayerCB.getSelectedIndex() - 1;
		_configFile.setStringValue("sense", "mute", String.valueOf(muteValue));
	}
}
