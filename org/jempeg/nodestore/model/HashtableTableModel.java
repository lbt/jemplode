/* HashtableTableModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.inzyme.container.ContainerUtils;
import com.inzyme.container.ToStringSortableContainer;
import com.inzyme.text.CollationKeyCache;

public class HashtableTableModel extends AbstractTableModel
{
    private Hashtable myHashtable;
    private Vector myKeys;
    private Vector myModifiedKeys;
    /*synthetic*/ static Class class$0;
    
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
	ToStringSortableContainer keysContainer
	    = new ToStringSortableContainer(myKeys);
	Enumeration keysEnum = myHashtable.keys();
	while (keysEnum.hasMoreElements()) {
	    Object key = keysEnum.nextElement();
	    myKeys.insertElementAt(key,
				   ContainerUtils.getSortedIndex(keysContainer,
								 null, key,
								 cache));
	}
	fireTableDataChanged();
    }
    
    public Hashtable getHashtable() {
	return myHashtable;
    }
    
    public void put(Object _key, Object _value) {
	myHashtable.put(_key, _value);
	if (!myKeys.contains(_key)) {
	    myKeys.insertElementAt(_key,
				   (ContainerUtils.getSortedIndex
				    (new ToStringSortableContainer(myKeys),
				     null, _key,
				     CollationKeyCache.createDefaultCache())));
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
	    if (_fireEvents)
		fireTableRowsDeleted(row, row);
	    keyModified(_key);
	}
    }
    
    public Enumeration getModifiedKeys() {
	return myModifiedKeys.elements();
    }
    
    protected void keyModified(Object _key) {
	if (!myModifiedKeys.contains(_key))
	    myModifiedKeys.addElement(_key);
    }
    
    public int getRowCount() {
	return myKeys.size();
    }
    
    public int getColumnCount() {
	return 2;
    }
    
    public Object getValueAt(int _rowIndex, int _columnIndex) {
	Object key = myKeys.elementAt(_rowIndex);
	Object cellValue;
	if (_columnIndex == 0)
	    cellValue = key;
	else
	    cellValue = myHashtable.get(key);
	return cellValue;
    }
    
    public boolean isCellEditable(int _rowIndex, int _columnIndex) {
	return true;
    }
    
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
	if (shouldSetValue)
	    put(key, value);
    }
    
    public Class getColumnClass(int _columnIndex) {
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
	Class columnClass = var_class;
	return columnClass;
    }
    
    public String getColumnName(int _columnIndex) {
	String columnName;
	if (_columnIndex == 0)
	    columnName = "Name";
	else
	    columnName = "Value";
	return columnName;
    }
}
