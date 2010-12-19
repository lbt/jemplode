package com.inzyme.container;

import org.jempeg.nodestore.model.NodeTag;

import com.inzyme.text.CollationKeyCache;

public class CollationKeySortableContainer implements ISortableContainer {
  private CollationKeyCache myCache;
  private ISortableContainer myProxiedContainer;

  public CollationKeySortableContainer(CollationKeyCache _cache, ISortableContainer _proxiedContainer) {
    myCache = _cache;
    myProxiedContainer = _proxiedContainer;
  }

  public Object getSortValueAt(NodeTag _sortOnNodeTag, int _index) {
    Object obj = myProxiedContainer.getSortValueAt(_sortOnNodeTag, _index);
    if (_sortOnNodeTag != null && obj instanceof String) {
      obj = _sortOnNodeTag.toValue((String) obj);
    }
    if (obj instanceof String) {
      obj = myCache.getCollationKey((String) obj);
    }
    return obj;
  }

  public Object getSortValue(NodeTag _sortOnNodeTag, Object _value) {
    Object obj = myProxiedContainer.getSortValue(_sortOnNodeTag, _value);
    if (_sortOnNodeTag != null && obj instanceof String) {
      obj = _sortOnNodeTag.toValue((String) obj);
    }
    if (obj instanceof String) {
      obj = myCache.getCollationKey((String) obj);
    }
    return obj;
  }

  public String getName() {
    return myProxiedContainer.getName();
  }

  public int getSize() {
    return myProxiedContainer.getSize();
  }

  public Object getValueAt(int _index) {
    Object obj = myProxiedContainer.getValueAt(_index);
    if (obj instanceof String) {
      obj = myCache.getCollationKey((String) obj);
    }
    return obj;
  }
}