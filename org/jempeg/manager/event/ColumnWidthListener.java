/* ColumnWidthListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
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

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;
import com.inzyme.util.Timer;

import org.jempeg.nodestore.model.IPlaylistTableModel;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.model.PlaylistTableUtils;

public class ColumnWidthListener
    implements PropertyChangeListener, TableColumnModelListener
{
    private JTable myTable;
    private Timer myTimer;
    private IPlaylistTableModel myPlaylistTableModel;
    private TableColumnModel myColumnModel;
    
    public ColumnWidthListener(JTable _table) {
	myTable = _table;
	myTimer = new Timer(2000, this, "flushColumnWidths");
	TableModel tableModel = myTable.getModel();
	myPlaylistTableModel
	    = PlaylistTableUtils.getPlaylistTableModel(tableModel);
	TableColumnModel columnModel = myTable.getColumnModel();
	addListeners(columnModel);
	restoreColumnWidths();
    }
    
    protected void removeListeners(TableColumnModel _oldColumnModel) {
	if (_oldColumnModel != null) {
	    _oldColumnModel.removeColumnModelListener(this);
	    int size = _oldColumnModel.getColumnCount();
	    for (int i = 0; i < size; i++) {
		TableColumn column = _oldColumnModel.getColumn(i);
		column.removePropertyChangeListener(this);
	    }
	}
    }
    
    protected void addListeners(TableColumnModel _newColumnModel) {
	if (myColumnModel != null)
	    removeListeners(myColumnModel);
	if (_newColumnModel != null) {
	    _newColumnModel.addColumnModelListener(this);
	    int size = _newColumnModel.getColumnCount();
	    for (int i = 0; i < size; i++) {
		TableColumn column = _newColumnModel.getColumn(i);
		column.addPropertyChangeListener(this);
	    }
	}
	myColumnModel = _newColumnModel;
    }
    
    public void saveColumnWidths() {
	TableColumnModel columnModel = myTable.getColumnModel();
	int columnCount = columnModel.getColumnCount();
	for (int i = 0; i < columnCount; i++) {
	    TableColumn column = columnModel.getColumn(i);
	    saveColumnWidth(column);
	}
	flushColumnWidths();
    }
    
    public synchronized void flushColumnWidths() {
	try {
	    PropertiesManager.getInstance().save();
	} catch (IOException e) {
	    Debug.println(e);
	}
    }
    
    protected void saveColumnWidth(TableColumn _column) {
	if (myPlaylistTableModel != null) {
	    int columnIndex = _column.getModelIndex();
	    NodeTag nodeTag = myPlaylistTableModel.getNodeTag(columnIndex);
	    String tagName = nodeTag.getName();
	    int width = _column.getWidth();
	    PlaylistTableUtils.setColumnWidth(tagName, width);
	}
    }
    
    public void restoreColumnWidths() {
	TableColumnModel columnModel = myTable.getColumnModel();
	IPlaylistTableModel tableModel
	    = PlaylistTableUtils.getPlaylistTableModel(myTable.getModel());
	if (tableModel != null) {
	    int columnCount = columnModel.getColumnCount();
	    for (int i = 0; i < columnCount; i++) {
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
    
    public void propertyChange(PropertyChangeEvent _event) {
	String propertyName = _event.getPropertyName();
	Object source = _event.getSource();
	if (source instanceof JTable) {
	    if (propertyName.equals("model")) {
		TableModel newTableModel = (TableModel) _event.getNewValue();
		myPlaylistTableModel
		    = PlaylistTableUtils.getPlaylistTableModel(newTableModel);
		TableColumnModel columnModel = myTable.getColumnModel();
		addListeners(columnModel);
	    } else if (propertyName.equals("columnModel")) {
		TableColumnModel newColumnModel
		    = (TableColumnModel) _event.getNewValue();
		addListeners(newColumnModel);
		restoreColumnWidths();
	    }
	} else if (source instanceof TableColumn) {
	    TableColumn column = (TableColumn) source;
	    TableColumn resizingColumn
		= myTable.getTableHeader().getResizingColumn();
	    if (resizingColumn != null && propertyName.equals("width")) {
		saveColumnWidth(column);
		myTimer.mark();
	    }
	}
    }
    
    public void columnAdded(TableColumnModelEvent _event) {
	if (myColumnModel.getColumnCount()
	    == myTable.getModel().getColumnCount())
	    restoreColumnWidths();
    }
    
    public void columnMarginChanged(ChangeEvent _event) {
	/* empty */
    }
    
    public void columnMoved(TableColumnModelEvent _event) {
	/* empty */
    }
    
    public void columnRemoved(TableColumnModelEvent _event) {
	/* empty */
    }
    
    public void columnSelectionChanged(ListSelectionEvent e) {
	/* empty */
    }
}
