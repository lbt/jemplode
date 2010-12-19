package org.jempeg.empeg.manager.event;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jempeg.manager.dialog.SynchronizeQueuePanel;

public class TransferDetailsTabListener implements ChangeListener {
	private SynchronizeQueuePanel mySynchronizeQueuePanel;
	
	public TransferDetailsTabListener(SynchronizeQueuePanel _synchronizeQueuePanel) {
		mySynchronizeQueuePanel = _synchronizeQueuePanel;
	}

	public void stateChanged(ChangeEvent _event) {
		JTabbedPane tabbedPane = (JTabbedPane)_event.getSource();
		mySynchronizeQueuePanel.setKeepHistory(tabbedPane.getSelectedComponent() == mySynchronizeQueuePanel);
	}
}
