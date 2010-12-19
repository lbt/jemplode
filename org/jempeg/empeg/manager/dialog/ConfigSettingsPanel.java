/* ConfigSettingsPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jempeg.nodestore.IDeviceSettings;

public class ConfigSettingsPanel extends AbstractChangeablePanel
{
    private JTextField myPlayerNameTF;
    
    public ConfigSettingsPanel() {
	setName("Settings");
	setLayout(new BorderLayout());
	JPanel formPanel = new JPanel();
	GridLayout gl = new GridLayout(0, 2, 0, 5);
	formPanel.setLayout(gl);
	JLabel playerNameLabel = new JLabel("Player Name:");
	myPlayerNameTF = new JTextField();
	formPanel.add(playerNameLabel);
	formPanel.add(myPlayerNameTF);
	add(formPanel, "North");
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
