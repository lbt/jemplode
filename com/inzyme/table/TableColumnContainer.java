/* TableColumnContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.table;
import javax.swing.table.TableModel;

import com.inzyme.container.ISortableContainer;

import org.jempeg.nodestore.model.NodeTag;

public class TableColumnContainer implements ISortableContainer
{
    private TableModel myModel;
    private int myColumn;
    
    public TableColumnContainer(TableModel _model, int _column) {
	myModel = _model;
	myColumn = _column;
    }
    
    public Object getSortValueAt(NodeTag _sortOnNodeTag, int _index) {
	return getValueAt(_index);
    }
    
    public Object getSortValue(NodeTag _sortOnNodeTag, Object _value) {
	return _value;
    }
    
    public String getName() {
	return myModel.getColumnName(myColumn);
    }
    
    public int getSize() {
	return myModel.getRowCount();
    }
    
    public Object getValueAt(int _index) {
	return myModel.getValueAt(_index, myColumn);
    }
}
