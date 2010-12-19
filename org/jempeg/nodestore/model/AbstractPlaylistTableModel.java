package org.jempeg.nodestore.model;

import javax.swing.table.AbstractTableModel;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

/**
* AbstractPlaylistTableModel provides most of the implementation of
* the IPlaylistTableModel interface. 
*
* @author Mike Schrag
* @version $Revision: 1.2 $
*/
public abstract class AbstractPlaylistTableModel extends AbstractTableModel implements IPlaylistTableModel {
  private String[] myColumnTagNames;
	
	/**
	 * Constructs a new AbstractPlaylistTableModel.
	 * 
	 * @param _columnTagNames the tag names to use as columns
	 */
	public AbstractPlaylistTableModel(String[] _columnTagNames) {
    myColumnTagNames = _columnTagNames;
	}
	
	/**
	 * Returns the tag names that are used as columns for this playlist table.
	 * 
	 * @return the tag names that are used as columns for this playlist table
	 */
	public String[] getColumnTagNames() {
		return myColumnTagNames;
	}
	
	/**
	 * Sets the tag names that are used as columns for this playlist table.
	 * 
	 * @param _columnTagNames the tag names that are used as columns for this playlist table
	 */
	public void setColumnTagNames(String[] _columnTagNames) {
		myColumnTagNames = _columnTagNames;
		fireTableStructureChanged();
	}

	public String getColumnName(int _column) {
    NodeTag nodeTag = getNodeTag(_column);
    String columnName = nodeTag.getDescription();
		return columnName;
	}
	
	public int getColumnCount() {
		int columnCount = myColumnTagNames.length;
		return columnCount;
	}
	
  public Object getValueAt(int _row, int _column) {
    Object value = null;
    try {
    	IFIDNode rowNode = getNodeAt(_row);
      NodeTag nodeTag = getNodeTag(_column);
      FIDPlaylist playlist = getPlaylist();
      if (playlist == null) {
	      value = nodeTag.getDisplayValue(rowNode);
      } else {
	      value = nodeTag.getDisplayValue(playlist, _row);
      }
    }
    catch (ArrayIndexOutOfBoundsException e) {
    	try {
	      Class sortClass = getSortColumnClass(_column);
	      if (sortClass == String.class) {
	        value = "";
	      } else if (sortClass == Integer.class) {
	        value = new Integer(0);
	      } else if (sortClass == Long.class) {
	        value = new Long(0);
	      }
    	}
    	catch (ArrayIndexOutOfBoundsException e2) {
    		value = "?";
    	}
    }
		return value;
	}

  public Class getColumnClass(int _column) {
    NodeTag nodeTag = getNodeTag(_column);
    Class columnClass = nodeTag.getType();
    return columnClass;
  }

	/**
	 * Returns the Class that should be used to sort the given column.
	 * 
	 * @param _column the column to lookup
	 * @return the Class that should be used to sort the given column
	 */
  public Class getSortColumnClass(int _column) {
    NodeTag nodeTag = getNodeTag(_column);
    Class columnClass = nodeTag.getSortType();
    return columnClass;
  }

	/**
	 * Returns the value of the column for sorting purposes.
	 * 
	 * @param _row the row to lookup
	 * @param _column the column to lookup
	 */
  public Object getSortValueAt(int _row, int _column) {
    Object value = null;
    try {
    	IFIDNode rowNode = getNodeAt(_row);
      NodeTag nodeTag = getNodeTag(_column);
      FIDPlaylist playlist = getPlaylist();
      if (playlist == null) {
	      value = nodeTag.getValue(rowNode);
      } else {
	      value = nodeTag.getValue(playlist, _row);
      }
      if (value == null) {
      	value = "";
      }
    }
    catch (ArrayIndexOutOfBoundsException e) {
      Class sortClass = getSortColumnClass(_column);
      if (sortClass == String.class) {
        value = "";
      } else if (sortClass == Integer.class) {
        value = new Integer(0);
      } else if (sortClass == Long.class) {
        value = new Long(0);
      }
    }
		return value;
  }

	/**
	 * Returns the NodeTag for the given column index.
	 * 
	 * @param _columnNum the column to lookup
	 * @return the NodeTag for the given column index
	 */
  public NodeTag getNodeTag(int _columnNum) {
    NodeTag nodeTag = NodeTag.getNodeTag(myColumnTagNames[_columnNum]);
    return nodeTag;
  }
  
	public int getSize() {
		return getRowCount();
	}
	
	/**
	 * Returns the playlist that this model represents
	 * or null if there isn't one.
	 * 
	 * @return the playlist that this model represents
	 */
	protected abstract FIDPlaylist getPlaylist();
}