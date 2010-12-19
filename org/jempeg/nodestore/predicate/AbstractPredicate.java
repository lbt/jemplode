/* AbstractPredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

public abstract class AbstractPredicate implements IPredicate
{
    private IPredicate myLeftValue;
    private IPredicate myRightValue;
    
    public AbstractPredicate(IPredicate _leftValue, IPredicate _rightValue) {
	myLeftValue = _leftValue;
	myRightValue = _rightValue;
    }
    
    public boolean isDependentOn(NodeTag _tag) {
	if (!myLeftValue.isDependentOn(_tag)
	    && !myRightValue.isDependentOn(_tag))
	    return false;
	return true;
    }
    
    protected String getLeftValue(IFIDNode _node) {
	return myLeftValue.getValue(_node);
    }
    
    protected String getRightValue(IFIDNode _node) {
	return myRightValue.getValue(_node);
    }
    
    protected abstract String operandToString();
    
    public String toString() {
	return ("(" + myLeftValue + " " + operandToString() + " "
		+ myRightValue + ")");
    }
}
