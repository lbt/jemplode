/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package com.inzyme.text;

import java.text.CollationKey;
import java.text.Collator;

import Acme.LruHashtable;

import com.inzyme.container.ContainerUtils;

/**
* Handy tree manipulation utilities.
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public class CollationKeyCache {
	private static final int DEFAULT_SIZE = 200;
	
	private Collator myCollator;
	private LruHashtable myCollationKeyCache;

  public static CollationKeyCache createDefaultCache() {
    return new CollationKeyCache(ContainerUtils.getDefaultCollator());
  }
  
	public CollationKeyCache(Collator _collator) {
		myCollator = _collator;
		myCollationKeyCache = new LruHashtable(DEFAULT_SIZE);
	}
	
	public void setSize(int _size) {
		myCollationKeyCache = new LruHashtable(_size);
	}

	/**
	* Clears the cache.
	*/
	public void clear() {
		myCollationKeyCache.clear();
	}

	/**
	* Returns a CollationKey for the given value.
	*
	* @param _value the value to return a key for
	* @returns a CollationKey for the given value
	*/
	public CollationKey getCollationKey(String _value) {
		CollationKey key = (CollationKey) myCollationKeyCache.get(_value);
		if (key == null) {
			key = myCollator.getCollationKey(_value);
			myCollationKeyCache.put(_value, key);
		}
		return key;
	}
	
	public String toString() {
		return "[CollationKeyCache: entries = " + myCollationKeyCache.size() + "]";
	}
}
