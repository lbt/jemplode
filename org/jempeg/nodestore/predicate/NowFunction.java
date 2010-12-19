package org.jempeg.nodestore.predicate;

import org.jempeg.nodestore.IFIDNode;

public class NowFunction extends AbstractOneParameterFunction {
	public NowFunction(String _functionName, IPredicate _parameter) {
		super(_functionName, _parameter);
	}
	
	protected String evaluate(IFIDNode _node, String _param) {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}
}
