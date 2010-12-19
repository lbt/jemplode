/**
 * This file is licensed under the GPL.
 *
 * See the LICENSE0 file included in this release, or
 * http://www.opensource.org/licenses/gpl-license.html
 * for the details of the license.
 */
package org.jempeg.nodestore;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;

import org.jempeg.nodestore.event.INodeTagListener;

/**
 * NodeTags is the datastructure that keeps track
 * of a set of key-value pairs of MP3 tags (like
 * title, album, etc.)  There is one NodeTags 
 * for each FID that is loaded.
 *
 * @author Mike Schrag
 * @version $Revision: 1.3 $
 */
public class NodeTags implements Cloneable, Serializable {
	private Properties myTags;
	private boolean myDirty;
	private INodeTagListener myListener;

	/**
	 * Creates a new NodeTags.
	 */
	public NodeTags() {
		this(new Properties());
	}

	/**
	 * Creates a new NodeTags.
	 * 
	 * @param _props the properties to construct this tag with
	 */
	public NodeTags(Properties _props) {
		myTags = _props;
	}

	/**
	 * Sets the node tag change listener (probably an IFIDNode) for
	 * this NodeTags.
	 * 
	 * @param _listener the listener to set
	 */
	void setNodeTagListener(INodeTagListener _listener) {
		myListener = _listener;
	}

	/**
	 * Return a Properties that contains the tag names and their corresponding
	 * values.
	 * 
	 * @returns a Properties of key/value tag pairs
	 */
	public Properties toProperties() {
		return myTags;
	}

	/**
	 * Clears this NodeTags and loads all the data
	 * from the given Hashtable.  The keys of the
	 * Hashtable should be String tag names.
	 * 
	 * @param _hashtable the Hashtable to load data from
	 */
	public void copyTagsFrom(Properties _properties) {
		myTags = (Properties) _properties.clone();
	}

	/**
	 * Copies all the key/value pairs from the 
	 * given NodeTags into this NodeTags.
	 * 
	 * @param _fromTags the NodeTags to copy from
	 */
	public void copyTagsFrom(NodeTags _fromTags) {
		copyTagsFrom(_fromTags.toProperties());
	}

	/**
	 * Sets whether or not this NodeTags has been modified.
	 * 
	 * @param _dirty whether or not this NodeTags has been modified
	 */
	public synchronized void setDirty(boolean _dirty) {
		if (myDirty != _dirty) {
			if (myListener != null) {
				String oldDirtyStr = String.valueOf(myDirty);
				String newDirtyStr = String.valueOf(_dirty);
				myListener.beforeNodeTagModified(null, DatabaseTags.DIRTY_TAG, oldDirtyStr, newDirtyStr);
				myDirty = _dirty;
				myListener.afterNodeTagModified(null, DatabaseTags.DIRTY_TAG, oldDirtyStr, newDirtyStr);
			}
			else {
				myDirty = _dirty;
			}
		}
	}

	/**
	 * Returns whether or not this NodeTags has been modified.
	 * 
	 * @return whether or not this NodeTags has been modified
	 */
	public boolean isDirty() {
		return myDirty;
	}

	/**
	 * Returns a clone of this NodeTags.
	 * 
	 * @return a clone of this NodeTags
	 */
	public Object clone() {
		NodeTags newTags = new NodeTags();
		newTags.copyTagsFrom(this);
		return newTags;
	}

	/**
	 * Returns the int value of a 0xXXX format String.
	 * 
	 * @param _tagName the tag name to lookup
	 * @return the tag value
	 */
	public int getHexValue(String _tagName) {
		String tag = getValue(_tagName);
		int value;
		if (tag.startsWith("0x")) {
			tag = tag.substring(2);
		}
		try {
			value = Integer.parseInt(tag, 16);
		}
		catch (NumberFormatException e) {
			value = 0;
		}
		return value;
	}

	/**
	 * Sets the value of a 0xXXX format String from an int.
	 * 
	 * @param _tagName the tag name to set
	 * @param _value the value to set
	 */
	public void setHexValue(String _tagName, int _value) {
		setValue(_tagName, "0x" + Integer.toHexString(_value));
	}

	/**
	 * Sets the value of a "true/false" valued String.
	 * 
	 * @param _tagName the tag name to set
	 * @param _value the value to set
	 */
	public void setBooleanValue(String _tagName, boolean _value) {
		setValue(_tagName, String.valueOf(_value));
	}

	/**
	 * Returns the boolean value of a "true/false/yes/no" valued String.
	 * 
	 * @param _tagName the tag name to lookup
	 * @return the tag value
	 */
	public boolean getBooleanValue(String _tagName) {
		String tagStr = getValue(_tagName);
		boolean tag = (tagStr != null && (tagStr.equalsIgnoreCase("true") || tagStr.equalsIgnoreCase("yes") || tagStr.equals("1")));
		return tag;
	}

	/**
	 * Sets the value of a "yes/no" (actually yes/"") valued String.
	 * 
	 * @param _tagName the tag name to set
	 * @param _value the value to set
	 */
	public void setYesNoValue(String _tagName, boolean _value) {
		String value = (_value) ? "yes" : "";
		setValue(_tagName, value);
	}

