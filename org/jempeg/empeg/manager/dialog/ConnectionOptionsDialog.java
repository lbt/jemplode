/* ConnectionOptionsDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
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

import com.inzyme.ui.DialogUtils;
import com.inzyme.util.Debug;

import org.jempeg.empeg.manager.DiscoveryManager;

public class ConnectionOptionsDialog extends JDialog
{
    private boolean myApproved;
    private JCheckBox mySerialPort;
    private JCheckBox myUsb;
    private JCheckBox myEthernetBroadcast;
    private JCheckBox mySpecificAddress;
    private JTextField myInetAddress;
    private DiscoveryManager myDiscoveryManager;
    
    protected class SpecificAddressAction implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    myInetAddress.setEnabled(mySpecificAddress.isSelected());
	}
    }
    
    protected class OKAction extends AbstractAction
    {
	public void actionPerformed(ActionEvent _event) {
	    try {
		try {
		    ok();
		} catch (IOException e) {
		    Debug.handleError((JFrame) ConnectionOptionsDialog.this
						   .getParent(),
				      e, true);
		}
	    } catch (Object object) {
		ConnectionOptionsDialog.this.setVisible(false);
		throw object;
	    }
	    ConnectionOptionsDialog.this.setVisible(false);
	}
    }
    
    protected class CancelAction extends AbstractAction
    {
	public void actionPerformed(ActionEvent _event) {
	    cancel();
	    ConnectionOptionsDialog.this.setVisible(false);
	}
    }
    
    public ConnectionOptionsDialog(JFrame _frame,
				   DiscoveryManager _discoveryManager) {
	super(_frame, "Connection Options", true);
	myDiscoveryManager = _discoveryManager;
	((JComponent) getContentPane())
	    .setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	JPanel optionsPanel = new JPanel(new GridLayout(0, 1));
	mySerialPort = new JCheckBox("Serial Port");
	mySerialPort.setSelected(_discoveryManager.isSerialEnabled());
	mySerialPort.setHorizontalAlignment(2);
	myUsb = new JCheckBox("USB");
	myUsb.setSelected(_discoveryManager.isUsbEnabled());
	myUsb.setHorizontalAlignment(2);
	myEthernetBroadcast = new JCheckBox("Ethernet Broadcast");
	myEthernetBroadcast.setSelected(_discoveryManager.isEthernetEnabled());
	myEthernetBroadcast.setHorizontalAlignment(2);
	JPanel specificAddressPanel = new JPanel();
	mySpecificAddress = new JCheckBox("Specific Address");
	mySpecificAddress.setSelected(_discoveryManager.isUnicastEnabled());
	mySpecificAddress.addActionListener(new SpecificAddressAction());
	myInetAddress = new JTextField();
	myInetAddress.setEnabled(_discoveryManager.isUnicastEnabled());
	myInetAddress.setColumns(11);
	InetAddress unicastAddress = _discoveryManager.getUnicastAddress();
	if (unicastAddress != null)
	    myInetAddress.setText(unicastAddress.getHostAddress());
	specificAddressPanel.setLayout(new BorderLayout());
	specificAddressPanel.add(mySpecificAddress, "West");
	specificAddressPanel.add(myInetAddress, "Center");
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
	myDiscoveryManager
	    .setEthernetEnabled(myEthernetBroadcast.isSelected());
	myDiscoveryManager.setUnicastEnabled(mySpecificAddress.isSelected());
	if (myDiscoveryManager.isUnicastEnabled()) {
	    InetAddress unicastAddress
		= InetAddress.getByName(myInetAddress.getText());
	    myDiscoveryManager.setUnicastAddress(unicastAddress);
	} else
	    myDiscoveryManager.setUnicastAddress(null);
	myDiscoveryManager.saveSettings();
	myApproved = true;
    }
    
    public void cancel() {
	myApproved = false;
    }
}
