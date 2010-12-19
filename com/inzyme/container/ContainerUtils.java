package com.inzyme.container;

import java.text.Collator;
import java.util.Vector;

import org.jempeg.JEmplodeProperties;
import org.jempeg.nodestore.model.NodeTag;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.text.IgnoreArticlesCollator;
import com.inzyme.text.InsensitiveCollator;
import com.inzyme.tree.ITraversalFilter;

/**
 * ContainerUtils is a set of handy methods for manipulating/navigating
 * IContainers.
 * 
 * @author Mike Schrag
 */
public class ContainerUtils {
  private static Collator COLLATOR;

  static {
    Collator baseCollator = Collator.getInstance();
    if (PropertiesManager.getInstance().getBooleanProperty(JEmplodeProperties.STRIP_ARTICLES_WHEN_COMPARING_KEY)) {
      ContainerUtils.COLLATOR = new IgnoreArticlesCollator(baseCollator);
    }
    else {
      ContainerUtils.COLLATOR = new InsensitiveCollator(baseCollator);
    }
  }

  public static Collator getDefaultCollator() {
    return ContainerUtils.COLLATOR;
  }

  /**
   * Traverses the given element and adds its children (if it's an
   * instanceof IContainer) and itself to the given Vector.  The 
   * elements in the vector will end up depth-first.
   * 
   * @param _element the element to traverse
   * @param _traversalVec the vector to store elements in
   * @param _resultsFilter the filter that determines what ends up being returned in the list of results
   * @param _traversalFilter the filter that determines
   */
  public static void traverse(Object _element, Vector _traversalVec, IFilter _resultsFilter, ITraversalFilter _traversalFilter) {
    traverse(_element, _traversalVec, _resultsFilter, _traversalFilter, 0);
  }

  /**
   * Traverses the given element and adds its children (if it's an
   * instanceof IContainer) and itself to the given Vector.  The 
   * elements in the vector will end up depth-first.
   * 
   * @param _element the element to traverse
   * @param _traversalVec the vector to store elements in
   * @param _resultsFilter the filter that determines what ends up being returned in the list of results
   * @param _traversalFilter the filter that determines
   * @param _depth the current depth 
   */
  public static void traverse(Object _element, Vector _traversalVec, IFilter _resultsFilter, ITraversalFilter _traversalFilter, int _depth) {
    if (_element instanceof IContainer) {
      IContainer container = (IContainer) _element;
      if (_traversalFilter.qualifies(null, container, _depth)) {
        int size = container.getSize();
        for (int i = 0; i < size; i ++ ) {
          Object element = container.getValueAt(i);
          traverse(element, _traversalVec, _resultsFilter, _traversalFilter, _depth + 1);
        }
      }
    }

    if (_resultsFilter.qualifies(_element)) {
      _traversalVec.addElement(_element);
    }
  }

  /**
   * Returns the index into the children array that
   * will yield a sorted list when the given value
   * is inserted.
   *
   * @param _list the list
   * @param _value the toString value of the child node
   * @param _cache the CollationKeyCache to use
   * @returns the sorted index into the parent array
   */
  public static int getSortedIndex(ISortableContainer _list, NodeTag _sortOnNodeTag, Object _value, CollationKeyCache _cache) {
    CollationKeySortableContainer cacheContainer = new CollationKeySortableContainer(_cache, _list);
    Comparable key1 = (Comparable) cacheContainer.getSortValue(_sortOnNodeTag, _value);

    int low = 0;
    int high = _list.getSize() - 1;
    int insertIndex = 0;
    while (low <= high) {
      int mid = (low + high) / 2;
      Comparable key2 = (Comparable) cacheContainer.getSortValueAt(_sortOnNodeTag, mid);
      int c = key1.compareTo(key2);
      if (c < 0) {
        high = mid - 1;
        insertIndex = mid;
      }
      else if (c > 0) {
        low = mid + 1;
        insertIndex = low;
      }
      else {
        return mid;
      }
    }
    return insertIndex;
  }
}