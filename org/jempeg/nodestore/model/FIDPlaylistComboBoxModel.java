package org.jempeg.nodestore.model;

import javax.swing.ComboBoxModel;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDPlaylistWrapper;

/**
 * FIDPlaylistComboBoxModel is a ComboBoxModel implementation
 * that wraps an FIDPlaylist.
 * 
 * @author Mike Schrag
 */
public class FIDPlaylistComboBoxModel extends FIDPlaylistListModel implements ComboBoxModel {
	private Object mySelectedItem;

	/**
	 * Constructor for FIDPlaylistComboBoxModel.
	 * @param _playlist
	 * @param _playlistIndex
	 */
	public FIDPlaylistComboBoxModel(FIDPlaylist _playlist, int _playlistIndex, boolean _showSelf) {
		super(_playlist, _playlistIndex, _showSelf);
	}

	/**
	 * @see javax.swing.ComboBoxModel#setSelectedItem(Object)
	 */
	public void setSelectedItem(Object _selectedItem) {
		if ((mySelectedItem != null && !mySelectedItem.equals(_selectedItem)) || mySelectedItem == null && _selectedItem != null) {
			mySelectedItem = _selectedItem;
			notifyContentsChanged(-1, -1);
		}
	}

	/**
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	public Object getSelectedItem() {
		return mySelectedItem;
	}
	
	public FIDPlaylist getSelectedPlaylist() {
		IFIDPlaylistWrapper wrapper = (IFIDPlaylistWrapper)mySelectedItem;
		FIDPlaylist playlist;
		if (wrapper != null) {
			playlist = wrapper.getPlaylist();
		} else {
			playlist = null;
		}
		return playlist;
	}
}
