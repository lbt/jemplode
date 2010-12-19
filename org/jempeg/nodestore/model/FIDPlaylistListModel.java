package org.jempeg.nodestore.model;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jempeg.nodestore.FIDPlaylist;

import com.inzyme.model.Range;

/**
 * FIDPlaylistListModel provides a ListModel implementation
 * on top of an FIDPlaylist.
 * 
 * @author Mike Schrag
 */
public class FIDPlaylistListModel extends AbstractFIDPlaylistModel implements ListModel {
	private boolean myShowSelf;
	private EventListenerList myEventListenerList;

	/**
	 * Constructor for FIDPlaylistListModel.
	 * @param _playlist
	 * @param _playlistIndex
	 */
	public FIDPlaylistListModel(FIDPlaylist _playlist, int _playlistIndex, boolean _showSelf) {
		super(_playlist, _playlistIndex);
		myEventListenerList = new EventListenerList();
		myShowSelf = _showSelf;
	}

	protected void notifyContentsChanged(int _startIndex, int _endIndex) {
		ListDataEvent event = null;
		Object[] listeners = myEventListenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (event == null) {
					event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, _startIndex, _endIndex);
				}
				((ListDataListener) listeners[i + 1]).contentsChanged(event);
			}
		}
	}

	protected void notifyStructureChanged() {
		notifyContentsChanged(0, getSize());
	}

	protected void notifyChildrenWereInserted(int[] _indexes) {
		ListDataEvent event = null;
		Object[] listeners = myEventListenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (event == null) {
					Range range = new Range(_indexes);
					event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, range.getStart(), range.getEnd());
				}
				((ListDataListener) listeners[i + 1]).intervalAdded(event);
			}
		}
	}

	protected void notifyChildrenWereRemoved(int[] _indexes, Object[] _childModels) {
		ListDataEvent event = null;
		Object[] listeners = myEventListenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (event == null) {
					Range range = new Range(_indexes);
					event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, range.getStart(), range.getEnd());
				}
				((ListDataListener) listeners[i + 1]).intervalRemoved(event);
			}
		}
	}

	protected AbstractFIDPlaylistModel createChildModel(FIDPlaylist _playlist, int _index) {
		FIDPlaylistListModel childListModel = new FIDPlaylistListModel(_playlist, _index, false);
		return childListModel;
	}

	public int getSize() {
		return super.getSize() + ((myShowSelf) ? 1 : 0);
	}

	public Object getElementAt(int _index) {
		Object obj;
		if (!myShowSelf) {
			obj = getValueAt(_index);
		}
		else if (_index == 0) {
			obj = this;
		}
		else {
			obj = getValueAt(_index - 1);
		}
		return obj;
	}

	public void addListDataListener(ListDataListener _listener) {
		myEventListenerList.add(ListDataListener.class, _listener);
	}

	public void removeListDataListener(ListDataListener _listener) {
		myEventListenerList.remove(ListDataListener.class, _listener);
	}
}
