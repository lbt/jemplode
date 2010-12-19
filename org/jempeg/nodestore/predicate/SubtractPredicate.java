/* SubtractPredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;

public class SubtractPredicate extends AbstractNumericPredicate
{
    public SubtractPredicate(IPredicate _leftValue, IPredicate _rightValue) {
	super(_leftValue, _rightValue);
    }
    
    public boolean evaluate(IFIDNode _node) {
	return PredicateUtils.evaluate(this, _node);
    }
    
    public String getValue(IFIDNode _node) {
	long value = getLeftNumericValue(_node) - getRightNumericValue(_node);
	return String.valueOf(value);
    }
    
    protected String operandToString() {
	return "-";
    }
}
