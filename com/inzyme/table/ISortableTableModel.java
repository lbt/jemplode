package com.inzyme.table;

import javax.swing.table.TableModel;

import com.inzyme.container.IContainer;

/**
* ISortableTableModel is a table model that can be sorted.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public interface ISortableTableModel extends IContainer, TableModel {
	/**
	 * Returns the Class that should be used to sort the given column.
	 * 
	 * @param _column the column to lookup
	 * @return the Class that should be used to sort the given column
	 */
  public Class getSortColumnClass(int _column);

	/**
	 * Returns the value of the column for sorting purposes.
	 * 
	 * @param _row the row to lookup
	 * @param _column the column to lookup
	 */
  public Object getSortValueAt(int _row, int _column);
}