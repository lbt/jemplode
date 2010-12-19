/* SetPlaylistOrderAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDPlaylistWrapper;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.model.SortedPlaylistTableModel;

public class SetPlaylistOrderAction extends AbstractAction
{
    private ApplicationContext myContext;
    private JTable myTable;
    
    public SetPlaylistOrderAction(ApplicationContext _context, JTable _table) {
	myContext = _context;
	myTable = _table;
    }
    
    public void performAction() {
	com.inzyme.container.IContainer container
	    = myContext.getSelectedContainer();
	if (container instanceof IFIDPlaylistWrapper) {
	    FIDPlaylist playlist
		= ((IFIDPlaylistWrapper) container).getPlaylist();
	    JTable table = myTable == null ? myContext.getTable() : myTable;
	    javax.swing.table.TableModel tableModel = table.getModel();
	    if (tableModel instanceof SortedPlaylistTableModel) {
		SortedPlaylistTableModel sortedPlaylistTableModel
		    = (SortedPlaylistTableModel) tableModel;
		NodeTag sortingNodeTag
		    = sortedPlaylistTableModel.getSortingNodeTag();
		boolean ascending = sortedPlaylistTableModel.isAscending();
		playlist.sortBy(sortingNodeTag, ascending);
	    }
	}
    }
    
    public void actionPerformed(ActionEvent _event) {
	performAction();
    }
}
