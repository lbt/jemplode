/* TruePredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;

public class TruePredicate implements IPredicate
{
    public String getValue(IFIDNode _node) {
	return String.valueOf(evaluate(_node));
    }
    
    public boolean evaluate(IFIDNode _node) {
	return true;
    }
    
    public boolean isDependentOn(NodeTag _tag) {
	return false;
    }
    
    public String toString() {
	return "";
    }
}
