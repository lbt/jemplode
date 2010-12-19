/* SortSoupLayer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import java.text.ParseException;

import com.inzyme.text.CollationKeyCache;
import com.inzyme.util.ReflectionUtils;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

public class SortSoupLayer implements ISoupLayer
{
    private int myContainedType;
    private NodeTag mySortTag;
    private CollationKeyCache mySortCache;
    
    public SortSoupLayer(NodeTag _sortTag) {
	this(3, _sortTag);
    }
    
    public SortSoupLayer(int _containedType, String _sortTag)
	throws ParseException {
	this(_containedType, NodeTag.getNodeTag(_sortTag));
    }
    
    public SortSoupLayer(int _containedType, NodeTag _sortTag) {
	myContainedType = _containedType;
	mySortTag = _sortTag;
	mySortCache = CollationKeyCache.createDefaultCache();
    }
    
    public String toExternalForm() {
	return "";
    }
    
    public boolean isDependentOn(NodeTag _tag) {
	return mySortTag.equals(_tag);
    }
    
    public int getType() {
	return myContainedType;
    }
    
    public NodeTag getSortTag() {
	return mySortTag;
    }
    
    public CollationKeyCache getSortCache() {
	return mySortCache;
    }
    
    public boolean qualifies(IFIDNode _node) {
	boolean qualifies = (_node.isIdentified() && !_node.isTransient()
			     && !_node.isMarkedForDeletion());
	return qualifies;
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
