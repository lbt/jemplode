/* BasicContainerTableModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.table;
import javax.swing.table.AbstractTableModel;

import com.inzyme.container.IContainer;

public class BasicContainerTableModel extends AbstractTableModel
    implements ISortableTableModel
{
    private IContainer myContainer;
    private String myColumnTitle;
    /*synthetic*/ static Class class$0;
    
    public BasicContainerTableModel(IContainer _container,
				    String _columnTitle) {
	myContainer = _container;
	myColumnTitle = _columnTitle;
    }
    
    public int getRowCount() {
	int rowCount = myContainer.getSize();
	return rowCount;
    }
    
    public int getColumnCount() {
	int columnCount = 1;
	return columnCount;
    }
    
    public String getColumnName(int _columnIndex) {
	return myColumnTitle;
    }
    
    public Object getValueAt(int _rowIndex, int _columnIndex) {
	Object rowValue = getValueAt(_rowIndex);
	return rowValue;
    }
    
    public Class getSortColumnClass(int _column) {
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_0_;
	    try {
		var_class_0_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_0_;
	}
	return var_class;
    }
    
    public Object getSortValueAt(int _row, int _column) {
	return getValueAt(_row, 0);
    }
    
    public Object getValueAt(int _row) {
	return myContainer.getValueAt(_row);
    }
    
    public int getSize() {
	return getRowCount();
    }
    
    public String getName() {
	return myContainer.getName();
    }
}
