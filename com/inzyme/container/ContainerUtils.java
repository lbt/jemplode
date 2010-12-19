/* ContainerUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.container;
import java.text.Collator;
import java.util.Vector;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.text.IgnoreArticlesCollator;
import com.inzyme.text.InsensitiveCollator;
import com.inzyme.tree.ITraversalFilter;

import org.jempeg.nodestore.model.NodeTag;

public class ContainerUtils
{
    private static Collator COLLATOR;
    
    static {
	Collator baseCollator = Collator.getInstance();
	if (PropertiesManager.getInstance()
		.getBooleanProperty("stripArticlesWhenComparing"))
	    COLLATOR = new IgnoreArticlesCollator(baseCollator);
	else
	    COLLATOR = new InsensitiveCollator(baseCollator);
    }
    
    public static Collator getDefaultCollator() {
	return COLLATOR;
    }
    
    public static void traverse(Object _element, Vector _traversalVec,
				IFilter _resultsFilter,
				ITraversalFilter _traversalFilter) {
	traverse(_element, _traversalVec, _resultsFilter, _traversalFilter, 0);
    }
    
    public static void traverse
	(Object _element, Vector _traversalVec, IFilter _resultsFilter,
	 ITraversalFilter _traversalFilter, int _depth) {
	if (_element instanceof IContainer) {
	    IContainer container = (IContainer) _element;
	    if (_traversalFilter.qualifies(null, container, _depth)) {
		int size = container.getSize();
		for (int i = 0; i < size; i++) {
		    Object element = container.getValueAt(i);
		    traverse(element, _traversalVec, _resultsFilter,
			     _traversalFilter, _depth + 1);
		}
	    }
	}
	if (_resultsFilter.qualifies(_element))
	    _traversalVec.addElement(_element);
    }
    
    public static int getSortedIndex(ISortableContainer _list,
				     NodeTag _sortOnNodeTag, Object _value,
				     CollationKeyCache _cache) {
	CollationKeySortableContainer cacheContainer
	    = new CollationKeySortableContainer(_cache, _list);
	Comparable key1
	    = (Comparable) cacheContainer.getSortValue(_sortOnNodeTag, _value);
	int low = 0;
	int high = _list.getSize() - 1;
	int insertIndex = 0;
	while (low <= high) {
	    int mid = (low + high) / 2;
	    Comparable key2
		= ((Comparable)
		   cacheContainer.getSortValueAt(_sortOnNodeTag, mid));
	    int c = key1.compareTo(key2);
	    if (c < 0) {
		high = mid - 1;
		insertIndex = mid;
	    } else if (c > 0) {
		low = mid + 1;
		insertIndex = low;
	    } else
		return mid;
	}
	return insertIndex;
    }
}
