/* TreeKeyListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.event;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTree;

public class TreeKeyListener extends KeyAdapter
{
    public synchronized void keyPressed(KeyEvent _event) {
	if (!_event.isMetaDown() && !_event.isControlDown()
	    && !_event.isAltDown()) {
	    JTree tree = (JTree) _event.getSource();
	    String selectionStr
		= new String(new char[] { _event.getKeyChar() }).toLowerCase();
	    int size = tree.getRowCount();
	    int[] selectionRows = tree.getSelectionRows();
	    int selectionRow;
	    if (selectionRows != null && selectionRows.length > 0)
		selectionRow = selectionRows[0];
	    else
		selectionRow = 0;
	    boolean done = false;
	    boolean selected = false;
	    do {
		for (int i = selectionRow + 1; !selected && i < size; i++) {
		    String value
			= tree.getPathForRow(i).getLastPathComponent().toString
			      ().toLowerCase();
		    if (value.startsWith(selectionStr)) {
			tree.setSelectionInterval(i, i);
			tree.scrollRowToVisible(i);
			selected = true;
		    }
		}
		if (!selected && selectionRow != 0)
		    selectionRow = 0;
		else
		    done = true;
	    } while (!selected && !done);
	}
    }
}
