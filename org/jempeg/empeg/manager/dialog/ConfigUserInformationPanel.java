/* ConfigUserInformationPanel - Decompiled by JODE
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

public class ConfigUserInformationPanel extends AbstractChangeablePanel
{
    private JTextField myNameTF;
    private JTextField myPhoneNumberTF;
    private JTextField myEmailAddressTF;
    
    public ConfigUserInformationPanel() {
	setName("User Information");
	BorderLayout bl = new BorderLayout();
	setLayout(bl);
	JLabel infoLabel
	    = new JLabel("This information is displayed in case of theft.");
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
	add(infoLabel, "North");
	add(fieldsPanel, "South");
	setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    
    protected void read0(IDeviceSettings _configFile) {
	unlistenTo(myNameTF);
	myNameTF.setText(_configFile.getStringValue("user info", "name", ""));
	listenTo(myNameTF);
	unlistenTo(myPhoneNumberTF);
	myPhoneNumberTF.setText(_configFile.getStringValue("user info",
							   "phone", ""));
	listenTo(myPhoneNumberTF);
	unlistenTo(myEmailAddressTF);
	myEmailAddressTF.setText(_configFile.getStringValue("user info",
							    "email", ""));
	listenTo(myEmailAddressTF);
    }
    
    protected void write0(IDeviceSettings _configFile) {
	_configFile.setStringValue("user info", "name", myNameTF.getText());
	_configFile.setStringValue("user info", "phone",
				   myPhoneNumberTF.getText());
	_configFile.setStringValue("user info", "email",
				   myEmailAddressTF.getText());
    }
}
