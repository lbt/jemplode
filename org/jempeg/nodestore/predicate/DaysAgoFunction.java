package org.jempeg.nodestore.predicate;

import org.jempeg.nodestore.IFIDNode;

public class DaysAgoFunction extends AbstractOneParameterFunction {
	public DaysAgoFunction(String _functionName, IPredicate _parameter) {
		super(_functionName, _parameter);
	}
	
	protected String evaluate(IFIDNode _node, String _param) {
		long daysago = (_param.length() > 0) ? Long.parseLong(_param) : 0;
		long time = System.currentTimeMillis() / 1000 - daysago * 24 * 60 * 60;
		String retval = String.valueOf(time);
		return retval;
	}
}
