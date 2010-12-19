/* InvertSelectionAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.jempeg.ApplicationContext;

public class InvertSelectionAction extends AbstractAction
{
    private ApplicationContext myContext;
    private JTable myTable;
    
    public InvertSelectionAction(ApplicationContext _context, JTable _table) {
	myContext = _context;
	myTable = _table;
    }
    
    public void actionPerformed(ActionEvent _event) {
	JTable table = myTable == null ? myContext.getTable() : myTable;
	int rowCount = table.getRowCount();
	int[] selectedRows = table.getSelectedRows();
	int[] invertedRows = new int[rowCount - selectedRows.length];
	int invertedRowIndex = 0;
	for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
	    boolean selected = false;
	    for (int selectedRowIndex = 0;
		 !selected && selectedRowIndex < selectedRows.length;
		 selectedRowIndex++) {
		if (selectedRows[selectedRowIndex] == rowIndex)
		    selected = true;
	    }
	    if (!selected)
		invertedRows[invertedRowIndex++] = rowIndex;
	}
	ListSelectionModel selectionModel = table.getSelectionModel();
	selectionModel.clearSelection();
	for (int i = 0; i < invertedRows.length; i++)
	    selectionModel.addSelectionInterval(invertedRows[i],
						invertedRows[i]);
	table.grabFocus();
    }
}
