/* CollationKeyCache - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.text;
import java.text.CollationKey;
import java.text.Collator;

import Acme.LruHashtable;

import com.inzyme.container.ContainerUtils;

public class CollationKeyCache
{
    private static final int DEFAULT_SIZE = 200;
    private Collator myCollator;
    private LruHashtable myCollationKeyCache;
    
    public static CollationKeyCache createDefaultCache() {
	return new CollationKeyCache(ContainerUtils.getDefaultCollator());
    }
    
    public CollationKeyCache(Collator _collator) {
	myCollator = _collator;
	myCollationKeyCache = new LruHashtable(200);
    }
    
    public void setSize(int _size) {
	myCollationKeyCache = new LruHashtable(_size);
    }
    
    public void clear() {
	myCollationKeyCache.clear();
    }
    
    public CollationKey getCollationKey(String _value) {
	CollationKey key = (CollationKey) myCollationKeyCache.get(_value);
	if (key == null) {
	    key = myCollator.getCollationKey(_value);
	    myCollationKeyCache.put(_value, key);
	}
	return key;
    }
    
    public String toString() {
	return ("[CollationKeyCache: entries = " + myCollationKeyCache.size()
		+ "]");
    }
}
