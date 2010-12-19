/* LruHashtableEnumerator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package Acme;
import java.util.Enumeration;
import java.util.Hashtable;

class LruHashtableEnumerator implements Enumeration
{
    Enumeration oldEnum;
    Enumeration newEnum;
    boolean old;
    
    LruHashtableEnumerator(Hashtable oldTable, Hashtable newTable,
			   boolean keys) {
	if (keys) {
	    oldEnum = oldTable.keys();
	    newEnum = newTable.keys();
	} else {
	    oldEnum = oldTable.elements();
	    newEnum = newTable.elements();
	}
	old = true;
    }
    
    public boolean hasMoreElements() {
	boolean r;
	if (old) {
	    r = oldEnum.hasMoreElements();
	    if (!r) {
		old = false;
		r = newEnum.hasMoreElements();
	    }
	} else
	    r = newEnum.hasMoreElements();
	return r;
    }
    
    public Object nextElement() {
	if (old)
	    return oldEnum.nextElement();
	return newEnum.nextElement();
    }
}
