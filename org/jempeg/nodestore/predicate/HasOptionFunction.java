package org.jempeg.nodestore.predicate;

import org.jempeg.nodestore.IFIDNode;

public class HasOptionFunction extends AbstractOneParameterFunction {
	public HasOptionFunction(String _functionName, IPredicate _parameter) {
		super(_functionName, _parameter);
	}
	
	protected String evaluate(IFIDNode _node, String _param) {
		boolean hasOption;
		try {
			int optionNum = Integer.parseInt(_param);
			hasOption = _node.hasOption(optionNum);
		}
		catch (Throwable t) {
			hasOption = false;
		}
		return String.valueOf(hasOption);
	}
}
