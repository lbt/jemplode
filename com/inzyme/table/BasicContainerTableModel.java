package com.inzyme.table;

import javax.swing.table.AbstractTableModel;

import com.inzyme.container.IContainer;

/**
 * BasicContainerTableModel models the children of an IContainer as
 * a one-column table where the values are the children of the container.
 * 
 * @author Mike Schrag
 */
public class BasicContainerTableModel extends AbstractTableModel implements ISortableTableModel {
	private IContainer myContainer;
	private String myColumnTitle;

	/**
	 * Constructor for BasicContainerTableModel.
	 * 
	 * @param _container the container to model the children of
	 * @param _columnTitle the title of the single column
	 */
	public BasicContainerTableModel(IContainer _container, String _columnTitle) {
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
		return String.class;
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
