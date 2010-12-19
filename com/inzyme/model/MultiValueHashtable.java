package com.inzyme.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class MultiValueHashtable {
	private Hashtable myHashtable;

	public MultiValueHashtable() {
		myHashtable = new Hashtable();
	}

	public Enumeration keys() {
		return myHashtable.keys();
	}

	public void putValues(Object _key, Object[] _values) {
		if (_values.length == 0) {
			// ignore it ...
		}
		else if (_values.length == 1) {
			put(_key, _values[0]);
		}
		else {
			Object obj = myHashtable.get(_key);
			Vector vec;
			if (obj == null) {
				vec = new Vector();
			}
			else if (obj instanceof Vector) {
				vec = (Vector) obj;
			}
			else {
				vec = new Vector();
				vec.addElement(obj);
			}
			for (int i = 0; i < _values.length; i ++) {
				vec.addElement(_values[i]);
			}
			if (obj != vec) {
				myHashtable.put(_key, vec);
			}
		}
	}

	public void put(Object _key, Object _value) {
		Object obj = myHashtable.get(_key);
		if (obj == null) {
			myHashtable.put(_key, _value);
		}
		else if (obj instanceof Vector) {
			Vector vec = (Vector) obj;
			vec.addElement(_value);
		}
		else {
			Vector vec = new Vector();
			vec.addElement(obj);
			vec.addElement(_value);
			myHashtable.put(_key, vec);
		}
	}

	public void remove(Object _key, Object _value) {
		Object obj = myHashtable.get(_key);
		if (obj == null) {
		}
		else if (obj instanceof Vector) {
			Vector vec = (Vector) obj;
			vec.removeElement(_value);
			int size = vec.size();
			if (size == 0) {
				myHashtable.remove(_key);
			}
			else if (size == 1) {
				myHashtable.put(_key, vec.elementAt(0));
			}
		}
		else if (obj.equals(_value)) {
			myHashtable.remove(_key);
		}
	}

	public void removeEqualEqual(Object _key, Object _value) {
		Object obj = myHashtable.get(_key);
		if (obj == _value) {
			myHashtable.remove(_key);
		}
		else if (obj instanceof Vector) {
			Vector vec = (Vector) obj;
			boolean removed = false;
			int size = vec.size();
			for (int i = 0; !removed && i < size; i ++) {
				if (vec.elementAt(i) == _value) {
					vec.removeElementAt(i);
					removed = true;
				}
			}
			if (removed) {
				if (size == 1) {
					myHashtable.remove(_key);
				}
				else if (size == 2) {
					myHashtable.put(_key, vec.elementAt(0));
				}
			}
		}
	}

	public void removeAll(Object _key) {
		myHashtable.remove(_key);
	}

	public Object get(Object _key) {
		Object obj = myHashtable.get(_key);
		return obj;
	}

	public void clear() {
		myHashtable.clear();
	}
}
