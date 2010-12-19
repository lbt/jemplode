/* IPredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

public interface IPredicate
{
    public String getValue(IFIDNode ifidnode);
    
    public boolean evaluate(IFIDNode ifidnode);
    
    public boolean isDependentOn(NodeTag nodetag);
}
