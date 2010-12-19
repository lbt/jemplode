package org.jempeg.manager.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.ICancelableDatabaseChange;
import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.SynchronizeQueue;
import org.jempeg.nodestore.SynchronizeQueueTableModel;

public class DatabaseChangeCancelAction extends AbstractAction {
	private ApplicationContext myContext;
	private JTable mySynchronizeQueueTable;
	
	public DatabaseChangeCancelAction(ApplicationContext _context, JTable _synchronizeQueueTable) {
		myContext = _context;
		mySynchronizeQueueTable = _synchronizeQueueTable;
	}
	
	public void actionPerformed(ActionEvent _event) {
		SynchronizeQueue queue = myContext.getPlayerDatabase().getSynchronizeQueue();
		synchronized (queue.getLockObject()) {
			SynchronizeQueueTableModel tableModel = (SynchronizeQueueTableModel)mySynchronizeQueueTable.getModel();
			int[] selectedRows = mySynchronizeQueueTable.getSelectedRows();
			IDatabaseChange[] databaseChanges = new IDatabaseChange[selectedRows.length];
			for (int i = 0; i < selectedRows.length; i ++) {
				databaseChanges[i] = tableModel.getDatabaseChangeAt(selectedRows[i]);
			}
			for (int i = 0; i < databaseChanges.length; i ++) {
				if (databaseChanges[i] != null && (databaseChanges[i] instanceof ICancelableDatabaseChange)) {
					((ICancelableDatabaseChange)databaseChanges[i]).cancel();
				}
			}
		}
	}
}
