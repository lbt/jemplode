package org.jempeg.nodestore.model;

import java.util.Vector;

import org.jempeg.nodestore.IFIDNode;

import com.inzyme.container.AbstractSortableContainer;

public class TagValueSortableContainer extends AbstractSortableContainer {
  private String myName;
  private Vector myVector;

  public TagValueSortableContainer(Vector _vector) {
    this("TagValueSortableContainer", _vector);
  }

  public TagValueSortableContainer(String _name, Vector _vector) {
    myName = _name;
    myVector = _vector;
  }

  protected Object getSortValue0(NodeTag _sortOnNodeTag, Object _value) {
    return TagValueRetriever.getValue((IFIDNode) _value, _sortOnNodeTag.getName());
  }

  public String getName() {
    return myName;
  }

  public int getSize() {
    return myVector.size();
  }

  public Object getValueAt(int _index) {
    return myVector.elementAt(_index);
  }
}