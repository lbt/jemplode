package org.jempeg.nodestore.predicate;

import org.jempeg.nodestore.IFIDNode;

public class SecondsAgoFunction extends AbstractOneParameterFunction {
	public SecondsAgoFunction(String _functionName, IPredicate _parameter) {
		super(_functionName, _parameter);
	}
	
	protected String evaluate(IFIDNode _node, String _param) {
		long secondsago = (_param.length() > 0) ? Long.parseLong(_param) : 0;
		long time = System.currentTimeMillis() / 1000 + secondsago;
		String retval = String.valueOf(time);
		return retval;
	}
}
