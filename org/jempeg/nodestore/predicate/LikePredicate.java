/* LikePredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;

public class LikePredicate extends AbstractPredicate
{
    public LikePredicate(IPredicate _leftValue, IPredicate _rightValue) {
	super(_leftValue, _rightValue);
    }
    
    public String getValue(IFIDNode _node) {
	return String.valueOf(evaluate(_node));
    }
    
    public boolean evaluate(IFIDNode _node) {
	String leftValue = getLeftValue(_node);
	String rightValue = getRightValue(_node);
	boolean like;
	if (leftValue != null && rightValue != null) {
	    leftValue = leftValue.toLowerCase();
	    rightValue = rightValue.toLowerCase();
	    like = leftValue.indexOf(rightValue) > -1;
	} else
	    like = false;
	return like;
    }
    
    protected String operandToString() {
	return "like";
    }
}
