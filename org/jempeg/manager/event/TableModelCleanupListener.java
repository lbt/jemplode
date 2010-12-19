package org.jempeg.manager.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.jempeg.ApplicationContext;

import com.inzyme.table.SortedTableModel;

/**
 * TableModelCleanupListener removes listeners from the table model
 * when its parent table removes it from being the "active" model.
 * 
 * @author Mike Schrag
 */
public class TableModelCleanupListener implements PropertyChangeListener {
	private ApplicationContext myContext;
	
	/**
	 * Constructor for TableModelCleanupListener.
	 */
	public TableModelCleanupListener(ApplicationContext _context) {
		myContext = _context;
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent _event) {
		if (_event.getPropertyName().equals("model")) {
			JTable table = (JTable)_event.getSource();
			TableModel oldTableModel = (TableModel)_event.getOldValue();
			if (oldTableModel instanceof SortedTableModel) {
				SortedTableModel oldSortedTableModel = (SortedTableModel)oldTableModel;
				oldSortedTableModel.removeListeners();
			}
			// MODIFIED
			// myContext.setTableSelection(table, null);
			myContext.clearSelection(table);
		}
	}
}
