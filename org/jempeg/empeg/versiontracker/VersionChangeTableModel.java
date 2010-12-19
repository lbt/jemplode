package org.jempeg.empeg.versiontracker;

import javax.swing.table.AbstractTableModel;

public class VersionChangeTableModel extends AbstractTableModel {
  private VersionChange[] myChanges;

  public VersionChangeTableModel(VersionChange[] _changes) {
    myChanges = _changes;
  }

  public Class getColumnClass(int _columnIndex) {
    return String.class;
  }

  public String getColumnName(int _columnIndex) {
    String name;
    switch (_columnIndex) {
      case 0: name = "Version"; break;
      case 1: name = "Feature"; break;
      default: name = ""; break;
    }
    return name;
  }

  public int getColumnCount() {
    return 2;
  }

  public int getRowCount() {
    return myChanges.length;
  }

  public Object getValueAt(int _rowIndex, int _columnIndex) {
    VersionChange change = myChanges[_rowIndex];
    String value;
    switch (_columnIndex) {
      case 0: value = change.getVersion(); break;
      case 1: value = change.getFeature(); break;
      default: value = ""; break;
    }
    return value;
  }
}

