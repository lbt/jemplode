/* LessThanOrEqualPredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;

public class LessThanOrEqualPredicate extends AbstractNumericPredicate
{
    public LessThanOrEqualPredicate(IPredicate _leftValue,
				    IPredicate _rightValue) {
	super(_leftValue, _rightValue);
    }
    
    public String getValue(IFIDNode _node) {
	return String.valueOf(evaluate(_node));
    }
    
    public boolean evaluate(IFIDNode _node) {
	boolean greaterThan
	    = getLeftNumericValue(_node) <= getRightNumericValue(_node);
	return greaterThan;
    }
    
    protected String operandToString() {
	return "<=";
    }
}
