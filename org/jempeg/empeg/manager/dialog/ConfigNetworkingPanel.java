/* ConfigNetworkingPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
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

import org.jempeg.nodestore.IDeviceSettings;

public class ConfigNetworkingPanel extends AbstractChangeablePanel
{
    private JRadioButton myAutomaticRB;
    private JRadioButton myManualRB;
    private JTextField myIPAddressTF;
    private JTextField myNetmaskTF;
    private JTextField myDefaultGatewayTF;
    
    public ConfigNetworkingPanel() {
	setName("Networking");
	setLayout(new BorderLayout());
	myAutomaticRB = new JRadioButton("Automatic Configuration (DHCP)");
	myAutomaticRB.setAlignmentY(2.0F);
	JPanel manualPanel = new JPanel();
	manualPanel.setLayout(new BorderLayout());
	myManualRB = new JRadioButton("Manual Configuration");
	myManualRB.setAlignmentY(2.0F);
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
	manualPanel.add(myManualRB, "North");
	manualPanel.add(Box.createVerticalStrut(5));
	manualPanel.add(fieldsPanel, "South");
	manualPanel.setBorder(BorderFactory.createCompoundBorder
			      (BorderFactory.createEtchedBorder(),
			       BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	add(myAutomaticRB, "North");
	add(manualPanel, "South");
	setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    
    protected void read0(IDeviceSettings _configFile) {
	unlistenTo(myIPAddressTF);
	myIPAddressTF.setText(_configFile.getStringValue("network",
							 "ipaddress", ""));
	listenTo(myIPAddressTF);
	unlistenTo(myNetmaskTF);
	myNetmaskTF.setText(_configFile.getStringValue("network", "netmask",
						       ""));
	listenTo(myNetmaskTF);
	unlistenTo(myDefaultGatewayTF);
	myDefaultGatewayTF.setText(_configFile.getStringValue("network",
							      "gateway", ""));
	listenTo(myDefaultGatewayTF);
	unlistenTo(myAutomaticRB);
	unlistenTo(myManualRB);
	boolean automatic
	    = _configFile.getBooleanValue("network", "dhcp", true);
	myAutomaticRB.setSelected(automatic);
	myManualRB.setSelected(!automatic);
	listenTo(myAutomaticRB);
	listenTo(myManualRB);
    }
    
    protected void write0(IDeviceSettings _configFile) {
	_configFile.setStringValue("network", "IPAddress",
				   myIPAddressTF.getText());
	_configFile.setStringValue("network", "Netmask",
				   myNetmaskTF.getText());
	_configFile.setStringValue("network", "Gateway",
				   myDefaultGatewayTF.getText());
	_configFile.setBooleanValue("network", "DHCP",
				    myAutomaticRB.isSelected());
    }
}
