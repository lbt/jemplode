/* AbstractNumericPredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;

public abstract class AbstractNumericPredicate extends AbstractPredicate
{
    public AbstractNumericPredicate(IPredicate _leftValue,
				    IPredicate _rightValue) {
	super(_leftValue, _rightValue);
    }
    
    protected long getLeftNumericValue(IFIDNode _node) {
	return PredicateUtils.getLongValue(getLeftValue(_node));
    }
    
    protected long getRightNumericValue(IFIDNode _node) {
	return PredicateUtils.getLongValue(getRightValue(_node));
    }
}
