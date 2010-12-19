/* AllowAllTraversalFilter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.tree;
import com.inzyme.container.IContainer;

public class AllowAllTraversalFilter implements ITraversalFilter
{
    public boolean qualifies(IContainer _parentContainer,
			     IContainer _childContainer, int _depth) {
	return true;
    }
}
