/* TableDropTargetListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

import javax.swing.JTable;

import com.inzyme.container.IContainer;
import com.inzyme.table.SortedTableModel;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.model.IPlaylistTableModel;

public class TableDropTargetListener extends AbstractFileDropTargetListener
{
    private JTable myTable;
    
    public TableDropTargetListener(ApplicationContext _context,
				   JTable _table) {
	super(_context);
	myTable = _table;
    }
    
    public void dragEnter(DropTargetDragEvent _event) {
	super.dragEnter(_event);
	myTable.requestFocus();
    }
    
    protected boolean isValid(DropTargetEvent _event) {
	if (!(myTable.getModel() instanceof IPlaylistTableModel)
	    && getContext().getPlayerDatabase() == null)
	    return false;
	return true;
    }
    
    protected IContainer getTargetContainer(DropTargetDropEvent _event) {
	IContainer targetContainer = null;
	Object tableModel = myTable.getModel();
	if (tableModel instanceof SortedTableModel)
	    targetContainer
		= (IContainer) ((SortedTableModel) tableModel).getModel();
	else if (tableModel instanceof IContainer)
	    targetContainer = (IContainer) tableModel;
	else
	    targetContainer = getContext().getPlayerDatabase();
	return targetContainer;
    }
}
