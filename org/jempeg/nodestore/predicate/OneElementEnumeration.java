package org.jempeg.nodestore.predicate;

import java.util.Enumeration;
import java.util.NoSuchElementException;


public class OneElementEnumeration implements Enumeration {
	private Object myElement;
	
	public OneElementEnumeration(Object _element) {
		myElement = _element;
	}
	
	public boolean hasMoreElements() {
		return myElement != null;
	}
	
	public Object nextElement() {
		if (myElement == null) {
			throw new NoSuchElementException();
		}
		Object element = myElement;
		myElement = null;
		return element;
	}
}
