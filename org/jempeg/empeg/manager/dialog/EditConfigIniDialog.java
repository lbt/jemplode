/* EditConfigIniDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

import org.jempeg.nodestore.IDeviceSettings;

public class EditConfigIniDialog extends JDialog
{
    private EditConfigIniPanel myConfigIniPanel = new EditConfigIniPanel();
    private IDeviceSettings myConfigFile;
    private boolean myChanged;
    
    protected class ChangeListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    setChanged(true);
	}
    }
    
    protected class OKListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    apply();
	    EditConfigIniDialog.this.setVisible(false);
	}
    }
    
    protected class CancelListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    cancel();
	    EditConfigIniDialog.this.setVisible(false);
	}
    }
    
    public EditConfigIniDialog(JFrame _frame) {
	super(_frame, "/empeg/var/config.ini", true);
	ActionListener changeListener = new ChangeListener();
	myConfigIniPanel.addActionListener(changeListener);
	ConfirmationPanel confirmationPanel
	    = new ConfirmationPanel(myConfigIniPanel);
	confirmationPanel.addOkListener(new OKListener());
	confirmationPanel.addCancelListener(new CancelListener());
	getContentPane().add(confirmationPanel);
	pack();
	setSize(Math.max(420, getSize().width), 300);
	DialogUtils.centerWindow(this);
    }
    
    public void setChanged(boolean _changed) {
	myChanged = _changed;
    }
    
    public boolean isChanged() {
	return myChanged;
    }
    
    public void setConfigFile(IDeviceSettings _configFile) {
	myConfigFile = _configFile;
	read();
    }
    
    public IDeviceSettings getConfigFile() {
	return myConfigFile;
    }
    
    protected void read() {
	myConfigIniPanel.read(myConfigFile);
	setChanged(false);
    }
    
    protected void apply() {
	myConfigIniPanel.write(myConfigFile);
    }
    
    protected void cancel() {
	/* empty */
    }
}
