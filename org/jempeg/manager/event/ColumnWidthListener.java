package org.jempeg.manager.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jempeg.nodestore.model.IPlaylistTableModel;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.model.PlaylistTableUtils;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;
import com.inzyme.util.Timer;

/**
 * ColumnWidthListener is responsible for saving
 * and restoring the column widths that are set while
 * the user drags and repositions the columns.
 * 
 * After two seconds of column inactivity, the column
 * widths are flushed to disk.
 * 
 * @author Mike Schrag
 */
public class ColumnWidthListener implements PropertyChangeListener, TableColumnModelListener {
	private JTable myTable;
	private Timer myTimer;

	private IPlaylistTableModel myPlaylistTableModel;	
	private TableColumnModel myColumnModel;
	
	/**
	 * Constructor for ColumnWidthListener.
	 * 
	 * @param _table the table to save columns for
	 */
	public ColumnWidthListener(JTable _table) {
		myTable = _table;
		myTimer = new Timer(2000, this, "flushColumnWidths");
		
		TableModel tableModel = myTable.getModel();
		myPlaylistTableModel = PlaylistTableUtils.getPlaylistTableModel(tableModel);
		TableColumnModel columnModel = myTable.getColumnModel();
		addListeners(columnModel);
		restoreColumnWidths();
	}

	/**
	 * Removes listeners from all of the columns.
	 * 
	 * @param _oldColumnModel the column model to remove listeners from
	 */
	protected void removeListeners(TableColumnModel _oldColumnModel) {
		if (_oldColumnModel != null) {
			_oldColumnModel.removeColumnModelListener(this);
			int size = _oldColumnModel.getColumnCount();
			for (int i = 0; i < size; i ++) {
				TableColumn column = _oldColumnModel.getColumn(i);
				column.removePropertyChangeListener(this);
			}
		}
	}
	
	/**
	 * Adds listeners to all of the columns to hear size changes.
	 * 
	 * @param _newColumnModel the column model to add listeners to
	 */
	protected void addListeners(TableColumnModel _newColumnModel) {
		if (myColumnModel != null) {
			removeListeners(myColumnModel);
		}
		
		if (_newColumnModel != null) {
			_newColumnModel.addColumnModelListener(this);
			int size = _newColumnModel.getColumnCount();
			for (int i = 0; i < size; i ++) {
				TableColumn column = _newColumnModel.getColumn(i);
				column.addPropertyChangeListener(this);
			}
		}
		
		myColumnModel = _newColumnModel;
	}
	
	/**
	 * Saves all of the column widths for the table.
	 */
	public void saveColumnWidths() {
		TableColumnModel columnModel = myTable.getColumnModel();
		int columnCount = columnModel.getColumnCount();
		for (int i = 0; i < columnCount; i ++) {
			TableColumn column = columnModel.getColumn(i);
			saveColumnWidth(column);
		}
		flushColumnWidths();
	}
	
	/**
	 * Flushes saved column widths to disk.
	 */
	public synchronized void flushColumnWidths() {
    try {
      PropertiesManager.getInstance().save();
   }
    catch (IOException e) {
      Debug.println(e);
    }
	}
	
  /**
  * Saves the current column widths to the properties file.
  */
  protected void saveColumnWidth(TableColumn _column) {
  	if (myPlaylistTableModel != null) {
	    int columnIndex = _column.getModelIndex();
	    NodeTag nodeTag = myPlaylistTableModel.getNodeTag(columnIndex);
	    String tagName = nodeTag.getName();
	    int width = _column.getWidth();
	    PlaylistTableUtils.setColumnWidth(tagName, width);
  	}
  }

	/**
	 * Restores all of the column widths for the table.
	 */
	public void restoreColumnWidths() {
		TableColumnModel columnModel = myTable.getColumnModel();
  	IPlaylistTableModel tableModel = PlaylistTableUtils.getPlaylistTableModel(myTable.getModel());
  	if (tableModel != null) {
			int columnCount = columnModel.getColumnCount();
			for (int i = 0; i < columnCount; i ++) {
				NodeTag nodeTag = tableModel.getNodeTag(i);
				String tagName = nodeTag.getName();
		    int width = PlaylistTableUtils.getColumnWidth(tagName);
		    if (width != -1) {
					TableColumn column = columnModel.getColumn(i);
		      column.setPreferredWidth(width);
		    }
			}
  	}
	}
    
	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent _event) {
		String propertyName = _event.getPropertyName();
		Object source = _event.getSource();
		if (source instanceof JTable) {
			if (propertyName.equals("model")) {
				TableModel newTableModel = (TableModel)_event.getNewValue();
				
				myPlaylistTableModel = PlaylistTableUtils.getPlaylistTableModel(newTableModel);
				TableColumnModel columnModel = myTable.getColumnModel();
				addListeners(columnModel);
				// we don't need to restore column widths here since the table
				// defaults to autocreating columns from the model, which will fire
				// column remove/add events.  Those will, in turn, restore widths.
			} else if (propertyName.equals("columnModel")) {
				TableColumnModel newColumnModel = (TableColumnModel)_event.getNewValue();
				addListeners(newColumnModel);
				restoreColumnWidths();
			}
		} else if (source instanceof TableColumn) {
			TableColumn column = (TableColumn)source;
			
			// resizing column will be null if it's not a user-initiated resize
			TableColumn resizingColumn = myTable.getTableHeader().getResizingColumn();
			if ((resizingColumn != null) && propertyName.equals("width")) {
				// save the column width to properties
			  saveColumnWidth(column);
			  	
				// mark the timer and save the column widths after the specified amount of time
				myTimer.mark();
			}
		}
	}
	
	/**
	 * @see javax.swing.event.TableColumnModelListener#columnAdded(TableColumnModelEvent)
	 */
	public void columnAdded(TableColumnModelEvent _event) {
		// This is weird, but basically if we were a table model listener, we would have
		// problems because events are delivered to the most recently added listener first.
		// That means that we would get the notification that the table structure changed
		// before JTable even gets the notification.  That is a bit of a fuck since the
		// column model will not yet be updated!!  So to work around this problem, we
		// instead listen to the column model, which of course doesn't have an "I'm done"
		// notification.  Since we just reset the model, we can instead sit quietly and
		// watch for the column model to remove all the columns then count back up to 
		// the number of columns that we have in the model.  Once the two counts match,
		// then all the columns are added and we can safely restore the column widths.
		if (myColumnModel.getColumnCount() == myTable.getModel().getColumnCount()) {
			restoreColumnWidths();
		}
	}

	/**
	 * @see javax.swing.event.TableColumnModelListener#columnMarginChanged(ChangeEvent)
	 */
	public void columnMarginChanged(ChangeEvent _event) {
	}

	/**
	 * @see javax.swing.event.TableColumnModelListener#columnMoved(TableColumnModelEvent)
	 */
	public void columnMoved(TableColumnModelEvent _event) {
	}

	/**
	 * @see javax.swing.event.TableColumnModelListener#columnRemoved(TableColumnModelEvent)
	 */
	public void columnRemoved(TableColumnModelEvent _event) {
	}

	/**
	 * @see javax.swing.event.TableColumnModelListener#columnSelectionChanged(ListSelectionEvent)
	 */
	public void columnSelectionChanged(ListSelectionEvent e) {
	}

}
