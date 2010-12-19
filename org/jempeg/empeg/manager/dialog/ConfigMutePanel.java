/* ConfigMutePanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jempeg.nodestore.IDeviceSettings;

public class ConfigMutePanel extends AbstractChangeablePanel
{
    private static final String[] MUTE_OPTIONS
	= { "Never", "When Input is 0 Volts", "When Input is +12 Volts" };
    private JComboBox myMutePlayerCB;
    
    public ConfigMutePanel() {
	setName("Mute");
	JPanel formPanel = new JPanel();
	GridLayout gl = new GridLayout(0, 2, 0, 5);
	formPanel.setLayout(gl);
	JLabel mutePlayerLabel = new JLabel("Mute Player:");
	myMutePlayerCB = new JComboBox(MUTE_OPTIONS);
	formPanel.add(mutePlayerLabel);
	formPanel.add(myMutePlayerCB);
	setBorder(BorderFactory.createCompoundBorder
		  ((BorderFactory.createTitledBorder
		    (BorderFactory.createEtchedBorder(),
		     "External Mute (Mobile Phone Mute)")),
		   BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	add(formPanel);
    }
    
    protected void read0(IDeviceSettings _configFile) {
	unlistenTo(myMutePlayerCB);
	int selectedIndex
	    = (Integer.parseInt(_configFile.getStringValue("sense", "mute",
							   "-1"))
	       + 1);
	myMutePlayerCB.setSelectedIndex(selectedIndex);
	listenTo(myMutePlayerCB);
    }
    
    protected void write0(IDeviceSettings _configFile) {
	int muteValue = myMutePlayerCB.getSelectedIndex() - 1;
	_configFile.setStringValue("sense", "mute", String.valueOf(muteValue));
    }
}
