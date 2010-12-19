package org.jempeg.manager.event;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.IFIDPlaylistWrapper;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.table.SortedTableModel;

/**
 * Updates the context with the currently selected objects
 * from the table view.
 *
 * @author Mike Schrag
 * @version $Revision: 1.4 $
 */
public class PlaylistTableSelectionListener implements ListSelectionListener, FocusListener {
	private ApplicationContext myContext;
	private JTable myTable;

	public PlaylistTableSelectionListener(ApplicationContext _context, JTable _table) {
		myContext = _context;
		myTable = _table;
	}

	public void valueChanged(ListSelectionEvent _event) {
		if (!_event.getValueIsAdjusting()) {
			selectionChanged();
		}
	}
	
	private void selectionChanged() {
		TableModel tableModel = myTable.getModel();
		if (tableModel instanceof IContainer) {
			IContainer container = (IContainer) myTable.getModel();
			int[] selectedRows = myTable.getSelectedRows();
			if (selectedRows == null || selectedRows.length == 0) {
//MODIFIED
//				myContext.setTableSelection(myTable, null);
				myContext.clearSelection(myTable);
			}
			else {
				if (container instanceof SortedTableModel) {
					SortedTableModel sortedTableModel = (SortedTableModel) container;
					container = (IContainer) sortedTableModel.getModel();
					selectedRows = sortedTableModel.getInternalRowsFor(selectedRows);
				}

				if (container instanceof IFIDPlaylistWrapper) {
					container = ((IFIDPlaylistWrapper) container).getPlaylist();
				}

//MODIFIED				
//				myContext.setTableSelection(myTable, new ContainerSelection(container, selectedRows));
				myContext.setSelection(myTable, new ContainerSelection(myContext, container, selectedRows));
			}
		}
	}
	
	public void focusGained(FocusEvent _event) {
		selectionChanged();
	}
	
	public void focusLost(FocusEvent _event) {
		// myContext.setSelection(myTable, null);
	}
}
