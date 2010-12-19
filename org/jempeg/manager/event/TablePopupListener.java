/* TablePopupListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

public class TablePopupListener extends MouseAdapter
{
    public JPopupMenu myPopup;
    
    public TablePopupListener(JPopupMenu _popup) {
	myPopup = _popup;
    }
    
    public void mousePressed(MouseEvent _event) {
	maybeShowPopup(_event);
    }
    
    public void mouseReleased(MouseEvent _event) {
	maybeShowPopup(_event);
    }
    
    protected void maybeShowPopup(MouseEvent _event) {
	if (_event.isPopupTrigger()) {
	    JTable table = (JTable) _event.getComponent();
	    if (table.getSelectedRowCount() <= 1) {
		int row = table.rowAtPoint(_event.getPoint());
		if (row != -1)
		    table.setRowSelectionInterval(row, row);
	    }
	    table.requestFocus();
	    myPopup.show(table, _event.getX(), _event.getY());
	    _event.consume();
	}
    }
}
