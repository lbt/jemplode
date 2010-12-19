/**
 * Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
 * other contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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

/**
 * A sorter for TableModels. The sorter has a model (conforming to TableModel) 
 * and itself implements TableModel. SortedTableModel does not store or copy 
 * the data in the TableModel, instead it maintains an array of 
 * integers which it keeps the same size as the number of rows in its 
 * model. When the model changes it notifies the sorter that something 
 * has changed eg. "rowsAdded" so that its internal array of integers 
 * can be reallocated. As requests are made of the sorter (like 
 * getValueAt(row, col) it redirects them to its model via the mapping 
 * array. That way the SortedTableModel appears to hold another copy of the table 
 * with the rows in a different order. The sorting algorthm used is stable 
 * which means that it does not move around rows when its comparison 
 * function returns 0 to denote that they are equivalent. 
 *
 * @version 1.5 12/17/97
 * @author Philip Milne
 * @version $Revision: 1.4 $
 */
public class SortedTableModel extends MappedTableModel implements ISortableTableModel {
  int indexes[];
  Vector sortingColumns = new Vector();
  boolean ascending = true;
  int compares;

  private IntHashtable myRowToCollator;
  private Collator myCollator;
  private PropertyChangeSupport myPropertyChangeSupport;

  public SortedTableModel() {
    indexes = new int[0]; // for consistency
    init();
  }

