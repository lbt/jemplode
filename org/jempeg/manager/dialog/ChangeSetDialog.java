/* ChangeSetDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.inzyme.model.Reason;
import com.inzyme.table.ReasonsTableModel;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.CloseDialogListener;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.FIDChangeSet;

public class ChangeSetDialog extends JDialog
{
    private ConfirmationPanel myConfirmationPanel;
    
    public ChangeSetDialog(JFrame _frame, String _title, String _operation,
			   FIDChangeSet _changeSet, boolean _showSuccessful,
			   boolean _showFailed) {
	super(_frame, _title, true);
	IFIDNode[] addedNodes = _changeSet.getAddedNodes();
	String[] addedNodeTitles = new String[addedNodes.length];
	for (int i = 0; i < addedNodes.length; i++) {
	    String title = addedNodes[i].getTitle();
	    addedNodeTitles[i] = title;
	}
	Reason[] skippedReasons = _changeSet.getSkippedReasons();
	Reason[] failedReasons = _changeSet.getFailedReasons();
	Reason[] reasons
	    = new Reason[skippedReasons.length + failedReasons.length];
	System.arraycopy(skippedReasons, 0, reasons, 0, skippedReasons.length);
	System.arraycopy(failedReasons, 0, reasons, skippedReasons.length,
			 failedReasons.length);
	JPanel layoutPanel = new JPanel();
	layoutPanel.setLayout(new BorderLayout());
	JPanel innerLayoutPanel = new JPanel();
	innerLayoutPanel.setLayout(new BoxLayout(innerLayoutPanel, 1));
	JLabel successLabel
	    = new JLabel(ResourceBundleUtils.getUIString
			 (_operation + ".completion.success",
			  new Object[] { new Integer(addedNodes.length) }));
	successLabel.setAlignmentX(0.0F);
	innerLayoutPanel.add(successLabel);
	if (_showSuccessful) {
	    JList successList = new JList(addedNodeTitles);
	    JScrollPane successScrollPane = new JScrollPane(successList);
	    successScrollPane.setAlignmentX(0.0F);
	    innerLayoutPanel.add(successScrollPane);
	    innerLayoutPanel.add(Box.createVerticalStrut(20));
	}
	JLabel skippedLabel
	    = new JLabel(ResourceBundleUtils.getUIString
			 (_operation + ".completion.skipped",
			  (new Object[]
			   { new Integer(skippedReasons.length) })));
	skippedLabel.setAlignmentX(0.0F);
	JLabel failedLabel
	    = new JLabel(ResourceBundleUtils.getUIString
			 (_operation + ".completion.failed",
			  new Object[] { new Integer(failedReasons.length) }));
	failedLabel.setAlignmentX(0.0F);
	innerLayoutPanel.add(skippedLabel);
	innerLayoutPanel.add(failedLabel);
	if (_showFailed) {
	    ReasonsTableModel reasonsTableModel
		= new ReasonsTableModel(reasons);
	    JTable reasonsTable = new JTable(reasonsTableModel);
	    JScrollPane reasonsScrollPane = new JScrollPane(reasonsTable);
	    reasonsScrollPane.setAlignmentX(0.0F);
	    innerLayoutPanel.add(reasonsScrollPane);
	}
	layoutPanel.add(innerLayoutPanel, "Center");
	myConfirmationPanel = new ConfirmationPanel(layoutPanel);
	myConfirmationPanel.setCancelVisible(false);
	myConfirmationPanel.addOkListener(new CloseDialogListener(this));
	myConfirmationPanel.addCancelListener(new CloseDialogListener(this));
	getContentPane().add(myConfirmationPanel);
	pack();
	setSize(420, 350);
	DialogUtils.centerWindow(this);
    }
    
    public void addOkListener(ActionListener _listener) {
	myConfirmationPanel.addOkListener(_listener);
    }
    
    public void addCancelListener(ActionListener _listener) {
	myConfirmationPanel.addCancelListener(_listener);
    }
}
