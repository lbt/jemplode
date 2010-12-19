package org.jempeg.nodestore.model;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.jempeg.nodestore.FIDPlaylist;

import com.inzyme.container.IContainer;
import com.inzyme.table.BasicContainerTableModel;
import com.inzyme.table.ISortableTableModel;
import com.inzyme.table.SortedTableModel;

/**
 * PlaylistTableModelFactory is responsible for creating
 * a table model that can be used to view a particular
 * type of object.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.3 $
 */
public class PlaylistTableModelFactory {
	/**
	 * Returns a table model for the given object.
	 * 
	 * @param _obj the Object to make a table model for
	 * @return a table model for the given Object
	 */
	public static TableModel getTableModel(Object _obj) {
		TableModel tableModel;

		if (_obj instanceof TreePath) {
			_obj = ((TreePath) _obj).getLastPathComponent();
		}

		if (_obj instanceof SortedPlaylistTableModel) {
			tableModel = (TableModel) _obj;
		}
		else if (_obj instanceof FIDPlaylist) {
			FIDPlaylist playlist = (FIDPlaylist) _obj;
			tableModel = new FIDPlaylistTableModel(playlist);
		}
		else if (_obj instanceof FIDPlaylistTreeNode) {
			FIDPlaylistTreeNode fidPlaylistTreeNode = (FIDPlaylistTreeNode) _obj;
			FIDPlaylist playlist = fidPlaylistTreeNode.getPlaylist();
			tableModel = new FIDPlaylistTableModel(playlist);
		}
		else if (_obj instanceof IContainer) {
			IContainer container = (IContainer) _obj;
			tableModel = new BasicContainerTableModel(container, "Title");
		}
		else {
			tableModel = new DefaultTableModel();
		}

		if (tableModel instanceof IPlaylistTableModel) {
			if (!(tableModel instanceof SortedPlaylistTableModel)) {
				tableModel = new SortedPlaylistTableModel((IPlaylistTableModel) tableModel);
			}
		}
		else if (tableModel instanceof ISortableTableModel) {
			tableModel = new SortedTableModel((ISortableTableModel) tableModel);
		}

		return tableModel;
	}
}