  public SortedTableModel(ISortableTableModel _model) {
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

  public void removePropertyChangeListener(PropertyChangeListener _listener) {
    myPropertyChangeSupport.removePropertyChangeListener(_listener);
  }

  public void setModel(ISortableTableModel _model) {
    super.setModel(_model);
    reallocateIndexes();
  }

  /**
   * @see org.jempeg.empeg.model.ISortableTableModel#getValueAt(int)
   */
  public Object getValueAt(int _row) {
    ISortableTableModel sortableTableModel = (ISortableTableModel) model;
    Object obj = sortableTableModel.getValueAt(indexes[_row]);
    return obj;
  }

  public int getRowCount() {
    return indexes.length;
  }

  /**
   * Returns the internal rows that correspond to the
   * mapped rows.
   * 
   * @param _mappedRows the externally exposed row indexes
   * @return the internal row indexes
   */
  public int[] getInternalRowsFor(int[] _externalRows) {
    int[] internalRows = new int[_externalRows.length];
    for (int i = 0; i < internalRows.length; i ++ ) {
      internalRows[i] = indexes[_externalRows[i]];
    }
    return internalRows;
  }

  /**
   * Returns the internal row that corresponds to the
   * mapped row.
   * 
   * @param _mappedRow the externally exposed row index
   * @return the internal row index
   */
  public int getRowFor(int _mappedRow) {
    int row = -1;
    if (_mappedRow != -1) {
      for (int i = 0; row == -1 && i < indexes.length; i ++ ) {
        if (indexes[i] == _mappedRow) {
          row = i;
        }
      }
    }
    return row;
  }

  /**
   * Returns the value that will be used to sort with.
   *
   * @param _row the row to lookup
   * @param _column the column to lookup
   * @returns the value that will be used to sort with
   */
  public Object getSortValueAt(int _row, int _column) {
    int row = indexes[_row];
    ISortableTableModel sortableTableModel = (ISortableTableModel) model;
    return sortableTableModel.getSortValueAt(row, _column);
  }

  /**
   * Returns the type that will be used to sort with.
   *
   * @param _column the column to lookup
   * @returns the type of the value that will be used to sort with
   */
  public Class getSortColumnClass(int _column) {
    ISortableTableModel sortableTableModel = (ISortableTableModel) model;
    return sortableTableModel.getSortColumnClass(_column);
  }

  public int compareRowsByColumn(int row1, int row2, int column) {
    ISortableTableModel sortableTableModel = (ISortableTableModel) model;
    Class type = sortableTableModel.getSortColumnClass(column);

    // Check for nulls.

    Object o1 = myRowToCollator.get(row1);
    if (o1 == null) {
      o1 = sortableTableModel.getSortValueAt(row1, column);
    }
    Object o2 = myRowToCollator.get(row2);
    if (o2 == null) {
      o2 = sortableTableModel.getSortValueAt(row2, column);
    }

    // If both values are null, return 0.
    if (o1 == null && o2 == null) {
      return (0);
    }
    else if (o1 == null) { // Define null less than everything. 
      return (-1);
    }
    else if (o2 == null) {
      return (1);
    }

    /*
     * We copy all returned values from the getValue call in case
     * an optimised model is reusing one object to return many
     * values.  The Number subclasses in the JDK are immutable and
     * so will not be used in this way but other subclasses of
     * Number might want to do this to save space and avoid
     * unnecessary heap allocation.
     */

    if (type.getSuperclass() == java.lang.Number.class) {
      Number n1 = (Number) o1;
      double d1 = n1.doubleValue();
      Number n2 = (Number) o2;
      double d2 = n2.doubleValue();

      if (d1 < d2) {
        return (-1);
      }
      else if (d1 > d2) {
        return (1);
      }
      else {
        return (0);
      }
    }
    else if (type == java.util.Date.class) {
      Date d1 = (Date) o1;
      long n1 = d1.getTime();
      Date d2 = (Date) o2;
      long n2 = d2.getTime();

      if (n1 < n2) {
        return (-1);
      }
      else if (n1 > n2) {
        return (1);
      }
      else {
        return (0);
      }
    }
    else if (type == String.class) {

      // Getting collation keys is hard work, so we cache them
      if (o1.getClass() == String.class || o2.getClass() == String.class) {
        if (o1.getClass() == String.class) {
          o1 = myCollator.getCollationKey((String) o1);
          myRowToCollator.put(row1, o1);
        }
        if (o2.getClass() == String.class) {
          o2 = myCollator.getCollationKey((String) o2);
          myRowToCollator.put(row2, o2);
        }
      }

      int result = ((Comparable) o1).compareTo((Comparable) o2);
      //      String s1 = (String)o1;
      //      String s2    = (String)o2;
      //      int result = s1.compareToIgnoreCase(s2);

      if (result < 0) {
        return (-1);
      }
      else if (result > 0) {
        return (1);
      }
      else {
        return (0);
      }
    }
    else if (type == Boolean.class) {
      Boolean bool1 = (Boolean) o1;
      boolean b1 = bool1.booleanValue();
      Boolean bool2 = (Boolean) o2;
      boolean b2 = bool2.booleanValue();

      if (b1 == b2) {
        return (0);
      }
      else if (b1) { // Define false < true
        return (1);
      }
      else {
        return (-1);
      }
    }
    else {
      String s1 = o1.toString();
      String s2 = o2.toString();
      int result = s1.compareToIgnoreCase(s2);

      if (result < 0) {
        return (-1);
      }
      else if (result > 0) {
        return (1);
      }
      else {
        return (0);
      }
    }
  }

  public int compare(int row1, int row2) {
    compares ++;

    for (int level = 0; level < sortingColumns.size(); level ++ ) {
      if (level > 0) {
        myRowToCollator.clear();
      }
      Integer column = (Integer) sortingColumns.elementAt(level);
      int result = compareRowsByColumn(row1, row2, column.intValue());
      if (result != 0) {
        return (ascending ? result : -result);
      }
    }
    return (0);
  }

  public synchronized void reallocateIndexes() {
    int rowCount = model.getRowCount();

    // Set up a new array of indexes with the right number of elements
    // for the new data model.
    indexes = new int[rowCount];

    // Initialise with the identity mapping.
    for (int row = 0; row < rowCount; row ++ ) {
      indexes[row] = row;
    }
  }

  protected synchronized void deleteRows(int _internalStartIndex, int _internalEndIndex) {
    int diff = (_internalEndIndex - _internalStartIndex + 1);
    IntVector indexesVec = new IntVector(indexes.length - diff);
    for (int i = 0; i < indexes.length; i ++ ) {
      int internalIndex = indexes[i];
      if (internalIndex < _internalStartIndex) {
        indexesVec.addElement(internalIndex);
      }
      else if (internalIndex > _internalEndIndex) {
        indexesVec.addElement(internalIndex - diff);
      }
    }
    indexes = indexesVec.elements();
  }

  public synchronized int insertRow(int _insertedInternalRow) {
    // Initialise with the identity mapping.
    for (int row = 0; row < indexes.length; row ++ ) {
      int internalRow = indexes[row];
      if (internalRow >= _insertedInternalRow) {
        indexes[row] = internalRow + 1;
      }
    }

    int sortingColumn = getSortingColumn();
    Object key = model.getValueAt(_insertedInternalRow, sortingColumn);
    // TODO: Should this cache be at a higher level?
    // TODO: "title"?
    int sortedRow = ContainerUtils.getSortedIndex(new TableColumnContainer(this, sortingColumn), null, key, CollationKeyCache.createDefaultCache());

    IntVector indexesVec = new IntVector(indexes.length + 1);
    for (int i = 0; i < indexes.length; i ++ ) {
      if (i < sortedRow) {
        indexesVec.addElement(indexes[i]);
      }
      else {
        if (i == sortedRow) {
          indexesVec.addElement(_insertedInternalRow);
        }
        indexesVec.addElement(indexes[i]);
      }
    }
    if (sortedRow == indexes.length) {
      indexesVec.addElement(_insertedInternalRow);
    }
    indexes = indexesVec.elements();

    return sortedRow;
  }

  public synchronized void tableChanged(TableModelEvent _event) {
    TableModelEvent newEvent;

    int firstRow = _event.getFirstRow();
    int lastRow = _event.getLastRow();

    if (_event.getType() == TableModelEvent.DELETE) {
      deleteRows(firstRow, lastRow);
      newEvent = new TableModelEvent(this);
    }
    else if (_event.getType() == TableModelEvent.INSERT) {
      if (firstRow == lastRow) {
        int sortedRow = insertRow(firstRow);
        newEvent = new TableModelEvent(this, sortedRow, sortedRow, _event.getColumn(), _event.getType());
      }
      else {
        reallocateIndexes();
        resort();
        newEvent = new TableModelEvent(this, _event.getFirstRow(), _event.getLastRow(), _event.getColumn(), _event.getType());
      }
    }
    else if (_event.getType() == TableModelEvent.UPDATE) {
      reallocateIndexes();
      resort();
      newEvent = new TableModelEvent(this, _event.getFirstRow(), _event.getLastRow(), _event.getColumn(), _event.getType());
    }
    else {
      reallocateIndexes();
      resort();
      newEvent = new TableModelEvent(this, _event.getFirstRow(), _event.getLastRow(), _event.getColumn(), _event.getType());
    }

    super.tableChanged(newEvent);
  }

  protected void resort() {
    sortByColumn(getSortingColumn(), isAscending());
  }

  public void checkModel() {
    if (indexes.length != model.getRowCount()) {
      //      System.err.println("Sorter not informed of a change in model.");
    }
  }

  public void sort(Object sender) {
    checkModel();

    compares = 0;
    //n2sort();
    //QuickSort qs = new QuickSort();
    //quicksort(0, indexes.length-1);
    if (sortingColumns.size() == 0) {
      for (int i = 0; i < indexes.length; i ++ ) {
        indexes[i] = i;
      }
    }
    else {
      shuttlesort((int[]) indexes.clone(), indexes, 0, indexes.length);
    }
    //System.out.println("Compares: "+compares);
    myRowToCollator.clear();
  }

  public void n2sort() {
    for (int i = 0; i < getRowCount(); i ++ ) {
      for (int j = i + 1; j < getRowCount(); j ++ ) {
        if (compare(indexes[i], indexes[j]) == -1) {
          swap(i, j);
        }
      }
    }
  }

  // This is a home-grown implementation which we have not had time
  // to research - it may perform poorly in some circumstances. It
  // requires twice the space of an in-place algorithm and makes
  // NlogN assigments shuttling the values between the two
  // arrays. The number of compares appears to vary between N-1 and
  // NlogN depending on the initial order but the main reason for
  // using it here is that, unlike qsort, it is stable.
  public void shuttlesort(int from[], int to[], int low, int high) {
    if (high - low < 2) {
      return;
    }
    int middle = (low + high) / 2;
    shuttlesort(to, from, low, middle);
    shuttlesort(to, from, middle, high);

    int p = low;
    int q = middle;

    /* This is an optional short-cut; at each recursive call,
     check to see if the elements in this subset are already
     ordered.  If so, no further comparisons are needed; the
     sub-array can just be copied.  The array must be copied rather
     than assigned otherwise sister calls in the recursion might
     get out of sinc.  When the number of elements is three they
     are partitioned so that the first set, [low, mid), has one
     element and and the second, [mid, high), has two. We skip the
     optimisation when the number of elements is three or less as
     the first compare in the normal merge will produce the same
     sequence of steps. This optimisation seems to be worthwhile
     for partially ordered lists but some analysis is needed to
     find out how the performance drops to Nlog(N) as the initial
     order diminishes - it may drop very quickly.  */

    if (high - low >= 4 && compare(from[middle - 1], from[middle]) <= 0) {
      for (int i = low; i < high; i ++ ) {
        to[i] = from[i];
      }
      return;
    }

    // A normal merge. 

    for (int i = low; i < high; i ++ ) {
      if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) {
        to[i] = from[p ++];
      }
      else {
        to[i] = from[q ++];
      }
    }
  }

