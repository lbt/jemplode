package org.jempeg.nodestore.predicate;

import org.jempeg.nodestore.IFIDNode;

public class PredicateUtils {
	public static boolean evaluate(String _value) {
		return _value.length() > 0 && !"0".equals(_value) && !"false".equals(_value) && !"no".equals(_value) && !"off".equals(_value);
	}
	
	public static boolean evaluate(IPredicate _predicate, IFIDNode _node) {
		String value = _predicate.getValue(_node);
		return PredicateUtils.evaluate(value);
	}
	
	public static long getLongValue(String _value) {
		long value;
		try {
			value = Long.parseLong(_value);
		}
		catch (NumberFormatException e) {
			value = (PredicateUtils.evaluate(_value)) ? 1 : 0;
		}
		return value;
	}
}