	/**
	 * Returns the value of a tag that contains an integer.
	 * 
	 * @param _tagName the tag name to lookup
	 * @param _default the default value if it's not set
	 * @return the tag value
	 */
	public int getIntValue(String _tagName, int _default) {
		String tag = getValue(_tagName);
		int intTag;
		if (tag.length() > 0) {
			try {
				intTag = Integer.parseInt(tag);
			}
			catch (NumberFormatException e) {
				intTag = _default;
			}
		}
		else {
			intTag = _default;
		}
		return intTag;
	}

	/**
	 * Returns the value of a tag that contains a short.
	 * 
	 * @param _tagName the tag name to lookup
	 * @param _default the default value if it's not set
	 * @return the tag value
	 */
	public short getShortValue(String _tagName, short _default) {
		return (short) getIntValue(_tagName, _default);
	}

	/**
	 * Sets the value of a tag that contains an integer.
	 * 
	 * @param _tagName the tag name to set
	 * @param _value the tag value
	 */
	public void setIntValue(String _tagName, int _value) {
		String strValue = String.valueOf(_value);
		setValue(_tagName, strValue);
	}

	/**
	 * Returns the value of a tag that contains a long.
	 * 
	 * @param _tagName the tag name to lookup
	 * @param _default the default value to use if it's not set
	 * @return the tag value
	 */
	public long getLongValue(String _tagName, long _default) {
		String tag = getValue(_tagName);
		long longTag;
		if (tag.length() > 0) {
			try {
				longTag = Long.parseLong(tag);
			}
			catch (NumberFormatException e) {
				longTag = _default;
			}
		}
		else {
			longTag = _default;
		}
		return longTag;
	}

	/**
	 * Sets the value of a tag that contains a long.
	 * 
	 * @param _tagName the tag name to set
	 * @param _value the tag value
	 */
	public void setLongValue(String _tagName, long _value) {
		setValue(_tagName, String.valueOf(_value));
	}

	/**
	 * Returns the value of a tag that contains a String (the default).
	 * 
	 * @param _tagName the tag name to lookup
	 * @return the tag value
	 */
	public String getValue(String _tagName) {
		String value = getRawValue(_tagName);
		if (value == null) {
			value = "";
		}
		return value;
	}

	/**
	 * Returns the value of a tag that contains a String (or null if it isn't set)
	 * 
	 * @param _tagName the tag name to lookup
	 * @return the tag value
	 */
	protected String getRawValue(String _tagName) {
		String value = myTags.getProperty(_tagName);
		return value;
	}

	/**
	 * Sets the value of the given tag name without firing events or remove if the
	 * value is null.
	 * 
	 * @param _tagName the name of the tag to set
	 * @param _value the new value of the tag
	 */
	protected void setRawValue(String _tagName, String _value) {
		if (_value == null) {
			myTags.remove(_tagName);
		}
		else {
			myTags.put(_tagName, _value);
		}
	}

	/**
	 * Sets the value of a tag that contains a String (the default).
	 * 
	 * @param _tagName the tag name to set
	 * @param _value the tag value
	 */
	public void setValue(String _tagName, String _value) {
		setValue(_tagName, _value, true);
	}

	/**
	 * Sets the value of a tag that contains a String (the default).
	 * 
	 * @param _tagName the tag name to set
	 * @param _value the tag value
	 * @param _fireEvents whether or not to fire events (and mark this dirty)
	 */
	public void setValue(String _tagName, String _value, boolean _fireEvents) {
		boolean changed;
		if (myListener != null && _tagName != DatabaseTags.LENGTH_TAG) {
			String rawValue = getRawValue(_tagName);
			String oldValue = (rawValue == null) ? "" : rawValue;
			String newValue = (_value == null) ? "" : _value;
			if (!oldValue.equals(newValue)) {
				if (_fireEvents) {
					myListener.beforeNodeTagModified(null, _tagName, oldValue, newValue);
				}
				setRawValue(_tagName, _value);
				if (_fireEvents) {
					myListener.afterNodeTagModified(null, _tagName, oldValue, newValue);
					changed = true;
				}
				else {
					changed = false;
				}
			}
			else {
				changed = false;
			}
		}
		else {
			setRawValue(_tagName, _value);
			changed = true;
		}
		if (changed) {
			setDirty(true);
		}
	}

	/**
	 * Returns the set of tag names that are used
	 * by this NodeTags.
	 * 
	 * @return the tag names that this contains
	 */
	public Enumeration getTagNames() {
		return myTags.keys();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[NodeTags: ");
		Enumeration tagNames = myTags.keys();
		while (tagNames.hasMoreElements()) {
			String tagName = (String) tagNames.nextElement();
			String value = getValue(tagName);
			sb.append(tagName);
			sb.append('=');
			sb.append(value);
			sb.append(", ");
		}
		sb.setLength(sb.length() - 2);
		sb.append(']');
		return sb.toString();
	}
}
