package org.jempeg.nodestore.predicate;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

public abstract class AbstractOneParameterFunction implements IPredicate {
	private String myFunctionName;
	private IPredicate myParameter;
	
	public AbstractOneParameterFunction(String _functionName, IPredicate _parameter) {
		myFunctionName = _functionName;
		myParameter = _parameter;
	}
	
	public String getValue(IFIDNode _node) {
		String param = (myParameter == null) ? "" : myParameter.getValue(_node);
		return evaluate(_node, param);
	}
	
	protected abstract String evaluate(IFIDNode _node, String _param);

	public boolean evaluate(IFIDNode _node) {
		return PredicateUtils.evaluate(this, _node);
	}

	public boolean isDependentOn(NodeTag _tag) {
		return (myParameter == null) ? false : myParameter.isDependentOn(_tag);
	}
	
	public String toString() {
		return myFunctionName + "(" + ((myParameter == null) ? "" : myParameter.toString()) + ")";
	}
}
