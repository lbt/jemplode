/* EditConfigIniPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jempeg.nodestore.IDeviceSettings;

public class EditConfigIniPanel extends AbstractChangeablePanel
{
    private JTextArea myConfigTA;
    private String myInitialText;
    
    public EditConfigIniPanel() {
	setName("config.ini");
	JLabel warningLabel
	    = new JLabel("Changes here will override other tabs.");
	BorderLayout bl = new BorderLayout();
	setLayout(bl);
	myConfigTA = new JTextArea();
	JScrollPane jsp = new JScrollPane(myConfigTA);
	add(warningLabel, "North");
	add(jsp, "Center");
	setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    
    protected void read0(IDeviceSettings _configFile) {
	unlistenTo(myConfigTA);
	myConfigTA.setText(_configFile.toString());
	myInitialText = myConfigTA.getText();
	listenTo(myConfigTA);
    }
    
    protected void write0(IDeviceSettings _configFile) {
	String text = myConfigTA.getText();
	if (!text.equals(myInitialText))
	    _configFile.fromString(myConfigTA.getText());
    }
}
