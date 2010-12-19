/* TableModelCleanupListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.inzyme.table.SortedTableModel;

import org.jempeg.ApplicationContext;

public class TableModelCleanupListener implements PropertyChangeListener
{
    private ApplicationContext myContext;
    
    public TableModelCleanupListener(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void propertyChange(PropertyChangeEvent _event) {
	if (_event.getPropertyName().equals("model")) {
	    JTable table = (JTable) _event.getSource();
	    TableModel oldTableModel = (TableModel) _event.getOldValue();
	    if (oldTableModel instanceof SortedTableModel) {
		SortedTableModel oldSortedTableModel
		    = (SortedTableModel) oldTableModel;
		oldSortedTableModel.removeListeners();
	    }
	    myContext.clearSelection(table);
	}
    }
}
