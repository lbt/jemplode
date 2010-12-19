/* WendyDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.inzyme.ui.CloseDialogListener;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.SynchronizeException;
import org.jempeg.nodestore.WendyFilters;

public class WendyDialog extends JDialog
{
    private ConfirmationPanel myConfirmationPanel;
    private WendyPanel myWendyPanel;
    private ApplicationContext myContext;
    
    public WendyDialog(ApplicationContext _context)
	throws SynchronizeException {
	super(_context.getFrame(), "Wendy Filters", true);
	PlayerDatabase playerDatabase = _context.getPlayerDatabase();
	WendyFilters filters
	    = playerDatabase.getDeviceSettings().getWendyFilters();
	myWendyPanel = new WendyPanel(filters);
	myContext = _context;
	myConfirmationPanel = new ConfirmationPanel(myWendyPanel);
	myConfirmationPanel.addOkListener(new CloseDialogListener(this) {
	    public void actionPerformed(ActionEvent _event) {
		try {
		    ok();
		} catch (SynchronizeException e) {
		    Debug.handleError((JFrame) WendyDialog.this.getParent(), e,
				      true);
		}
		super.actionPerformed(_event);
	    }
	});
	myConfirmationPanel.addCancelListener(new CloseDialogListener(this) {
	    public void actionPerformed(ActionEvent _event) {
		cancel();
		super.actionPerformed(_event);
	    }
	});
	getContentPane().add(myConfirmationPanel);
	pack();
	setSize(Math.max(420, getSize().width), 350);
	DialogUtils.centerWindow(this);
    }
    
    public void ok() throws SynchronizeException {
	WendyFilters filters = myWendyPanel.getWendyFilters();
	PlayerDatabase playerDatabase = myContext.getPlayerDatabase();
	playerDatabase.getDeviceSettings().setWendyFilters(filters,
							   playerDatabase);
    }
    
    public void cancel() {
	/* empty */
    }
    
    public void addOkListener(ActionListener _listener) {
	myConfirmationPanel.addOkListener(_listener);
    }
    
    public void addCancelListener(ActionListener _listener) {
	myConfirmationPanel.addCancelListener(_listener);
    }
}
