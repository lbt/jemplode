/* PredicateUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;

public class PredicateUtils
{
    public static boolean evaluate(String _value) {
	if (_value.length() > 0 && !"0".equals(_value)
	    && !"false".equals(_value) && !"no".equals(_value)
	    && !"off".equals(_value))
	    return true;
	return false;
    }
    
    public static boolean evaluate(IPredicate _predicate, IFIDNode _node) {
	String value = _predicate.getValue(_node);
	return evaluate(value);
    }
    
    public static long getLongValue(String _value) {
	long value;
	try {
	    value = Long.parseLong(_value);
	} catch (NumberFormatException e) {
	    value = (long) (evaluate(_value) ? 1 : 0);
	}
	return value;
    }
}
