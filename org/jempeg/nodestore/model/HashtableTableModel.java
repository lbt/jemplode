package org.jempeg.nodestore.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.inzyme.container.ContainerUtils;
import com.inzyme.container.ToStringSortableContainer;
import com.inzyme.text.CollationKeyCache;

/**
 * HashtableTableModel is an implementation of TableModel
 * that provides a two column (key/value) view of a Hashtable.
 * 
 * @author Mike Schrag
 */
public class HashtableTableModel extends AbstractTableModel {
	private Hashtable myHashtable;
	private Vector myKeys;
	private Vector myModifiedKeys;
	
	public HashtableTableModel() {
		this(new Hashtable());
	}
	
	public HashtableTableModel(Hashtable _hashtable) {
		setHashtable(_hashtable);
	}

	public void setHashtable(Hashtable _hashtable) {
		myHashtable = _hashtable;
		myKeys = new Vector();
		myModifiedKeys = new Vector();
		
		CollationKeyCache cache = CollationKeyCache.createDefaultCache();
		ToStringSortableContainer keysContainer = new ToStringSortableContainer(myKeys);
		Enumeration keysEnum = myHashtable.keys();
		while (keysEnum.hasMoreElements()) {
			Object key = (Object)keysEnum.nextElement();
			myKeys.insertElementAt(key, ContainerUtils.getSortedIndex(keysContainer, null, key, cache));
		}
		
		fireTableDataChanged();
	}
	
	public Hashtable getHashtable() {
		return myHashtable;
	}
	
	public void put(Object _key, Object _value) {
		myHashtable.put(_key, _value);
		if (!myKeys.contains(_key)) {
			myKeys.insertElementAt(_key, ContainerUtils.getSortedIndex(new ToStringSortableContainer(myKeys), null, _key, CollationKeyCache.createDefaultCache()));
			//int row = myKeys.size() - 1;
			//fireTableRowsInserted(row, row);
			fireTableDataChanged();
		} else {
			int row = myKeys.indexOf(_key);
			myHashtable.put(_key, _value);
			fireTableCellUpdated(row, 1);
		}
		keyModified(_key);
	}
	
	public void remove(Object _key) {
		remove(_key, true);
	}
	
	protected void remove(Object _key, boolean _fireEvents) {
		int row = myKeys.indexOf(_key);
		if (row > -1) {
			myKeys.removeElementAt(row);
			myHashtable.remove(_key);
			if (_fireEvents) {
				fireTableRowsDeleted(row, row);
			}
			keyModified(_key);
		}
	}
	
	public Enumeration getModifiedKeys() {
		return myModifiedKeys.elements();
	}
	
	protected void keyModified(Object _key) {
		if (!myModifiedKeys.contains(_key)) {
			myModifiedKeys.addElement(_key);
		}
	}
	
	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return myKeys.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int _rowIndex, int _columnIndex) {
		Object cellValue;
		
		Object key = myKeys.elementAt(_rowIndex);
		if (_columnIndex == 0) {
			cellValue = key;
		} else {
			cellValue = myHashtable.get(key);
		}
		
		return cellValue;
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int _rowIndex, int _columnIndex) {
		return true;
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
	 */
	public void setValueAt(Object _aValue, int _rowIndex, int _columnIndex) {
		boolean shouldSetValue = false;
		
		Object key = myKeys.elementAt(_rowIndex);
		Object value = myHashtable.get(key);
		if (_columnIndex == 0) {
			if (!myHashtable.containsKey(_aValue)) {
				remove(key, false);
				key = _aValue;
				shouldSetValue = true;
			}
		} else {
			value = _aValue;
			shouldSetValue = true;
		}
		if (shouldSetValue) {
			put(key, value);
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int _columnIndex) {
		Class columnClass = String.class;
		return columnClass;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int _columnIndex) {
		String columnName;
		if (_columnIndex == 0) {
			columnName = "Name";
		} else {
			columnName = "Value";
		}
		return columnName;
	}

}
