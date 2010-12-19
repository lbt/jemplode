package org.jempeg.nodestore.predicate;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.model.TagValueRetriever;

public class TagValue implements IPredicate {
	private String myTagName;
	private NodeTag myTag;
	
	public TagValue(String _tagName) {
		myTagName = _tagName;
	}
	
	protected String getTagName() {
		return myTagName;
	}

	public boolean evaluate(IFIDNode _node) {
		return PredicateUtils.evaluate(this, _node);
	}
	
	public String getValue(IFIDNode _node) {
		return TagValueRetriever.getValue(_node, myTagName);
	}

	public boolean isDependentOn(NodeTag _tag) {
		if (myTag == null) {
			myTag = NodeTag.getNodeTag(myTagName);
		}
		return myTag.isDerivedFrom(_tag);
	}
	
	public String toString() {
		return myTagName;
	}
}
