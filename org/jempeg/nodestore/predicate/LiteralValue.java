package org.jempeg.nodestore.predicate;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

public class LiteralValue implements IPredicate {
	private String myValue;
	private boolean myNumeric;
	
	public LiteralValue(String _value) {
		this(_value, false);
	}
	
	public LiteralValue(String _value, boolean _numeric) {
		myValue = _value;
		myNumeric = _numeric;
	}
	
	public String getValue(IFIDNode _node) {
		return myValue;
	}
	
	public boolean evaluate(IFIDNode _node) {
		return true;
	}

	public boolean isDependentOn(NodeTag _tag) {
		return false;
	}

	private String escape(String _value) {
		int length = _value.length();
		StringBuffer escapedValue = new StringBuffer(length);

		for (int i = 0; i < length; i++) {
			char ch = _value.charAt(i);
			switch (ch) {
			case '"' :
				escapedValue.append("\\");
			default :
				escapedValue.append(ch);
				break;
			}
		}

		return escapedValue.toString();
	}
	
	public String toString() {
		String value = (myValue == null) ? "" : myValue;
		if (!myNumeric) {
			value = "\"" + escape(value) + "\"";
		}
		return value;
	}
}
