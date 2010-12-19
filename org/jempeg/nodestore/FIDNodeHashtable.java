// LongHashtable - a Hashtable that uses longs as the keys
//
// This is 90% based on JavaSoft's java.util.Hashtable.
//
// Visit the ACME Labs Java page for up-to-date versions of this and other
// fine Java utilities: http://www.acme.com/java/

package org.jempeg.nodestore;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;

// This is a hacked version of IntHashtable to use Long keys
// @see java.util.Hashtable

public class FIDNodeHashtable implements Cloneable, Serializable {
  /// The hash table data.
  private LongHashtableEntry table[];

  /// The total number of entries in the hash table.
  private int count;

  /// Rehashes the table when count exceeds this threshold.
  private int threshold;

  /// The load factor for the hashtable.
  private float loadFactor;

  /// Constructs a new, empty hashtable with the specified initial
  // capacity and the specified load factor.
  // @param initialCapacity the initial number of buckets
  // @param loadFactor a number between 0.0 and 1.0, it defines
  //		the threshold for rehashing the hashtable into
  //		a bigger one.
  // @exception IllegalArgumentException If the initial capacity
  // is less than or equal to zero.
  // @exception IllegalArgumentException If the load factor is
  // less than or equal to zero.
  public FIDNodeHashtable(int initialCapacity, float loadFactor) {
    if (initialCapacity <= 0 || loadFactor <= 0.0)
      throw new IllegalArgumentException();
    this.loadFactor = loadFactor;
    table = new LongHashtableEntry[initialCapacity];
    threshold = (int) (initialCapacity * loadFactor);
  }

  /// Constructs a new, empty hashtable with the specified initial
  // capacity.
  // @param initialCapacity the initial number of buckets
  public FIDNodeHashtable(int initialCapacity) {
    this(initialCapacity, 0.75f);
  }

  /// Constructs a new, empty hashtable. A default capacity and load factor
  // is used. Note that the hashtable will automatically grow when it gets
  // full.
  public FIDNodeHashtable() {
    this(101, 0.75f);
  }

  /// Returns the number of elements contained in the hashtable.
  public int size() {
    return count;
  }

  /// Returns true if the hashtable contains no elements.
  public boolean isEmpty() {
    return count == 0;
  }

  /// Returns an enumeration of the hashtable's keys.
  // @see LongHashtable#elements
  public synchronized Enumeration keys() {
    return new LongHashtableEnumerator(table, true);
  }

  /// Returns an enumeration of the elements. Use the Enumeration methods
  // on the returned object to fetch the elements sequentially.
  // @see LongHashtable#keys
  public synchronized Enumeration elements() {
    return new LongHashtableEnumerator(table, false);
  }

  /// Returns true if the specified object is an element of the hashtable.
  // This operation is more expensive than the containsKey() method.
  // @param value the value that we are looking for
  // @exception NullPointerException If the value being searched
  // for is equal to null.
  // @see LongHashtable#containsKey
  public synchronized boolean contains(IFIDNode value) {
    if (value == null)
      throw new NullPointerException();
    LongHashtableEntry tab[] = table;
    for (int i = tab.length; i -- > 0; ) {
      for (LongHashtableEntry e = tab[i]; e != null; e = e.next ) {
        if (e.value.equals(value))
          return true;
      }
    }
    return false;
  }

  private int hashCode(long _key) {
    //		return (int)(_key ^ (_key >>> 32));
    //		Are we really ever going to have 2 billion fids?
    return (int) _key;
  }

  /// Returns true if the collection contains an element for the key.
  // @param key the key that we are looking for
  // @see LongHashtable#contains
  public synchronized boolean containsKey(long key) {
    LongHashtableEntry tab[] = table;
    int hash = hashCode(key);
    int index = (hash & 0x7FFFFFFF) % tab.length;
    for (LongHashtableEntry e = tab[index]; e != null; e = e.next ) {
      if (e.hash == hash && e.key == key)
        return true;
    }
    return false;
  }

  /// Gets the object associated with the specified key in the
  // hashtable.
  // @param key the specified key
  // @returns the element for the key or null if the key
  // 		is not defined in the hash table.
  // @see LongHashtable#put
  public synchronized IFIDNode get(long key) {
    LongHashtableEntry tab[] = table;
    int hash = hashCode(key);
    int index = (hash & 0x7FFFFFFF) % tab.length;
    for (LongHashtableEntry e = tab[index]; e != null; e = e.next ) {
      if (e.hash == hash && e.key == key)
        return e.value;
    }
    return null;
  }

  /// A get method that takes an Object, for compatibility with
  // java.util.Dictionary. The Object must be a Long.
  public IFIDNode get(Object okey) {
    if (!(okey instanceof Long))
      throw new InternalError("key is not a Long");
    Long ikey = (Long) okey;
    long key = ikey.longValue();
    return get(key);
  }

  /// Rehashes the content of the table into a bigger table.
  // This method is called automatically when the hashtable's
  // size exceeds the threshold.
  protected void rehash() {
    int oldCapacity = table.length;
    LongHashtableEntry oldTable[] = table;

    int newCapacity = oldCapacity * 2 + 1;
    LongHashtableEntry newTable[] = new LongHashtableEntry[newCapacity];

    threshold = (int) (newCapacity * loadFactor);
    table = newTable;

    for (int i = oldCapacity; i -- > 0; ) {
      for (LongHashtableEntry old = oldTable[i]; old != null; ) {
        LongHashtableEntry e = old;
        old = old.next;

        int index = (e.hash & 0x7FFFFFFF) % newCapacity;
        e.next = newTable[index];
        newTable[index] = e;
      }
    }
  }

