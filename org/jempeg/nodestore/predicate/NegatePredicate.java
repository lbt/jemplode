/* NegatePredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

public class NegatePredicate implements IPredicate
{
    private IPredicate myPredicate;
    
    public NegatePredicate(IPredicate _predicate) {
	myPredicate = _predicate;
    }
    
    public String getValue(IFIDNode _node) {
	String value = myPredicate.getValue(_node);
	try {
	    long numericValue = -1L * Long.parseLong(value);
	    value = String.valueOf(numericValue);
	} catch (Throwable t) {
	    value = "";
	}
	return value;
    }
    
    public boolean evaluate(IFIDNode _node) {
	return PredicateUtils.evaluate(this, _node);
    }
    
    public boolean isDependentOn(NodeTag _tag) {
	return myPredicate.isDependentOn(_tag);
    }
    
    public String toString() {
	return "-" + myPredicate;
    }
}
