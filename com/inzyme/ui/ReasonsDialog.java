/* ReasonsDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.ui;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.inzyme.model.Reason;
import com.inzyme.table.ReasonsTableModel;

public class ReasonsDialog extends JDialog
{
    private ConfirmationPanel myConfirmationPanel;
    
    public ReasonsDialog(JFrame _frame, String _title, String _label,
			 Reason[] _reasons, boolean _showFilenames) {
	this(_frame, _title, _label, "OK", "Cancel", _reasons, _showFilenames);
    }
    
    public ReasonsDialog(JFrame _frame, String _title, String _label,
			 String _okLabel, String _cancelLabel,
			 Reason[] _reasons, boolean _showFilenames) {
	super(_frame, _title, true);
	JPanel layoutPanel = new JPanel();
	layoutPanel.setLayout(new BorderLayout());
	JLabel label = new JLabel(_label);
	layoutPanel.add(label, "North");
	if (_showFilenames) {
	    ReasonsTableModel reasonsTableModel
		= new ReasonsTableModel(_reasons);
	    JTable reasonsTable = new JTable(reasonsTableModel);
	    JScrollPane reasonsScrollPane = new JScrollPane(reasonsTable);
	    layoutPanel.add(reasonsScrollPane, "Center");
	} else {
	    Vector reasons = new Vector();
	    for (int i = 0; i < _reasons.length; i++)
		reasons.addElement(_reasons[i].getReason());
	    JList reasonsList = new JList(reasons);
	    JScrollPane reasonsScrollPane = new JScrollPane(reasonsList);
	    layoutPanel.add(reasonsScrollPane, "Center");
	}
	myConfirmationPanel = new ConfirmationPanel(layoutPanel);
	myConfirmationPanel.addOkListener(new CloseDialogListener(this));
	myConfirmationPanel.addCancelListener(new CloseDialogListener(this));
	if (_okLabel == null)
	    myConfirmationPanel.setOkVisible(false);
	else
	    myConfirmationPanel.setOkText(_okLabel);
	if (_cancelLabel == null)
	    myConfirmationPanel.setCancelVisible(false);
	else
	    myConfirmationPanel.setCancelText(_cancelLabel);
	getContentPane().add(myConfirmationPanel);
	pack();
	setSize(Math.max(420, getSize().width), 350);
	DialogUtils.centerWindow(this);
    }
    
    public int getSelectedOption() {
	return myConfirmationPanel.getSelectedOption();
    }
    
    public void addOkListener(ActionListener _listener) {
	myConfirmationPanel.addOkListener(_listener);
    }
    
    public void addCancelListener(ActionListener _listener) {
	myConfirmationPanel.addCancelListener(_listener);
    }
}
