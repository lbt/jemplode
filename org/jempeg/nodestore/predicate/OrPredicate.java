/* OrPredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

public class OrPredicate implements IPredicate
{
    private IPredicate myLeft;
    private IPredicate myRight;
    
    public OrPredicate(IPredicate _left, IPredicate _right) {
	myLeft = _left;
	myRight = _right;
    }
    
    public String getValue(IFIDNode _node) {
	return String.valueOf(evaluate(_node));
    }
    
    public boolean evaluate(IFIDNode _node) {
	boolean results = myLeft.evaluate(_node) || myRight.evaluate(_node);
	return results;
    }
    
    public boolean isDependentOn(NodeTag _tag) {
	if (!myLeft.isDependentOn(_tag) && !myRight.isDependentOn(_tag))
	    return false;
	return true;
    }
    
    public String toString() {
	return "(" + myLeft + " or " + myRight + ")";
    }
}
