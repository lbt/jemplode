/* VersionChangeDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;

import org.jempeg.empeg.versiontracker.IVersionTracker;
import org.jempeg.empeg.versiontracker.VersionChange;
import org.jempeg.empeg.versiontracker.VersionChangeTableModel;

public class VersionChangeDialog extends JDialog
{
    private boolean myShouldUpgrade;
    
    protected class OkListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    myShouldUpgrade = true;
	    VersionChangeDialog.this.setVisible(false);
	}
    }
    
    protected class CancelListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    myShouldUpgrade = false;
	    VersionChangeDialog.this.setVisible(false);
	}
    }
    
    public VersionChangeDialog
	(JFrame _frame, IVersionTracker _versionTracker) throws IOException {
	super(_frame, _versionTracker.getProductName() + " Has Changed", true);
	String productName = _versionTracker.getProductName();
	VersionChange[] changes = _versionTracker.getChanges();
	JPanel layoutPanel = new JPanel();
	layoutPanel.setLayout(new BorderLayout());
	JPanel innerLayoutPanel = new JPanel();
	innerLayoutPanel.setLayout(new BoxLayout(innerLayoutPanel, 1));
	JLabel productLabel = new JLabel(productName + " v"
					 + _versionTracker.getLatestVersion());
	productLabel.setFont(new Font("SansSerif", 1, 14));
	productLabel.setAlignmentX(0.0F);
	innerLayoutPanel.add(productLabel);
	innerLayoutPanel.add(Box.createVerticalStrut(20));
	JLabel releaseNotesLabel = new JLabel("Release Notes");
	releaseNotesLabel.setAlignmentX(0.0F);
	innerLayoutPanel.add(releaseNotesLabel);
	JTextArea releaseNotesTextArea
	    = new JTextArea(_versionTracker.getReleaseNotes());
	releaseNotesTextArea.setEditable(false);
	JScrollPane releaseNotesScrollPane
	    = new JScrollPane(releaseNotesTextArea);
	releaseNotesScrollPane.setAlignmentX(0.0F);
	innerLayoutPanel.add(releaseNotesScrollPane);
	innerLayoutPanel.add(Box.createVerticalStrut(20));
	JLabel changesLabel = new JLabel("Change Log");
	changesLabel.setAlignmentX(0.0F);
	innerLayoutPanel.add(changesLabel);
	VersionChangeTableModel versionChangeTableModel
	    = new VersionChangeTableModel(changes);
	JTable versionChangeTable = new JTable(versionChangeTableModel);
	JScrollPane versionChangeScrollPane
	    = new JScrollPane(versionChangeTable);
	versionChangeScrollPane.setAlignmentX(0.0F);
	innerLayoutPanel.add(versionChangeScrollPane);
	layoutPanel.add(innerLayoutPanel, "Center");
	ConfirmationPanel confirmationPanel
	    = new ConfirmationPanel(layoutPanel);
	confirmationPanel.setOkText("Upgrade Now");
	confirmationPanel.setCancelText("Upgrade Later");
	confirmationPanel.addOkListener(new OkListener());
	confirmationPanel.addCancelListener(new CancelListener());
	getContentPane().add(confirmationPanel);
	pack();
	setSize(Math.max(420, getSize().width), 350);
	DialogUtils.centerWindow(this);
    }
    
    public boolean shouldUpgrade() {
	return myShouldUpgrade;
    }
}
