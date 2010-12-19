/* IntHashtableEnumerator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package Acme;
import java.util.Enumeration;
import java.util.NoSuchElementException;

class IntHashtableEnumerator implements Enumeration
{
    boolean keys;
    int index;
    IntHashtableEntry[] table;
    IntHashtableEntry entry;
    
    IntHashtableEnumerator(IntHashtableEntry[] table, boolean keys) {
	this.table = table;
	this.keys = keys;
	index = table.length;
    }
    
    public boolean hasMoreElements() {
	if (entry != null)
	    return true;
	while (index-- > 0) {
	    if ((entry = table[index]) != null)
		return true;
	}
	return false;
    }
    
    public Object nextElement() {
	if (entry == null) {
	    while (index-- > 0 && (entry = table[index]) == null) {
		/* empty */
	    }
	}
	if (entry != null) {
	    IntHashtableEntry e = entry;
	    entry = e.next;
	    return keys ? (Object) new Integer(e.key) : e.value;
	}
	throw new NoSuchElementException("IntHashtableEnumerator");
    }
}
