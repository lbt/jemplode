package com.inzyme.table;

import javax.swing.table.TableModel;

import org.jempeg.nodestore.model.NodeTag;

import com.inzyme.container.ISortableContainer;

/**
 * TableColumnContainer is an implementation of the Container
 * interface on top of a single column of a table.
 * 
 * @author Mike Schrag
 */
public class TableColumnContainer implements ISortableContainer {
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