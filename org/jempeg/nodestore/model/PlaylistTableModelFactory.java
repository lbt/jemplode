/* PlaylistTableModelFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import com.inzyme.container.IContainer;
import com.inzyme.table.BasicContainerTableModel;
import com.inzyme.table.ISortableTableModel;
import com.inzyme.table.SortedTableModel;

import org.jempeg.nodestore.FIDPlaylist;

public class PlaylistTableModelFactory
{
    public static TableModel getTableModel(Object _obj) {
	if (_obj instanceof TreePath)
	    _obj = ((TreePath) _obj).getLastPathComponent();
	TableModel tableModel;
	if (_obj instanceof SortedPlaylistTableModel)
	    tableModel = (TableModel) _obj;
	else if (_obj instanceof FIDPlaylist) {
	    FIDPlaylist playlist = (FIDPlaylist) _obj;
	    tableModel = new FIDPlaylistTableModel(playlist);
	} else if (_obj instanceof FIDPlaylistTreeNode) {
	    FIDPlaylistTreeNode fidPlaylistTreeNode
		= (FIDPlaylistTreeNode) _obj;
	    FIDPlaylist playlist = fidPlaylistTreeNode.getPlaylist();
	    tableModel = new FIDPlaylistTableModel(playlist);
	} else if (_obj instanceof IContainer) {
	    IContainer container = (IContainer) _obj;
	    tableModel = new BasicContainerTableModel(container, "Title");
	} else
	    tableModel = new DefaultTableModel();
	if (tableModel instanceof IPlaylistTableModel) {
	    if (!(tableModel instanceof SortedPlaylistTableModel))
		tableModel = new SortedPlaylistTableModel((IPlaylistTableModel)
							  tableModel);
	} else if (tableModel instanceof ISortableTableModel)
	    tableModel
		= new SortedTableModel((ISortableTableModel) tableModel);
	return tableModel;
    }
}
