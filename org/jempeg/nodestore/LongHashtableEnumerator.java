/* LongHashtableEnumerator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.util.Enumeration;
import java.util.NoSuchElementException;

class LongHashtableEnumerator implements Enumeration
{
    boolean keys;
    int index;
    LongHashtableEntry[] table;
    LongHashtableEntry entry;
    
    LongHashtableEnumerator(LongHashtableEntry[] table, boolean keys) {
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
	    LongHashtableEntry e = entry;
	    entry = e.next;
	    return keys ? (Object) new Long(e.key) : e.value;
	}
	throw new NoSuchElementException("LongHashtableEnumerator");
    }
}
