/* PeekableEnumeration - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.predicate;
import java.util.Enumeration;
import java.util.Stack;

public class PeekableEnumeration implements Enumeration
{
    private Enumeration myEnumeration;
    private Stack myPeekStack;
    
    public PeekableEnumeration(Enumeration _enumeration) {
	myEnumeration = _enumeration;
	myPeekStack = new Stack();
    }
    
    public boolean hasMoreElements() {
	if (myPeekStack.size() <= 0 && !myEnumeration.hasMoreElements())
	    return false;
	return true;
    }
    
    public Object nextElement() {
	Object element;
	if (myPeekStack.size() > 0)
	    element = myPeekStack.pop();
	else
	    element = myEnumeration.nextElement();
	return element;
    }
    
    public Object peek() {
	Object peekValue;
	if (myPeekStack.size() == 0) {
	    if (hasMoreElements()) {
		peekValue = nextElement();
		myPeekStack.push(peekValue);
	    } else
		peekValue = null;
	} else
	    peekValue = myPeekStack.peek();
	return peekValue;
    }
    
    public void push(Object _peekValue) {
	myPeekStack.push(_peekValue);
    }
}
