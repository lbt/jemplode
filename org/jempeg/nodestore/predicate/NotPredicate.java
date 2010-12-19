/* NotPredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

public class NotPredicate implements IPredicate
{
    private IPredicate myPredicate;
    
    public NotPredicate(IPredicate _predicate) {
	myPredicate = _predicate;
    }
    
    public String getValue(IFIDNode _node) {
	return String.valueOf(evaluate(_node));
    }
    
    public boolean evaluate(IFIDNode _node) {
	boolean not = !myPredicate.evaluate(_node);
	return not;
    }
    
    public boolean isDependentOn(NodeTag _tag) {
	return myPredicate.isDependentOn(_tag);
    }
    
    public String toString() {
	return "(not " + myPredicate + ")";
    }
}
