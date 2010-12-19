package com.inzyme.tree;

import com.inzyme.container.IContainer;

public class AllowAllTraversalFilter implements ITraversalFilter {
	public AllowAllTraversalFilter() {
	}
	
	public boolean qualifies(IContainer _parentContainer, IContainer _childContainer, int _depth) {
		return true;
	}
}
