package org.jempeg.nodestore.model;

import java.io.IOException;

import org.jempeg.JEmplodeProperties;
import org.jempeg.nodestore.IFIDNode;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.table.ISortableTableModel;
import com.inzyme.table.SortedTableModel;

/**
 * @author mschrag
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SortedPlaylistTableModel extends SortedTableModel implements IPlaylistTableModel {
	/**
	 * Constructor for SortedPlaylistTableModel.
	 */
	public SortedPlaylistTableModel() {
		super();
	}

	/**
	 * Constructor for SortedPlaylistTableModel.
	 * @param model
	 */
	public SortedPlaylistTableModel(IPlaylistTableModel _model) {
		super(_model);
	}

	public void setModel(ISortableTableModel _model) {
		if (!(_model instanceof IPlaylistTableModel)) {
			throw new IllegalArgumentException("You can only use a SortedPlaylistTableModel to sort an IPlaylistTableModel.");
		}
		super.setModel(_model);
	}
	
	/**
	 * @see org.jempeg.empeg.model.SortedTableModel#init()
	 */
	protected void init() {
		super.init();
		restoreSortSettings();
	}

	protected void saveSortSettings(int _column, boolean _ascending) {
		PropertiesManager propertiesManager = PropertiesManager.getInstance();
		int sortingColumn = _column;
		NodeTag tag = getNodeTag(sortingColumn);
		propertiesManager.setProperty("jempeg.playlist.sort.tag", tag.getName());
		propertiesManager.setBooleanProperty("jempeg.playlist.sort.direction", _ascending);
		
		try {
			propertiesManager.save();
		}
		catch (IOException e) {
			// This bitches a LOT on my machine ... not sure why
			//Debug.println(e);
		}
	}

	protected void restoreSortSettings() {
		PropertiesManager propertiesManager = PropertiesManager.getInstance();
		String sortTag = propertiesManager.getProperty(JEmplodeProperties.SORT_TAG_KEY);
		boolean direction = propertiesManager.getBooleanProperty(JEmplodeProperties.SORT_DIRECTION_KEY);

		int sortColumn = -1;
		int columnCount = getColumnCount();
		for (int i = 0; sortColumn == -1 && i < columnCount; i ++) {
			NodeTag tag = getNodeTag(i);
			if (tag.getName().equals(sortTag)) {
				sortColumn = i;
			}
		}
		
		if (sortColumn == -1) {
			sortColumn = 0;
		}
		
		sortByColumn(sortColumn, direction);
	}
	
	/**
	 * @see org.jempeg.empeg.model.IPlaylistTableModel#getNodeTag(int)
	 */
	public NodeTag getNodeTag(int _columnNum) {
		IPlaylistTableModel playlistTableModel = (IPlaylistTableModel) model;
		return playlistTableModel.getNodeTag(_columnNum);
	}

	/**
	 * @see org.jempeg.empeg.model.IPlaylistTableModel#getNodeAt(int)
	 */
	public IFIDNode getNodeAt(int _row) {
		IFIDNode node = (IFIDNode) getValueAt(_row);
		return node;
	}
	
	/**
	 * @see org.jempeg.empeg.model.IPlaylistTableModel#getColumnTagNames()
	 */
	public String[] getColumnTagNames() {
		IPlaylistTableModel playlistTableModel = (IPlaylistTableModel) model;
		return playlistTableModel.getColumnTagNames();
	}

	/**
	 * @see org.jempeg.empeg.model.IPlaylistTableModel#setColumnTagNames(String[])
	 */
	public void setColumnTagNames(String[] _columnTagNames) {
		IPlaylistTableModel playlistTableModel = (IPlaylistTableModel) model;
		playlistTableModel.setColumnTagNames(_columnTagNames);
	}
	
	/**
	 * @see org.jempeg.empeg.model.SortedTableModel#sortByColumn(int, boolean)
	 */
	public void sortByColumn(int _column, boolean _ascending) {
		saveSortSettings(_column, _ascending);
		super.sortByColumn(_column, _ascending);
	}
	
	/**
	 * Returns the NodeTag that is currently being sorted.
	 * 
	 * @return the NodeTag that is currently being sorted
	 */
	public NodeTag getSortingNodeTag() {
		int sortingColumn = getSortingColumn();
		NodeTag sortingNodeTag = getNodeTag(sortingColumn);
		return sortingNodeTag;
	}

	protected void resort() {
		restoreSortSettings();
		super.resort();
	}
}
