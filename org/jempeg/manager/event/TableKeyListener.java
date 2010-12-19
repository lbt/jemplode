/* TableKeyListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.inzyme.util.Timer;

public class TableKeyListener extends KeyAdapter
{
    private StringBuffer mySelection = new StringBuffer();
    private Timer myTimer = new Timer(1000, this, "clear");
    
    public void keyPressed(KeyEvent _event) {
	if (!_event.isMetaDown() && !_event.isControlDown()
	    && !_event.isAltDown()) {
	    JTable table = (JTable) _event.getSource();
	    mySelection.append(_event.getKeyChar());
	    TableModel tableModel = table.getModel();
	    int size = tableModel.getRowCount();
	    String selectionStr = mySelection.toString().toLowerCase();
	    boolean selected = false;
	    for (int i = 0; !selected && i < size; i++) {
		String value
		    = tableModel.getValueAt(i, 0).toString().toLowerCase();
		if (value.startsWith(selectionStr)) {
		    table.setRowSelectionInterval(i, i);
		    table.scrollRectToVisible(table.getCellRect(i, 0, true));
		    selected = true;
		}
	    }
	    myTimer.mark();
	}
    }
    
    public void clear() {
	mySelection.setLength(0);
    }
}
