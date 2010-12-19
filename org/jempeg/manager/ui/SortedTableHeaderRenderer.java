/* SortedTableHeaderRenderer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.ui;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.inzyme.table.SortedTableModel;

public class SortedTableHeaderRenderer implements TableCellRenderer
{
    private TableCellRenderer myOriginalRenderer;
    
    public SortedTableHeaderRenderer(TableCellRenderer _originalRenderer) {
	myOriginalRenderer = _originalRenderer;
    }
    
    public Component getTableCellRendererComponent
	(JTable _table, Object _value, boolean _isSelected, boolean _hasFocus,
	 int _row, int _column) {
	Component comp
	    = myOriginalRenderer.getTableCellRendererComponent(_table, _value,
							       _isSelected,
							       _hasFocus, _row,
							       _column);
	if (comp instanceof JLabel) {
	    JLabel label = (JLabel) comp;
	    Object tableModel = _table.getModel();
	    if (tableModel instanceof SortedTableModel) {
		SortedTableModel sortedTableModel
		    = (SortedTableModel) tableModel;
		int sortingColumn = sortedTableModel.getSortingColumn();
		if (sortingColumn == _column) {
		    boolean ascending = sortedTableModel.isAscending();
		    Color background = label.getBackground();
		    if (background == null)
			background = Color.white;
		    if (ascending)
			label.setIcon(new ArrowIcon(8, 8, 1,
						    background.brighter(),
						    background.darker(),
						    background));
		    else
			label.setIcon(new ArrowIcon(8, 8, 5,
						    background.brighter(),
						    background.darker(),
						    background));
		    label.setHorizontalTextPosition(2);
		    label.setIconTextGap(10);
		} else
		    label.setIcon(null);
	    }
	}
	return comp;
    }
}
