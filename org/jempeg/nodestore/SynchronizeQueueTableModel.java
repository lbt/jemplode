package org.jempeg.nodestore;

import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.table.AbstractTableModel;

import org.jempeg.nodestore.event.ISynchronizeClientListener;
import org.jempeg.nodestore.event.ISynchronizeQueueListener;
import org.jempeg.protocol.ISynchronizeClient;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.util.ReflectionUtils;

public class SynchronizeQueueTableModel extends AbstractTableModel implements ISynchronizeQueueListener, ISynchronizeClientListener {
	private SynchronizeQueue mySynchronizeQueue;
	private Vector myDequeuedItems;
	private WrappedDatabaseChange myCurrentDatabaseChange;
	private BoundedRangeModel myCompletionModel;
	private boolean myKeepHistory;
	
	private long myDequeuedLength;
	private long myEnqeuedLength;
	private long myCurrentLength;
	
	public SynchronizeQueueTableModel(SynchronizeQueue _synchronizeQueue, ISynchronizeClient _synchronizeClient) {
		myDequeuedItems = new Vector();
		myCompletionModel = new DefaultBoundedRangeModel();
		mySynchronizeQueue = _synchronizeQueue;
		mySynchronizeQueue.addSynchronizeQueueListener(this);
		_synchronizeClient.addSynchronizeClientListener(this);
	}
	
	public long getTotalLength() {
		return myDequeuedLength + myEnqeuedLength;
	}
	
	public long getCompletedLength() {
		return myDequeuedLength - myCurrentLength;
	}
	
	public void setKeepHistory(boolean _keepHistory) {
		myKeepHistory = _keepHistory;
		if (!myKeepHistory) {
			clearDequeuedChanges();
		}
	}
	
	public BoundedRangeModel getCompletionModel() {
		return myCompletionModel;
	}
	
	public void progressUpdated() {
		myCompletionModel.setMinimum(0);
		myCompletionModel.setMaximum(100);
		double totalLength = getTotalLength();
		double completedLength = getCompletedLength();
		if (totalLength == 0) {
			myCompletionModel.setValue(0);
		} else {
			myCompletionModel.setValue((int)(100.0 * (completedLength / totalLength)));
		}
	}
	
	public void databaseChangeDequeued(SynchronizeQueue _queue, IDatabaseChange _databaseChange) {
		myDequeuedItems.addElement(new WrappedDatabaseChange(_databaseChange));
		myDequeuedLength += _databaseChange.getLength();
		myEnqeuedLength = _queue.getLength();
		progressUpdated();
		fireTableDataChanged();
	}
	
	public void databaseChangeEnqueued(SynchronizeQueue _queue, IDatabaseChange _databaseChange) {
		myEnqeuedLength = _queue.getLength();
		progressUpdated();
		fireTableDataChanged();
	}
	
	public void databaseChangeRequeued(SynchronizeQueue _queue, IDatabaseChange _databaseChange) {
		myDequeuedItems.removeElement(new WrappedDatabaseChange(_databaseChange));
		myDequeuedLength -= _databaseChange.getLength();
		myEnqeuedLength = _queue.getLength();
		progressUpdated();
		fireTableDataChanged();
	}
	
	public void databaseChangesCleared(SynchronizeQueue _queue) {
		myDequeuedItems.removeAllElements();
		myDequeuedLength = 0;
		myEnqeuedLength = _queue.getLength();
		progressUpdated();
		fireTableDataChanged();
	}
	
	public void downloadCompleted(PlayerDatabase _playerDatabase) {
	}
	
	public void downloadStarted(PlayerDatabase _playerDatabase) {
	}
	
	public void synchronizeStarted(PlayerDatabase _playerDatabase) {
	}
	
	public void synchronizeStarted(IDatabaseChange _databaseChange) {
		int index = myDequeuedItems.indexOf(new WrappedDatabaseChange(_databaseChange));
		if (index != -1) {
			myCurrentDatabaseChange = (WrappedDatabaseChange)myDequeuedItems.elementAt(index);
			myCurrentDatabaseChange.setInProgress(true);
			myCurrentLength = myCurrentDatabaseChange.getLength();
			progressUpdated();
			fireTableDataChanged();
		}
	}
	
	public void synchronizeInProgress(IDatabaseChange _databaseChange, long _current, long _total) {
		if (myCurrentDatabaseChange != null) {
			double percent = (double)_current / (double)_total;
			String percentFormat = NumberFormat.getPercentInstance().format(percent);
			String status = percentFormat;
			myCurrentDatabaseChange.setStatus(status);
			updateUI(_databaseChange);
		}
	}

