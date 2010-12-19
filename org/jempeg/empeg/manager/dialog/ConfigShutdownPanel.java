/* ConfigShutdownPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jempeg.nodestore.IDeviceSettings;

public class ConfigShutdownPanel extends AbstractChangeablePanel
{
    private JTextField myShutdownTF;
    
    public ConfigShutdownPanel() {
	setName("Shutdown");
	setLayout(new BorderLayout());
	JLabel infoLabel
	    = (new JLabel
	       ("Sets the number of seconds to sleep before shutdown:"));
	JPanel formPanel = new JPanel();
	BoxLayout bl = new BoxLayout(formPanel, 0);
	formPanel.setLayout(bl);
	myShutdownTF = new JTextField();
	JLabel secondsLabel = new JLabel("Seconds");
	formPanel.add(myShutdownTF);
	formPanel.add(secondsLabel);
	add(infoLabel, "North");
	add(formPanel, "North");
	setBorder(BorderFactory.createCompoundBorder
		  (BorderFactory.createTitledBorder(BorderFactory
							.createEtchedBorder(),
						    "Player Shutdown"),
		   BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }
    
    protected void read0(IDeviceSettings _configFile) {
	unlistenTo(myShutdownTF);
	int timeout
	    = (Integer.parseInt(_configFile.getStringValue("power",
							   "off_timeout",
							   "60000"))
	       / 1000);
	myShutdownTF.setText(String.valueOf(timeout));
	listenTo(myShutdownTF);
    }
    
    protected void write0(IDeviceSettings _configFile) {
	int timeout;
	try {
	    timeout = Integer.parseInt(myShutdownTF.getText()) * 1000;
	} catch (Throwable t) {
	    timeout = 60000;
	}
	_configFile.setStringValue("power", "off_timeout",
				   String.valueOf(timeout));
    }
}
