package org.jempeg.nodestore.model;

import org.jempeg.nodestore.IFIDNode;

import com.inzyme.table.ISortableTableModel;

/**
* IPlaylistTableModel is a table model that contains IFIDNodes.
*
* @author Mike Schrag
* @version $Revision: 1.2 $
*/
public interface IPlaylistTableModel extends ISortableTableModel {
	/**
	 * Returns the NodeTag for the given column index.
	 * 
	 * @param _columnNum the column to lookup
	 * @return the NodeTag for the given column index
	 */
  public NodeTag getNodeTag(int _columnNum);
	
	/**
	 * Returns FIDNode at the given index.
	 * 
	 * @param _row the index to lookup
	 * @return FIDNode at the given index
	 */
	public IFIDNode getNodeAt(int _row);
	
	/**
	 * Returns the tag names that are used as columns for this playlist table.
	 * 
	 * @return the tag names that are used as columns for this playlist table
	 */
	public String[] getColumnTagNames();
	
	/**
	 * Sets the tag names that are used as columns for this playlist table.
	 * 
	 * @param _columnTagNames the tag names that are used as columns for this playlist table
	 */
	public void setColumnTagNames(String[] _columnTagNames);
}