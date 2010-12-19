/* LongHashtableEntry - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.io.Serializable;

class LongHashtableEntry implements Serializable
{
    int hash;
    long key;
    IFIDNode value;
    LongHashtableEntry next;
    
    protected Object clone() {
	LongHashtableEntry entry = new LongHashtableEntry();
	entry.hash = hash;
	entry.key = key;
	entry.value = value;
	entry.next = next != null ? (LongHashtableEntry) next.clone() : null;
	return entry;
    }
}
