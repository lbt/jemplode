/* TableSortListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.inzyme.table.SortedTableModel;

public class TableSortListener extends MouseAdapter
{
    public void mouseClicked(MouseEvent _event) {
	if ((_event.getModifiers() & 0x10) > 0) {
	    JTableHeader tableHeader = (JTableHeader) _event.getSource();
	    JTable table = tableHeader.getTable();
	    javax.swing.table.TableModel tableModel = table.getModel();
	    if (tableModel instanceof SortedTableModel) {
		SortedTableModel sortedTableModel
		    = (SortedTableModel) tableModel;
		TableColumnModel columnModel = table.getColumnModel();
		int viewColumn = columnModel.getColumnIndexAtX(_event.getX());
		int column = table.convertColumnIndexToModel(viewColumn);
		if (_event.getClickCount() % 2 != 0 && column != -1) {
		    boolean ascending = sortedTableModel.isAscending();
		    boolean clickAscending = true;
		    if (sortedTableModel.getSortingColumn() == column)
			clickAscending = !ascending;
		    int shiftPressed = _event.getModifiers() & 0x1;
		    if (shiftPressed != 0)
			clickAscending = !ascending;
		    sortedTableModel.sortByColumn(column, clickAscending);
		}
	    }
	    tableHeader.repaint();
	}
    }
}
