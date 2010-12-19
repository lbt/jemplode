/* PlaylistTableCellRenderer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.ui;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.inzyme.container.IMutableTypeContainer;
import com.inzyme.table.ISortableTableModel;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.IPlaylistTableModel;

public class PlaylistTableCellRenderer implements TableCellRenderer
{
    private TableCellRenderer myOriginalRenderer;
    
    public PlaylistTableCellRenderer(TableCellRenderer _originalRenderer) {
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
	try {
	    Object tableModelObj = _table.getModel();
	    Object rowObject;
	    int type;
	    if (tableModelObj instanceof IPlaylistTableModel) {
		IPlaylistTableModel playlistTableModel
		    = (IPlaylistTableModel) tableModelObj;
		IFIDNode fidNode = playlistTableModel.getNodeAt(_row);
		type = fidNode.getType();
		rowObject = fidNode;
	    } else if (tableModelObj instanceof ISortableTableModel) {
		ISortableTableModel sortableTableModel
		    = (ISortableTableModel) tableModelObj;
		rowObject = sortableTableModel.getValueAt(_row);
		if (rowObject instanceof IMutableTypeContainer) {
		    IMutableTypeContainer containerTreeNode
			= (IMutableTypeContainer) rowObject;
		    type = containerTreeNode.getType();
		} else
		    type = 1;
	    } else {
		type = 1;
		rowObject = null;
	    }
	    if (comp instanceof JLabel) {
		JLabel label = (JLabel) comp;
		if (_column == 0)
		    label.setIcon(PlaylistTreeCellRenderer.ICONS[type]);
		else
		    label.setIcon(null);
		label.setOpaque(true);
	    }
	    Color foreground;
	    Color background;
	    if (_isSelected) {
		foreground = _table.getSelectionForeground();
		background = _table.getSelectionBackground();
	    } else {
		foreground = _table.getForeground();
		background = _table.getBackground();
	    }
	    double darkenPercentage = 0.95;
	    Color altBackground
		= new Color((int) ((double) background.getRed()
				   * darkenPercentage),
			    (int) ((double) background.getGreen()
				   * darkenPercentage),
			    (int) ((double) background.getBlue()
				   * darkenPercentage));
	    if (_row % 2 == 0)
		comp.setBackground(background);
	    else
		comp.setBackground(altBackground);
	    NodeColorizer.colorize(comp, rowObject, foreground, _isSelected);
	} catch (Throwable throwable) {
	    /* empty */
	}
	return comp;
    }
}
