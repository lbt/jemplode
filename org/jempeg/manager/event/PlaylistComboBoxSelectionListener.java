package org.jempeg.manager.event;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTable;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.model.AbstractFIDPlaylistModel;

/**
 * PlaylistComboBoxSelectionListener hooks combobox selection events up to the 
 * playlist table so that as the combobox selection changes, the table 
 * model will be updated with the contents of the corresponding 
 * combobox item.
 *
 * @author Mike Schrag
 * @version $Revision: 1.4 $
 */
public class PlaylistComboBoxSelectionListener implements ItemListener {
	private ApplicationContext myContext;
	private JTable myTable;

	public PlaylistComboBoxSelectionListener(ApplicationContext _context, JTable _table) {
		myContext = _context;
		myTable = _table;
	}

	public void itemStateChanged(ItemEvent _event) {
		FIDPlaylist selectedPlaylist;
		if (_event.getStateChange() == ItemEvent.DESELECTED) {
			selectedPlaylist = null;
		}
		else {
			AbstractFIDPlaylistModel selectedPlaylistModel = (AbstractFIDPlaylistModel) _event.getItem();
			selectedPlaylist = selectedPlaylistModel.getPlaylist();
		}

		if (myTable == myContext.getTable()) {
			myContext.setSelectedContainer(selectedPlaylist);
		}
	}
}
