package org.jempeg.nodestore.soup;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

import com.inzyme.text.CollationKeyCache;

public interface ISoupLayer {
	public int getType();
	
	public NodeTag getSortTag();
	
	public CollationKeyCache getSortCache();
	
	public boolean isDependentOn(NodeTag _tag);
	
	public boolean qualifies(IFIDNode _node);

	public String toExternalForm();
}
