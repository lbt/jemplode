/* ConfigurePlayerDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

import org.jempeg.nodestore.IDeviceSettings;

public class ConfigurePlayerDialog extends JDialog
{
    private Vector myPanels = new Vector();
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
	    ConfigurePlayerDialog.this.setVisible(false);
	}
    }
    
    protected class CancelListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    cancel();
	    ConfigurePlayerDialog.this.setVisible(false);
	}
    }
    
    public ConfigurePlayerDialog(JFrame _frame) {
	super(_frame, "Configure Player", true);
	JTabbedPane tabbedPane = new JTabbedPane();
	myPanels.addElement(new ConfigSettingsPanel());
	myPanels.addElement(new ConfigUserInformationPanel());
	myPanels.addElement(new ConfigNetworkingPanel());
	myPanels.addElement(new ConfigMutePanel());
	myPanels.addElement(new ConfigShutdownPanel());
	myPanels.addElement(new ConfigAdvancedPanel());
	ActionListener changeListener = new ChangeListener();
	for (int i = 0; i < myPanels.size(); i++) {
	    AbstractChangeablePanel panel
		= (AbstractChangeablePanel) myPanels.elementAt(i);
	    panel.addActionListener(changeListener);
	    tabbedPane.add(panel, panel.getName());
	}
	ConfirmationPanel confirmationPanel
	    = new ConfirmationPanel(tabbedPane);
	confirmationPanel.addOkListener(new OKListener());
	confirmationPanel.addCancelListener(new CancelListener());
	getContentPane().add(confirmationPanel);
	pack();
	DialogUtils.centerWindow(this);
    }
    
    public void setConfigFile(IDeviceSettings _configFile) {
	myConfigFile = _configFile;
	read();
    }
    
    public void setChanged(boolean _changed) {
	myChanged = _changed;
    }
    
    public boolean isChanged() {
	return myChanged;
    }
    
    protected IDeviceSettings getConfigFile() {
	return myConfigFile;
    }
    
    protected void read() {
	for (int i = 0; i < myPanels.size(); i++) {
	    AbstractChangeablePanel panel
		= (AbstractChangeablePanel) myPanels.elementAt(i);
	    panel.read(myConfigFile);
	}
	setChanged(false);
    }
    
    protected void write() {
	for (int i = 0; i < myPanels.size(); i++) {
	    AbstractChangeablePanel panel
		= (AbstractChangeablePanel) myPanels.elementAt(i);
	    panel.write(myConfigFile);
	}
    }
    
    protected void apply() {
	write();
    }
    
    protected void cancel() {
	/* empty */
    }
}