  /// Puts the specified element into the hashtable, using the specified
  // key. The element may be retrieved by doing a get() with the same key.
  // The key and the element cannot be null.
  // @param key the specified key in the hashtable
  // @param value the specified element
  // @exception NullPointerException If the value of the element
  // is equal to null.
  // @see LongHashtable#get
  // @return the old value of the key, or null if it did not have one.
  public synchronized IFIDNode put(long key, IFIDNode value) {
    // Make sure the value is not null.
    if (value == null)
      throw new NullPointerException();

    // Makes sure the key is not already in the hashtable.
    LongHashtableEntry tab[] = table;
    int hash = hashCode(key);
    int index = (hash & 0x7FFFFFFF) % tab.length;
    for (LongHashtableEntry e = tab[index]; e != null; e = e.next ) {
      if (e.hash == hash && e.key == key) {
        IFIDNode old = e.value;
        e.value = value;
        return old;
      }
    }

    if (count >= threshold) {
      // Rehash the table if the threshold is exceeded.
      rehash();
      return put(key, value);
    }

    // Creates the new entry.
    LongHashtableEntry e = new LongHashtableEntry();
    e.hash = hash;
    e.key = key;
    e.value = value;
    e.next = tab[index];
    tab[index] = e;
    ++count;
    return null;
  }

  /// A put method that takes an Object, for compatibility with
  // java.util.Dictionary. The Object must be a Long.
  public Object put(Object okey, IFIDNode value) {
    if (!(okey instanceof Long))
      throw new InternalError("key is not a Long");
    Long ikey = (Long) okey;
    long key = ikey.longValue();
    return put(key, value);
  }

  /// Removes the element corresponding to the key. Does nothing if the
  // key is not present.
  // @param key the key that needs to be removed
  // @return the value of key, or null if the key was not found.
  public synchronized IFIDNode remove(long key) {
    LongHashtableEntry tab[] = table;
    int hash = hashCode(key);
    int index = (hash & 0x7FFFFFFF) % tab.length;
    for (LongHashtableEntry e = tab[index], prev = null; e != null; prev = e, e = e.next ) {
      if (e.hash == hash && e.key == key) {
        if (prev != null)
          prev.next = e.next;
        else
          tab[index] = e.next;
        --count;
        return e.value;
      }
    }
    return null;
  }

  /// A remove method that takes an Object, for compatibility with
  // java.util.Dictionary. The Object must be a Long.
  public IFIDNode remove(Object okey) {
    if (!(okey instanceof Long))
      throw new InternalError("key is not a Long");
    Long ikey = (Long) okey;
    long key = ikey.longValue();
    return remove(key);
  }

  /// Clears the hash table so that it has no more elements in it.
  public synchronized void clear() {
    LongHashtableEntry tab[] = table;
    for (int index = tab.length; --index >= 0; )
      tab[index] = null;
    count = 0;
  }

  /// Creates a clone of the hashtable. A shallow copy is made,
  // the keys and elements themselves are NOT cloned. This is a
  // relatively expensive operation.
  public synchronized Object clone() {
    try {
      FIDNodeHashtable t = (FIDNodeHashtable) super.clone();
      t.table = new LongHashtableEntry[table.length];
      for (int i = table.length; i -- > 0; )
        t.table[i] = (table[i] != null) ? (LongHashtableEntry) table[i].clone() : null;
      return t;
    }
    catch (CloneNotSupportedException e) {
      // This shouldn't happen, since we are Cloneable.
      throw new InternalError();
    }
  }

  /// Converts to a rather lengthy String.
  public synchronized String toString() {
    int max = size() - 1;
    StringBuffer buf = new StringBuffer();
    Enumeration k = keys();
    Enumeration e = elements();
    buf.append("{");

    for (int i = 0; i <= max; ++i ) {
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

class LongHashtableEntry implements Serializable {
  int hash;
  long key;
  IFIDNode value;
  LongHashtableEntry next;

  protected Object clone() {
    LongHashtableEntry entry = new LongHashtableEntry();
    entry.hash = hash;
    entry.key = key;
    entry.value = value;
    entry.next = (next != null) ? (LongHashtableEntry) next.clone() : null;
    return entry;
  }
}

class LongHashtableEnumerator implements Enumeration {
  boolean keys;
  int index;
  LongHashtableEntry table[];
  LongHashtableEntry entry;

  LongHashtableEnumerator(LongHashtableEntry table[], boolean keys) {
    this.table = table;
    this.keys = keys;
    this.index = table.length;
  }

  public boolean hasMoreElements() {
    if (entry != null)
      return true;
    while (index -- > 0)
      if ((entry = table[index]) != null)
        return true;
    return false;
  }

  public Object nextElement() {
    if (entry == null) {
      while ((index -- > 0) && ((entry = table[index]) == null)) {
      }
    }
    if (entry != null) {
      LongHashtableEntry e = entry;
      entry = e.next;
      return keys ? (Object) new Long(e.key) : (Object) e.value;
    }
    throw new NoSuchElementException("LongHashtableEnumerator");
  }
}