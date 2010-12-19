/* FIDNodeHashtable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.io.Serializable;
import java.util.Enumeration;

public class FIDNodeHashtable implements Cloneable, Serializable
{
    private LongHashtableEntry[] table;
    private int count;
    private int threshold;
    private float loadFactor;
    
    public FIDNodeHashtable(int initialCapacity, float loadFactor) {
	if (initialCapacity <= 0 || (double) loadFactor <= 0.0)
	    throw new IllegalArgumentException();
	this.loadFactor = loadFactor;
	table = new LongHashtableEntry[initialCapacity];
	threshold = (int) ((float) initialCapacity * loadFactor);
    }
    
    public FIDNodeHashtable(int initialCapacity) {
	this(initialCapacity, 0.75F);
    }
    
    public FIDNodeHashtable() {
	this(101, 0.75F);
    }
    
    public int size() {
	return count;
    }
    
    public boolean isEmpty() {
	if (count == 0)
	    return true;
	return false;
    }
    
    public synchronized Enumeration keys() {
	return new LongHashtableEnumerator(table, true);
    }
    
    public synchronized Enumeration elements() {
	return new LongHashtableEnumerator(table, false);
    }
    
    public synchronized boolean contains(IFIDNode value) {
	if (value == null)
	    throw new NullPointerException();
	LongHashtableEntry[] tab = table;
	int i = tab.length;
	while (i-- > 0) {
	    for (LongHashtableEntry e = tab[i]; e != null; e = e.next) {
		if (e.value.equals(value))
		    return true;
	    }
	}
	return false;
    }
    
    private int hashCode(long _key) {
	return (int) _key;
    }
    
    public synchronized boolean containsKey(long key) {
	LongHashtableEntry[] tab = table;
	int hash = hashCode(key);
	int index = (hash & 0x7fffffff) % tab.length;
	for (LongHashtableEntry e = tab[index]; e != null; e = e.next) {
	    if (e.hash == hash && e.key == key)
		return true;
	}
	return false;
    }
    
    public synchronized IFIDNode get(long key) {
	LongHashtableEntry[] tab = table;
	int hash = hashCode(key);
	int index = (hash & 0x7fffffff) % tab.length;
	for (LongHashtableEntry e = tab[index]; e != null; e = e.next) {
	    if (e.hash == hash && e.key == key)
		return e.value;
	}
	return null;
    }
    
    public IFIDNode get(Object okey) {
	if (!(okey instanceof Long))
	    throw new InternalError("key is not a Long");
	Long ikey = (Long) okey;
	long key = ikey.longValue();
	return get(key);
    }
    
    protected void rehash() {
	int oldCapacity = table.length;
	LongHashtableEntry[] oldTable = table;
	int newCapacity = oldCapacity * 2 + 1;
	LongHashtableEntry[] newTable = new LongHashtableEntry[newCapacity];
	threshold = (int) ((float) newCapacity * loadFactor);
	table = newTable;
	int i = oldCapacity;
	while (i-- > 0) {
	    LongHashtableEntry old = oldTable[i];
	    while (old != null) {
		LongHashtableEntry e = old;
		old = old.next;
		int index = (e.hash & 0x7fffffff) % newCapacity;
		e.next = newTable[index];
		newTable[index] = e;
	    }
	}
    }
    
    public synchronized IFIDNode put(long key, IFIDNode value) {
	if (value == null)
	    throw new NullPointerException();
	LongHashtableEntry[] tab = table;
	int hash = hashCode(key);
	int index = (hash & 0x7fffffff) % tab.length;
	for (LongHashtableEntry e = tab[index]; e != null; e = e.next) {
	    if (e.hash == hash && e.key == key) {
		IFIDNode old = e.value;
		e.value = value;
		return old;
	    }
	}
	if (count >= threshold) {
	    rehash();
	    return put(key, value);
	}
	LongHashtableEntry e = new LongHashtableEntry();
	e.hash = hash;
	e.key = key;
	e.value = value;
	e.next = tab[index];
	tab[index] = e;
	count++;
	return null;
    }
    
    public Object put(Object okey, IFIDNode value) {
	if (!(okey instanceof Long))
	    throw new InternalError("key is not a Long");
	Long ikey = (Long) okey;
	long key = ikey.longValue();
	return put(key, value);
    }
    
    public synchronized IFIDNode remove(long key) {
	LongHashtableEntry[] tab = table;
	int hash = hashCode(key);
	int index = (hash & 0x7fffffff) % tab.length;
	LongHashtableEntry e = tab[index];
	LongHashtableEntry prev = null;
	for (/**/; e != null; e = e.next) {
	    if (e.hash == hash && e.key == key) {
		if (prev != null)
		    prev.next = e.next;
		else
		    tab[index] = e.next;
		count--;
		return e.value;
	    }
	    prev = e;
	}
	return null;
    }
    
    public IFIDNode remove(Object okey) {
	if (!(okey instanceof Long))
	    throw new InternalError("key is not a Long");
	Long ikey = (Long) okey;
	long key = ikey.longValue();
	return remove(key);
    }
    
    public synchronized void clear() {
	LongHashtableEntry[] tab = table;
	int index = tab.length;
	while (--index >= 0)
	    tab[index] = null;
	count = 0;
    }
    
    public synchronized Object clone() {
	try {
	    FIDNodeHashtable t = (FIDNodeHashtable) super.clone();
	    t.table = new LongHashtableEntry[table.length];
	    int i = table.length;
	    while (i-- > 0)
		t.table[i] = (table[i] != null
			      ? (LongHashtableEntry) table[i].clone() : null);
	    return t;
	} catch (CloneNotSupportedException e) {
	    throw new InternalError();
	}
    }
    
    public synchronized String toString() {
	int max = size() - 1;
	StringBuffer buf = new StringBuffer();
	Enumeration k = keys();
	Enumeration e = elements();
	buf.append("{");
	for (int i = 0; i <= max; i++) {
	    String s1 = k.nextElement().toString();
	    String s2 = e.nextElement().toString();
	    buf.append(s1 + "=" + s2);
	    if (i < max)
		buf.append(", ");
	}
	buf.append("}");
	return buf.toString();
    }
}
