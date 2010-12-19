/* SortedTableModel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.table;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.Collator;
import java.util.Date;
import java.util.Vector;

import javax.swing.event.TableModelEvent;

import Acme.IntHashtable;

import com.inzyme.container.ContainerUtils;
import com.inzyme.model.IntVector;
import com.inzyme.text.CollationKeyCache;

public class SortedTableModel extends MappedTableModel
    implements ISortableTableModel
{
    int[] indexes;
    Vector sortingColumns;
    boolean ascending;
    int compares;
    private IntHashtable myRowToCollator;
    private Collator myCollator;
    private PropertyChangeSupport myPropertyChangeSupport;
    /*synthetic*/ static Class class$0;
    /*synthetic*/ static Class class$1;
    /*synthetic*/ static Class class$2;
    /*synthetic*/ static Class class$3;
    
    public SortedTableModel() {
	sortingColumns = new Vector();
	ascending = true;
	indexes = new int[0];
	init();
    }
    
    public SortedTableModel(ISortableTableModel _model) {
	sortingColumns = new Vector();
	ascending = true;
	setModel(_model);
	init();
    }
    
    protected void init() {
	myRowToCollator = new IntHashtable();
	myCollator = ContainerUtils.getDefaultCollator();
	myPropertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener _listener) {
	myPropertyChangeSupport.addPropertyChangeListener(_listener);
    }
    
    public void removePropertyChangeListener
	(PropertyChangeListener _listener) {
	myPropertyChangeSupport.removePropertyChangeListener(_listener);
    }
    
    public void setModel(ISortableTableModel _model) {
	super.setModel(_model);
	reallocateIndexes();
    }
    
    public Object getValueAt(int _row) {
	ISortableTableModel sortableTableModel = (ISortableTableModel) model;
	Object obj = sortableTableModel.getValueAt(indexes[_row]);
	return obj;
    }
    
    public int getRowCount() {
	return indexes.length;
    }
    
    public int[] getInternalRowsFor(int[] _externalRows) {
	int[] internalRows = new int[_externalRows.length];
	for (int i = 0; i < internalRows.length; i++)
	    internalRows[i] = indexes[_externalRows[i]];
	return internalRows;
    }
    
    public int getRowFor(int _mappedRow) {
	int row = -1;
	if (_mappedRow != -1) {
	    for (int i = 0; row == -1 && i < indexes.length; i++) {
		if (indexes[i] == _mappedRow)
		    row = i;
	    }
	}
	return row;
    }
    
    public Object getSortValueAt(int _row, int _column) {
	int row = indexes[_row];
	ISortableTableModel sortableTableModel = (ISortableTableModel) model;
	return sortableTableModel.getSortValueAt(row, _column);
    }
    
    public Class getSortColumnClass(int _column) {
	ISortableTableModel sortableTableModel = (ISortableTableModel) model;
	return sortableTableModel.getSortColumnClass(_column);
    }
    
    public int compareRowsByColumn(int row1, int row2, int column) {
	ISortableTableModel sortableTableModel = (ISortableTableModel) model;
	Class type = sortableTableModel.getSortColumnClass(column);
	Object o1 = myRowToCollator.get(row1);
	if (o1 == null)
	    o1 = sortableTableModel.getSortValueAt(row1, column);
	Object o2 = myRowToCollator.get(row2);
	if (o2 == null)
	    o2 = sortableTableModel.getSortValueAt(row2, column);
	if (o1 == null && o2 == null)
	    return 0;
	if (o1 == null)
	    return -1;
	if (o2 == null)
	    return 1;
	Class var_class = type.getSuperclass();
	Class var_class_0_ = class$0;
	if (var_class_0_ == null) {
	    Class var_class_1_;
	    try {
		var_class_1_ = Class.forName("java.lang.Number");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_0_ = class$0 = var_class_1_;
	}
	if (var_class == var_class_0_) {
	    Number n1 = (Number) o1;
	    double d1 = n1.doubleValue();
	    Number n2 = (Number) o2;
	    double d2 = n2.doubleValue();
	    if (d1 < d2)
		return -1;
	    if (d1 > d2)
		return 1;
	    return 0;
	}
	Class var_class_2_ = type;
	Class var_class_3_ = class$1;
	if (var_class_3_ == null) {
	    Class var_class_4_;
	    try {
		var_class_4_ = Class.forName("java.util.Date");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_3_ = class$1 = var_class_4_;
	}
	if (var_class_2_ == var_class_3_) {
	    Date d1 = (Date) o1;
	    long n1 = d1.getTime();
	    Date d2 = (Date) o2;
	    long n2 = d2.getTime();
	    if (n1 < n2)
		return -1;
	    if (n1 > n2)
		return 1;
	    return 0;
	}
	Class var_class_5_ = type;
	Class var_class_6_ = class$2;
	if (var_class_6_ == null) {
	    Class var_class_7_;
	    try {
		var_class_7_ = Class.forName("java.lang.String");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_6_ = class$2 = var_class_7_;
	}
	if (var_class_5_ == var_class_6_) {
	    Class var_class_8_ = o1.getClass();
	    Class var_class_9_ = class$2;
	    if (var_class_9_ == null) {
		Class var_class_10_;
		try {
		    var_class_10_ = Class.forName("java.lang.String");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class_9_ = class$2 = var_class_10_;
	    }
	    do {
		if (var_class_8_ != var_class_9_) {
		    Class var_class_11_ = o2.getClass();
		    Class var_class_12_ = class$2;
		    if (var_class_12_ == null) {
			Class var_class_13_;
			try {
			    var_class_13_ = Class.forName("java.lang.String");
			} catch (ClassNotFoundException classnotfoundexception) {
			    NoClassDefFoundError noclassdeffounderror
				= new NoClassDefFoundError;
			    ((UNCONSTRUCTED)noclassdeffounderror)
				.NoClassDefFoundError
				(classnotfoundexception.getMessage());
			    throw noclassdeffounderror;
			}
			var_class_12_ = class$2 = var_class_13_;
		    }
		    if (var_class_11_ != var_class_12_)
			break;
		}
		Class var_class_14_ = o1.getClass();
		Class var_class_15_ = class$2;
		if (var_class_15_ == null) {
		    Class var_class_16_;
		    try {
			var_class_16_ = Class.forName("java.lang.String");
		    } catch (ClassNotFoundException classnotfoundexception) {
			NoClassDefFoundError noclassdeffounderror
			    = new NoClassDefFoundError;
			((UNCONSTRUCTED)noclassdeffounderror)
			    .NoClassDefFoundError
			    (classnotfoundexception.getMessage());
			throw noclassdeffounderror;
		    }
		    var_class_15_ = class$2 = var_class_16_;
		}
		if (var_class_14_ == var_class_15_) {
		    o1 = myCollator.getCollationKey((String) o1);
		    myRowToCollator.put(row1, o1);
		}
		Class var_class_17_ = o2.getClass();
		Class var_class_18_ = class$2;
		if (var_class_18_ == null) {
		    Class var_class_19_;
		    try {
			var_class_19_ = Class.forName("java.lang.String");
		    } catch (ClassNotFoundException classnotfoundexception) {
			NoClassDefFoundError noclassdeffounderror
			    = new NoClassDefFoundError;
			((UNCONSTRUCTED)noclassdeffounderror)
			    .NoClassDefFoundError
			    (classnotfoundexception.getMessage());
			throw noclassdeffounderror;
		    }
		    var_class_18_ = class$2 = var_class_19_;
		}
		if (var_class_17_ == var_class_18_) {
		    o2 = myCollator.getCollationKey((String) o2);
		    myRowToCollator.put(row2, o2);
		}
	    } while (false);
	    int result = ((Comparable) o1).compareTo(o2);
	    if (result < 0)
		return -1;
	    if (result > 0)
		return 1;
	    return 0;
	}
	Class var_class_20_ = type;
	Class var_class_21_ = class$3;
	if (var_class_21_ == null) {
	    Class var_class_22_;
	    try {
		var_class_22_ = Class.forName("java.lang.Boolean");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_21_ = class$3 = var_class_22_;
	}
	if (var_class_20_ == var_class_21_) {
	    Boolean bool1 = (Boolean) o1;
	    boolean b1 = bool1.booleanValue();
	    Boolean bool2 = (Boolean) o2;
	    boolean b2 = bool2.booleanValue();
	    if (b1 == b2)
		return 0;
	    if (b1)
		return 1;
	    return -1;
	}
	String s1 = o1.toString();
	String s2 = o2.toString();
	int result = s1.compareToIgnoreCase(s2);
	if (result < 0)
	    return -1;
	if (result > 0)
	    return 1;
	return 0;
    }
    
    public int compare(int row1, int row2) {
	compares++;
	for (int level = 0; level < sortingColumns.size(); level++) {
	    if (level > 0)
		myRowToCollator.clear();
	    Integer column = (Integer) sortingColumns.elementAt(level);
	    int result = compareRowsByColumn(row1, row2, column.intValue());
	    if (result != 0)
		return ascending ? result : -result;
	}
	return 0;
    }
    
    public synchronized void reallocateIndexes() {
	int rowCount = model.getRowCount();
	indexes = new int[rowCount];
	for (int row = 0; row < rowCount; row++)
	    indexes[row] = row;
    }
    
    protected synchronized void deleteRows(int _internalStartIndex,
					   int _internalEndIndex) {
	int diff = _internalEndIndex - _internalStartIndex + 1;
	IntVector indexesVec = new IntVector(indexes.length - diff);
	for (int i = 0; i < indexes.length; i++) {
	    int internalIndex = indexes[i];
	    if (internalIndex < _internalStartIndex)
		indexesVec.addElement(internalIndex);
	    else if (internalIndex > _internalEndIndex)
		indexesVec.addElement(internalIndex - diff);
	}
	indexes = indexesVec.elements();
    }
    
    public synchronized int insertRow(int _insertedInternalRow) {
	for (int row = 0; row < indexes.length; row++) {
	    int internalRow = indexes[row];
	    if (internalRow >= _insertedInternalRow)
		indexes[row] = internalRow + 1;
	}
	int sortingColumn = getSortingColumn();
	Object key = model.getValueAt(_insertedInternalRow, sortingColumn);
	int sortedRow = (ContainerUtils.getSortedIndex
			 (new TableColumnContainer(this, sortingColumn), null,
			  key, CollationKeyCache.createDefaultCache()));
	IntVector indexesVec = new IntVector(indexes.length + 1);
	for (int i = 0; i < indexes.length; i++) {
	    if (i < sortedRow)
		indexesVec.addElement(indexes[i]);
	    else {
		if (i == sortedRow)
		    indexesVec.addElement(_insertedInternalRow);
		indexesVec.addElement(indexes[i]);
	    }
	}
	if (sortedRow == indexes.length)
	    indexesVec.addElement(_insertedInternalRow);
	indexes = indexesVec.elements();
	return sortedRow;
    }
    
    public synchronized void tableChanged(TableModelEvent _event) {
	int firstRow = _event.getFirstRow();
	int lastRow = _event.getLastRow();
	TableModelEvent newEvent;
	if (_event.getType() == -1) {
	    deleteRows(firstRow, lastRow);
	    newEvent = new TableModelEvent(this);
	} else if (_event.getType() == 1) {
	    if (firstRow == lastRow) {
		int sortedRow = insertRow(firstRow);
		newEvent = new TableModelEvent(this, sortedRow, sortedRow,
					       _event.getColumn(),
					       _event.getType());
	    } else {
		reallocateIndexes();
		resort();
		newEvent = new TableModelEvent(this, _event.getFirstRow(),
					       _event.getLastRow(),
					       _event.getColumn(),
					       _event.getType());
	    }
	} else if (_event.getType() == 0) {
	    reallocateIndexes();
	    resort();
	    newEvent
		= new TableModelEvent(this, _event.getFirstRow(),
				      _event.getLastRow(), _event.getColumn(),
				      _event.getType());
	} else {
	    reallocateIndexes();
	    resort();
	    newEvent
		= new TableModelEvent(this, _event.getFirstRow(),
				      _event.getLastRow(), _event.getColumn(),
				      _event.getType());
	}
	super.tableChanged(newEvent);
    }
    
    protected void resort() {
	sortByColumn(getSortingColumn(), isAscending());
    }
    
    public void checkModel() {
	if (indexes.length != 0) {
	    /* empty */
	}
	model.getRowCount();
    }
    
    public void sort(Object sender) {
	checkModel();
	compares = 0;
	if (sortingColumns.size() == 0) {
	    for (int i = 0; i < indexes.length; i++)
		indexes[i] = i;
	} else
	    shuttlesort((int[]) indexes.clone(), indexes, 0, indexes.length);
	myRowToCollator.clear();
    }
    
    public void n2sort() {
	for (int i = 0; i < getRowCount(); i++) {
	    for (int j = i + 1; j < getRowCount(); j++) {
		if (compare(indexes[i], indexes[j]) == -1)
		    swap(i, j);
	    }
	}
    }
    
    public void shuttlesort(int[] from, int[] to, int low, int high) {
	if (high - low >= 2) {
	    int middle = (low + high) / 2;
	    shuttlesort(to, from, low, middle);
	    shuttlesort(to, from, middle, high);
	    int p = low;
	    int q = middle;
	    if (high - low >= 4
		&& compare(from[middle - 1], from[middle]) <= 0) {
		for (int i = low; i < high; i++)
		    to[i] = from[i];
	    } else {
		for (int i = low; i < high; i++) {
		    if (q >= high
			|| p < middle && compare(from[p], from[q]) <= 0)
			to[i] = from[p++];
		    else
			to[i] = from[q++];
		}
	    }
	}
    }
    
    public void swap(int i, int j) {
	int tmp = indexes[i];
	indexes[i] = indexes[j];
	indexes[j] = tmp;
    }
    
    public synchronized Object getValueAt(int aRow, int aColumn) {
	checkModel();
	return model.getValueAt(indexes[aRow], aColumn);
    }
    
    public synchronized void setValueAt(Object aValue, int aRow, int aColumn) {
	checkModel();
	model.setValueAt(aValue, indexes[aRow], aColumn);
    }
    
    public void sortByColumn(int column) {
	sortByColumn(column, true);
    }
    
    public synchronized void sortByColumn(int _column, boolean _ascending) {
	boolean oldAscending = ascending;
	int oldSortColumn = getSortingColumn();
	ascending = _ascending;
	sortingColumns.removeAllElements();
	if (_column != -1)
	    sortingColumns.addElement(new Integer(_column));
	sort(this);
	super.tableChanged(new TableModelEvent(this));
	myPropertyChangeSupport.firePropertyChange("sortDirection",
						   new Boolean(oldAscending),
						   new Boolean(_ascending));
	myPropertyChangeSupport.firePropertyChange("sortColumn",
						   new Integer(oldSortColumn),
						   new Integer(_column));
    }
    
    public int getSortingColumn() {
	int sortingColumn;
	if (sortingColumns.size() > 0)
	    sortingColumn = ((Integer) sortingColumns.elementAt(0)).intValue();
	else
	    sortingColumn = -1;
	return sortingColumn;
    }
    
    public boolean isAscending() {
	return ascending;
    }
    
    public String getName() {
	ISortableTableModel sortableTableModel
	    = (ISortableTableModel) getModel();
	String name = sortableTableModel.getName();
	return name;
    }
    
    public int getSize() {
	return getRowCount();
    }
}
