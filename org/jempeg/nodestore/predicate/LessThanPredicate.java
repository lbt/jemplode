/* LessThanPredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;

public class LessThanPredicate extends AbstractNumericPredicate
{
    public LessThanPredicate(IPredicate _leftValue, IPredicate _rightValue) {
	super(_leftValue, _rightValue);
    }
    
    public String getValue(IFIDNode _node) {
	return String.valueOf(evaluate(_node));
    }
    
    public boolean evaluate(IFIDNode _node) {
	boolean lessThan
	    = getLeftNumericValue(_node) < getRightNumericValue(_node);
	return lessThan;
    }
    
    protected String operandToString() {
	return "<";
    }
}
