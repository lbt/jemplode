/* SelectAllAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import org.jempeg.ApplicationContext;

public class SelectAllAction extends AbstractAction
{
    private ApplicationContext myContext;
    private JTable myTable;
    
    public SelectAllAction(ApplicationContext _context, JTable _table) {
	myContext = _context;
	myTable = _table;
    }
    
    public void actionPerformed(ActionEvent _event) {
	JTable table = myTable == null ? myContext.getTable() : myTable;
	table.selectAll();
	table.grabFocus();
    }
}
