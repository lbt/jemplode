/* IntHashtable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package Acme;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.Enumeration;

public class IntHashtable extends Dictionary implements Cloneable, Serializable
{
    private IntHashtableEntry[] table;
    private int count;
    private int threshold;
    private float loadFactor;
    
    public IntHashtable(int initialCapacity, float loadFactor) {
	if (initialCapacity <= 0 || (double) loadFactor <= 0.0)
	    throw new IllegalArgumentException();
	this.loadFactor = loadFactor;
	table = new IntHashtableEntry[initialCapacity];
	threshold = (int) ((float) initialCapacity * loadFactor);
    }
    
    public IntHashtable(int initialCapacity) {
	this(initialCapacity, 0.75F);
    }
    
    public IntHashtable() {
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
	return new IntHashtableEnumerator(table, true);
    }
    
    public synchronized Enumeration elements() {
	return new IntHashtableEnumerator(table, false);
    }
    
    public synchronized boolean contains(Object value) {
	if (value == null)
	    throw new NullPointerException();
	IntHashtableEntry[] tab = table;
	int i = tab.length;
	while (i-- > 0) {
	    for (IntHashtableEntry e = tab[i]; e != null; e = e.next) {
		if (e.value.equals(value))
		    return true;
	    }
	}
	return false;
    }
    
    public synchronized boolean containsKey(int key) {
	IntHashtableEntry[] tab = table;
	int hash = key;
	int index = (hash & 0x7fffffff) % tab.length;
	for (IntHashtableEntry e = tab[index]; e != null; e = e.next) {
	    if (e.hash == hash && e.key == key)
		return true;
	}
	return false;
    }
    
    public synchronized Object get(int key) {
	IntHashtableEntry[] tab = table;
	int hash = key;
	int index = (hash & 0x7fffffff) % tab.length;
	for (IntHashtableEntry e = tab[index]; e != null; e = e.next) {
	    if (e.hash == hash && e.key == key)
		return e.value;
	}
	return null;
    }
    
    public Object get(Object okey) {
	if (!(okey instanceof Integer))
	    throw new InternalError("key is not an Integer");
	Integer ikey = (Integer) okey;
	int key = ikey.intValue();
	return get(key);
    }
    
    protected void rehash() {
	int oldCapacity = table.length;
	IntHashtableEntry[] oldTable = table;
	int newCapacity = oldCapacity * 2 + 1;
	IntHashtableEntry[] newTable = new IntHashtableEntry[newCapacity];
	threshold = (int) ((float) newCapacity * loadFactor);
	table = newTable;
	int i = oldCapacity;
	while (i-- > 0) {
	    IntHashtableEntry old = oldTable[i];
	    while (old != null) {
		IntHashtableEntry e = old;
		old = old.next;
		int index = (e.hash & 0x7fffffff) % newCapacity;
		e.next = newTable[index];
		newTable[index] = e;
	    }
	}
    }
    
    public synchronized Object put(int key, Object value) {
	if (value == null)
	    throw new NullPointerException();
	IntHashtableEntry[] tab = table;
	int hash = key;
	int index = (hash & 0x7fffffff) % tab.length;
	for (IntHashtableEntry e = tab[index]; e != null; e = e.next) {
	    if (e.hash == hash && e.key == key) {
		Object old = e.value;
		e.value = value;
		return old;
	    }
	}
	if (count >= threshold) {
	    rehash();
	    return put(key, value);
	}
	IntHashtableEntry e = new IntHashtableEntry();
	e.hash = hash;
	e.key = key;
	e.value = value;
	e.next = tab[index];
	tab[index] = e;
	count++;
	return null;
    }
    
    public Object put(Object okey, Object value) {
	if (!(okey instanceof Integer))
	    throw new InternalError("key is not an Integer");
	Integer ikey = (Integer) okey;
	int key = ikey.intValue();
	return put(key, value);
    }
    
    public synchronized Object remove(int key) {
	IntHashtableEntry[] tab = table;
	int hash = key;
	int index = (hash & 0x7fffffff) % tab.length;
	IntHashtableEntry e = tab[index];
	IntHashtableEntry prev = null;
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
    
    public Object remove(Object okey) {
	if (!(okey instanceof Integer))
	    throw new InternalError("key is not an Integer");
	Integer ikey = (Integer) okey;
	int key = ikey.intValue();
	return remove(key);
    }
    
    public synchronized void clear() {
	IntHashtableEntry[] tab = table;
	int index = tab.length;
	while (--index >= 0)
	    tab[index] = null;
	count = 0;
    }
    
    public synchronized Object clone() {
	try {
	    IntHashtable t = (IntHashtable) super.clone();
	    t.table = new IntHashtableEntry[table.length];
	    int i = table.length;
	    while (i-- > 0)
		t.table[i] = (table[i] != null
			      ? (IntHashtableEntry) table[i].clone() : null);
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
