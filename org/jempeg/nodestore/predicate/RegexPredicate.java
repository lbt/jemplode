/* RegexPredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jempeg.nodestore.IFIDNode;

public class RegexPredicate extends AbstractPredicate
{
    public RegexPredicate(IPredicate _leftValue, IPredicate _rightValue) {
	super(_leftValue, _rightValue);
    }
    
    public String getValue(IFIDNode _node) {
	return String.valueOf(evaluate(_node));
    }
    
    public boolean evaluate(IFIDNode _node) {
	String leftValue = getLeftValue(_node);
	String rightValue = getRightValue(_node);
	boolean matches;
	if (leftValue != null && rightValue != null) {
	    Pattern pattern = Pattern.compile(rightValue);
	    Matcher m = pattern.matcher(leftValue);
	    matches = m.find();
	} else
	    matches = false;
	return matches;
    }
    
    protected String operandToString() {
	return "regex";
    }
}
