package org.jempeg.manager.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.jempeg.ApplicationContext;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.DialogUtils;
import com.inzyme.ui.UIUtils;

public class SynchronizeQueueDialog extends JDialog implements ActionListener {
	private SynchronizeQueuePanel mySynchronizeQueuePanel;
	private boolean myHasBeenVisibleBefore;
	private boolean myAlwaysKeepHistory;
	
	public SynchronizeQueueDialog(JFrame _owner, ApplicationContext _context, boolean _dualProgressBars, boolean _alwaysKeepHistory) {
		super(_owner, ResourceBundleUtils.getUIString("syncDetails.frameTitle"));
		myAlwaysKeepHistory = _alwaysKeepHistory;
		mySynchronizeQueuePanel = new SynchronizeQueuePanel(_context, _dualProgressBars);
		JButton closeButton = new JButton();
		UIUtils.initializeButton(closeButton, "close");
		closeButton.addActionListener(this);
		mySynchronizeQueuePanel.getButtonPanel().add(closeButton);
		getContentPane().add(mySynchronizeQueuePanel, BorderLayout.CENTER);
		pack();
		if (myAlwaysKeepHistory) {
			mySynchronizeQueuePanel.setKeepHistory(true);
		}
	}
	
	public SynchronizeQueuePanel getSynchronizeQueuePanel() {
		return mySynchronizeQueuePanel;
	}
	
	public void show() {
		mySynchronizeQueuePanel.setKeepHistory(true);
		if (!myHasBeenVisibleBefore) {
			DialogUtils.centerWindowRelativeTo(this, getParent().getLocation(), getParent().getSize());
			myHasBeenVisibleBefore = true;
		}
		super.show();
	}
	
	public void hide() {
		if (!myAlwaysKeepHistory) {
			mySynchronizeQueuePanel.setKeepHistory(false);
		}
		super.hide();
	}
	
	public void actionPerformed(ActionEvent _event) {
		hide();
	}
}