  public void swap(int i, int j) {
    int tmp = indexes[i];
    indexes[i] = indexes[j];
    indexes[j] = tmp;
  }

  // The mapping only affects the contents of the data rows.
  // Pass all requests to these rows through the mapping array: "indexes".

  public synchronized Object getValueAt(int aRow, int aColumn) {
    checkModel();
    return (model.getValueAt(indexes[aRow], aColumn));
  }

  public synchronized void setValueAt(Object aValue, int aRow, int aColumn) {
    checkModel();
    model.setValueAt(aValue, indexes[aRow], aColumn);
  }

  public void sortByColumn(int column) {
    sortByColumn(column, true);
  }

  public synchronized void sortByColumn(int _column, boolean _ascending) {
    boolean oldAscending = this.ascending;
    int oldSortColumn = getSortingColumn();

    this.ascending = _ascending;
    sortingColumns.removeAllElements();
    if (_column != -1) {
      sortingColumns.addElement(new Integer(_column));
    }
    sort(this);
    super.tableChanged(new TableModelEvent(this));
    myPropertyChangeSupport.firePropertyChange("sortDirection", new Boolean(oldAscending), new Boolean(_ascending));
    myPropertyChangeSupport.firePropertyChange("sortColumn", new Integer(oldSortColumn), new Integer(_column));
  }

  public int getSortingColumn() {
    int sortingColumn;
    if (sortingColumns.size() > 0) {
      sortingColumn = ((Integer) sortingColumns.elementAt(0)).intValue();
    }
    else {
      sortingColumn = -1;
    }
    return sortingColumn;
  }

  public boolean isAscending() {
    return ascending;
  }

  /**
   * @see org.jempeg.model.IContainer#getName()
   */
  public String getName() {
    ISortableTableModel sortableTableModel = (ISortableTableModel) getModel();
    String name = sortableTableModel.getName();
    return name;
  }

  /**
   * @see org.jempeg.empeg.model.IContainer#getSize()
   */
  public int getSize() {
    return getRowCount();
  }
}