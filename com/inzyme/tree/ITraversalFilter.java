package com.inzyme.tree;

import com.inzyme.container.IContainer;

public interface ITraversalFilter {
	public boolean qualifies(IContainer _parentContainer, IContainer _childContainer, int _depth);
}
