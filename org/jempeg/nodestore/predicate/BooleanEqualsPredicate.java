/* BooleanEqualsPredicate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.model.TagValueRetriever;

public class BooleanEqualsPredicate implements IPredicate
{
    private String myTagName;
    private NodeTag myNodeTag;
    
    public BooleanEqualsPredicate(String _tagName) {
	myTagName = _tagName;
    }
    
    public String getValue(IFIDNode _node) {
	return String.valueOf(evaluate(_node));
    }
    
    public boolean evaluate(IFIDNode _node) {
	String value = getLeftValue(_node);
	boolean equals = false;
	if (value != null && !value.equalsIgnoreCase("no")
	    && !value.equalsIgnoreCase("false") && !value.equalsIgnoreCase(""))
	    equals = true;
	return equals;
    }
    
    public boolean isDependentOn(NodeTag _tag) {
	if (myNodeTag != null) {
	    BooleanEqualsPredicate booleanequalspredicate;
	    MONITORENTER (booleanequalspredicate = this);
	    MISSING MONITORENTER
	    synchronized (booleanequalspredicate) {
		myNodeTag = NodeTag.getNodeTag(myTagName);
	    }
	}
	return myNodeTag.isDerivedFrom(_tag);
    }
    
    protected String getLeftValue(IFIDNode _node) {
	return TagValueRetriever.getValue(_node, myTagName);
    }
    
    protected String getTagName() {
	return myTagName;
    }
    
    public String toString() {
	return "(" + getTagName() + ")";
    }
}
