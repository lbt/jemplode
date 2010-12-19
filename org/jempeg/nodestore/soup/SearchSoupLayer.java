/* SearchSoupLayer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import java.text.ParseException;

import com.inzyme.text.CollationKeyCache;
import com.inzyme.util.ReflectionUtils;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.predicate.IPredicate;
import org.jempeg.nodestore.predicate.PredicateParser;

public class SearchSoupLayer implements ISoupLayer
{
    private IPredicate myPredicate;
    private int myContainedType;
    private NodeTag mySortTag;
    private CollationKeyCache mySortCache;
    
    public SearchSoupLayer(IPredicate _predicate) {
	this(_predicate, 3, NodeTag.TITLE_TAG);
    }
    
    public SearchSoupLayer(String _predicateStr, int _containedType,
			   String _sortTag) throws ParseException {
	this(new PredicateParser().parse(_predicateStr), _containedType,
	     NodeTag.getNodeTag(_sortTag));
    }
    
    public SearchSoupLayer(IPredicate _predicate, int _containedType,
			   NodeTag _sortTag) {
	myPredicate = _predicate;
	myContainedType = _containedType;
	mySortTag = _sortTag;
	mySortCache = CollationKeyCache.createDefaultCache();
    }
    
    public String toExternalForm() {
	return myPredicate.toString();
    }
    
    public IPredicate getPredicate() {
	return myPredicate;
    }
    
    public boolean isDependentOn(NodeTag _tag) {
	if (!myPredicate.isDependentOn(_tag) && !mySortTag.equals(_tag))
	    return false;
	return true;
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
			     && !_node.isMarkedForDeletion()
			     && (!(_node instanceof FIDPlaylist)
				 || !((FIDPlaylist) _node).isSoup())
			     && myPredicate.evaluate(_node));
	return qualifies;
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