	public void synchronizeCompleted(IDatabaseChange _databaseChange, boolean _successfully) {
		if (myCurrentDatabaseChange != null) {
			if (_successfully) {
				if (myCurrentDatabaseChange.getWrappedDatabaesChange() instanceof NodeRemovedDatabaseChange) {
					myCurrentDatabaseChange.setStatus(ResourceBundleUtils.getUIString("syncDetails.status.deleteComplete"));
				} else {
					myCurrentDatabaseChange.setStatus(ResourceBundleUtils.getUIString("syncDetails.status.transferComplete"));
				}
			} else {
				myCurrentDatabaseChange.setStatus(ResourceBundleUtils.getUIString("syncDetails.status.failed"));
			}
			myCurrentDatabaseChange.setInProgress(false);
			if (!myKeepHistory) {
				myDequeuedLength -= myCurrentDatabaseChange.getLength();
				myDequeuedItems.removeElement(myCurrentDatabaseChange);
			}
			myCurrentLength = 0;
			progressUpdated();
			fireTableDataChanged();
		}
	}
	
	public void synchronizeCompleted(PlayerDatabase _playerDatabase, boolean _succesfully) {
	}
	
	public int getIndexOf(IDatabaseChange _databaseChange) {
		int index = myDequeuedItems.indexOf(new WrappedDatabaseChange(_databaseChange));
		return index;
	}
	
	private void updateUI(IDatabaseChange _databaseChange) {
		int index = getIndexOf(_databaseChange);
		if (index == -1) {
			fireTableDataChanged();
		} else {
			fireTableRowsUpdated(index, index);
		}
	}
	
	public void clearDequeuedChanges() {
		for (int i = myDequeuedItems.size() - 1; i >= 0; i --) {
			WrappedDatabaseChange databaseChange = (WrappedDatabaseChange)myDequeuedItems.elementAt(i);
			if (!databaseChange.isInProgress()) {
				myDequeuedItems.removeElement(databaseChange);
			}
		}
		myDequeuedLength = 0;
		progressUpdated();
		fireTableDataChanged();
	}

	public IDatabaseChange getDatabaseChangeAt(int _row) {
		int size = myDequeuedItems.size();
		if (_row < size) {
			return null;
		} else {
			IDatabaseChange databaseChange = mySynchronizeQueue.get(_row - size);
			return databaseChange;
		}
	}
	
	public Object getValueAt(int _row, int _column) {
		Object value;
		int size = myDequeuedItems.size();
		if (_row < size) {
			WrappedDatabaseChange databaseChange = (WrappedDatabaseChange)myDequeuedItems.elementAt(_row);
			if (_column == 0) {
				value = databaseChange.getDescription();
			} else {
				value = databaseChange.getStatus();
			}
		} else {
			IDatabaseChange databaseChange = mySynchronizeQueue.get(_row - size);
			if (_column == 0) {
				value = databaseChange.getDescription();
			} else {
				if (databaseChange instanceof NodeRemovedDatabaseChange) {
					value = ResourceBundleUtils.getUIString("syncDetails.status.deletePending");
				} else {
					value = ResourceBundleUtils.getUIString("syncDetails.status.transferPending");
				}
			}
		} 
		if (value == null) {
			value = "";
		}
		return value;
	}
	
	public int getColumnCount() {
		return 2;
	}
	
	public int getRowCount() {
		return myDequeuedItems.size() + mySynchronizeQueue.getSize();
	}
	
	public String getColumnName(int _column) {
		if (_column == 0) {
			return ResourceBundleUtils.getUIString("syncDetails.header.activity");
		} else {
			return ResourceBundleUtils.getUIString("syncDetails.header.status");
		}
	}
	
	public static class WrappedDatabaseChange {
		private IDatabaseChange myDatabaseChange;
		private String myStatus;
		private boolean myInProgress;
		
		public WrappedDatabaseChange(IDatabaseChange _databaseChange) {
			myDatabaseChange = _databaseChange;
		}
		
		public void setStatus(String _status) {
			myStatus = _status;
		}
		
		public String getStatus() {
			return myStatus;
		}
		
		public void setInProgress(boolean _inProgress) {
			myInProgress = _inProgress;
		}
		
		public boolean isInProgress() {
			return myInProgress;
		}
		
		public String getDescription() {
			return myDatabaseChange.getDescription();
		}
		
		public long getLength() {
			return myDatabaseChange.getLength();
		}
		
		public IDatabaseChange getWrappedDatabaesChange() {
			return myDatabaseChange;
		}
		
		public boolean equals(Object _obj) {
			if (_obj instanceof WrappedDatabaseChange) {
				return myDatabaseChange.equals(((WrappedDatabaseChange)_obj).myDatabaseChange);
			} else if (_obj instanceof IDatabaseChange) {
				return myDatabaseChange.equals((IDatabaseChange)_obj);
			} else {
				return super.equals(_obj);
			}
		}
		
		public int hashCode() {
			return myDatabaseChange.hashCode();
		}
		
		public String toString() {
			return ReflectionUtils.toString(this);
		}
	}
}
