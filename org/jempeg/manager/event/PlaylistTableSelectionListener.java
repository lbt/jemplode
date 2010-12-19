/* PlaylistTableSelectionListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.table.SortedTableModel;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.IFIDPlaylistWrapper;

public class PlaylistTableSelectionListener
    implements ListSelectionListener, FocusListener
{
    private ApplicationContext myContext;
    private JTable myTable;
    
    public PlaylistTableSelectionListener(ApplicationContext _context,
					  JTable _table) {
	myContext = _context;
	myTable = _table;
    }
    
    public void valueChanged(ListSelectionEvent _event) {
	if (!_event.getValueIsAdjusting())
	    selectionChanged();
    }
    
    private void selectionChanged() {
	javax.swing.table.TableModel tableModel = myTable.getModel();
	if (tableModel instanceof IContainer) {
	    IContainer container = (IContainer) myTable.getModel();
	    int[] selectedRows = myTable.getSelectedRows();
	    if (selectedRows == null || selectedRows.length == 0)
		myContext.clearSelection(myTable);
	    else {
		if (container instanceof SortedTableModel) {
		    SortedTableModel sortedTableModel
			= (SortedTableModel) container;
		    container = (IContainer) sortedTableModel.getModel();
		    selectedRows
			= sortedTableModel.getInternalRowsFor(selectedRows);
		}
		if (container instanceof IFIDPlaylistWrapper)
		    container
			= ((IFIDPlaylistWrapper) container).getPlaylist();
		myContext.setSelection(myTable,
				       new ContainerSelection(myContext,
							      container,
							      selectedRows));
	    }
	}
    }
    
    public void focusGained(FocusEvent _event) {
	selectionChanged();
    }
    
    public void focusLost(FocusEvent _event) {
	/* empty */
    }
}
