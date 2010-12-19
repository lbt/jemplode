/* ISortableContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.container;
import org.jempeg.nodestore.model.NodeTag;

public interface ISortableContainer extends IContainer
{
    public Object getSortValueAt(NodeTag nodetag, int i);
    
    public Object getSortValue(NodeTag nodetag, Object object);
}
