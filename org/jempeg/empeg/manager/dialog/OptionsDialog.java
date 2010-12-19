/* OptionsDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

import org.jempeg.ApplicationContext;

public class OptionsDialog extends JDialog
{
    private OptionsPanel myOptionsPanel;
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
	    OptionsDialog.this.setVisible(false);
	}
    }
    
    protected class CancelListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    cancel();
	    OptionsDialog.this.setVisible(false);
	}
    }
    
    public OptionsDialog(ApplicationContext _context, JFrame _frame) {
	super(_frame, "Options", true);
	myOptionsPanel = new OptionsPanel(_context);
	ActionListener changeListener = new ChangeListener();
	myOptionsPanel.addActionListener(changeListener);
	ConfirmationPanel confirmationPanel
	    = new ConfirmationPanel(new JScrollPane(myOptionsPanel));
	confirmationPanel.addOkListener(new OKListener());
	confirmationPanel.addCancelListener(new CancelListener());
	getContentPane().add(confirmationPanel);
	pack();
	read();
	setSize(620, 400);
	DialogUtils.centerWindow(this);
    }
    
    public void setChanged(boolean _changed) {
	myChanged = _changed;
    }
    
    public boolean isChanged() {
	return myChanged;
    }
    
    protected void read() {
	myOptionsPanel.read(null);
	setChanged(false);
    }
    
    protected void apply() {
	myOptionsPanel.write(null);
    }
    
    protected void cancel() {
	/* empty */
    }
}
