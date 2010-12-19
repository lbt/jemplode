package com.inzyme.table;

import javax.swing.table.AbstractTableModel;

import com.inzyme.model.Reason;


/**
 * ReasonsTableModel is a TableModel implementation that
 * is able to display a set of Reason objects.  The format
 * is a two-column display with the filename from the 
 * reason and a description of the reason.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.1 $
 */
public class ReasonsTableModel extends AbstractTableModel {
  private Reason[] myReasons;

  public ReasonsTableModel(Reason[] _reasons) {
    myReasons = _reasons;
  }

  public Class getColumnClass(int _columnIndex) {
    return String.class;
  }

  public String getColumnName(int _columnIndex) {
    String name;
    switch (_columnIndex) {
      case 0: name = "Failed/Skipped Filename"; break;
      case 1: name = "Reason"; break;
      default: name = ""; break;
    }
    return name;
  }

  public int getColumnCount() {
    return 2;
  }

  public int getRowCount() {
    return myReasons.length;
  }

  public Object getValueAt(int _rowIndex, int _columnIndex) {
    Reason reason = myReasons[_rowIndex];
    String value;
    switch (_columnIndex) {
      case 0: value = reason.getFileName(); break;
      case 1: value = reason.getReason(); break;
      default: value = ""; break;
    }
    return value;
  }
}

