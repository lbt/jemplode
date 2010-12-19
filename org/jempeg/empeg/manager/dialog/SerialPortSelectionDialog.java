/* SerialPortSelectionDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;

import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

public class SerialPortSelectionDialog extends JDialog
{
    public static final int APPROVE_OPTION = 1;
    public static final int CANCEL_OPTION = 0;
    private JComboBox myCommPortList;
    private boolean myOkPressed;
    
    protected class OkListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    myOkPressed = true;
	    SerialPortSelectionDialog.this.setVisible(false);
	    SerialPortSelectionDialog.this.dispose();
	}
    }
    
    protected class CancelListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    myOkPressed = false;
	    SerialPortSelectionDialog.this.setVisible(false);
	    SerialPortSelectionDialog.this.dispose();
	}
    }
    
    public SerialPortSelectionDialog(JFrame _frame) {
	super(_frame, "Select a Serial Port", true);
	Vector commPorts = new Vector();
	Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
	while (portIdentifiers.hasMoreElements()) {
	    CommPortIdentifier commPort
		= (CommPortIdentifier) portIdentifiers.nextElement();
	    String name = commPort.getName();
	    commPorts.addElement(name);
	}
	myCommPortList = new JComboBox(commPorts);
	ConfirmationPanel confPanel = new ConfirmationPanel(myCommPortList);
	confPanel.addOkListener(new OkListener());
	confPanel.addCancelListener(new CancelListener());
	getContentPane().add(confPanel);
	pack();
	DialogUtils.centerWindow(this);
    }
    
    public int showSerialPortSelectionDialog() {
	setVisible(true);
	int results = myOkPressed ? 1 : 0;
	return results;
    }
    
    public CommPortIdentifier getSelectedPort() throws NoSuchPortException {
	String commPortIdentifierStr
	    = (String) myCommPortList.getSelectedItem();
	CommPortIdentifier commPort
	    = CommPortIdentifier.getPortIdentifier(commPortIdentifierStr);
	return commPort;
    }
}
