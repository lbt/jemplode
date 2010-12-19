/* NodeTags - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;

import org.jempeg.nodestore.event.INodeTagListener;

public class NodeTags implements Cloneable, Serializable
{
    private Properties myTags;
    private boolean myDirty;
    private INodeTagListener myListener;
    
    public NodeTags() {
	this(new Properties());
    }
    
    public NodeTags(Properties _props) {
	myTags = _props;
    }
    
    void setNodeTagListener(INodeTagListener _listener) {
	myListener = _listener;
    }
    
    public Properties toProperties() {
	return myTags;
    }
    
    public void copyTagsFrom(Properties _properties) {
	myTags = (Properties) _properties.clone();
    }
    
    public void copyTagsFrom(NodeTags _fromTags) {
	copyTagsFrom(_fromTags.toProperties());
    }
    
    public synchronized void setDirty(boolean _dirty) {
	if (myDirty != _dirty) {
	    if (myListener != null) {
		String oldDirtyStr = String.valueOf(myDirty);
		String newDirtyStr = String.valueOf(_dirty);
		myListener.beforeNodeTagModified(null, "dirty", oldDirtyStr,
						 newDirtyStr);
		myDirty = _dirty;
		myListener.afterNodeTagModified(null, "dirty", oldDirtyStr,
						newDirtyStr);
	    } else
		myDirty = _dirty;
	}
    }
    
    public boolean isDirty() {
	return myDirty;
    }
    
    public Object clone() {
	NodeTags newTags = new NodeTags();
	newTags.copyTagsFrom(this);
	return newTags;
    }
    
    public int getHexValue(String _tagName) {
	String tag = getValue(_tagName);
	if (tag.startsWith("0x"))
	    tag = tag.substring(2);
	int value;
	try {
	    value = Integer.parseInt(tag, 16);
	} catch (NumberFormatException e) {
	    value = 0;
	}
	return value;
    }
    
    public void setHexValue(String _tagName, int _value) {
	setValue(_tagName, "0x" + Integer.toHexString(_value));
    }
    
    public void setBooleanValue(String _tagName, boolean _value) {
	setValue(_tagName, String.valueOf(_value));
    }
    
    public boolean getBooleanValue(String _tagName) {
	String tagStr = getValue(_tagName);
	boolean tag = tagStr != null && (tagStr.equalsIgnoreCase("true")
					 || tagStr.equalsIgnoreCase("yes")
					 || tagStr.equals("1"));
	return tag;
    }
    
    public void setYesNoValue(String _tagName, boolean _value) {
	String value = _value ? "yes" : "";
	setValue(_tagName, value);
    }
    
    public int getIntValue(String _tagName, int _default) {
	String tag = getValue(_tagName);
	int intTag;
	do {
	    if (tag.length() > 0) {
		try {
		    intTag = Integer.parseInt(tag);
		    break;
		} catch (NumberFormatException e) {
		    intTag = _default;
		    break;
		}
	    }
	    intTag = _default;
	} while (false);
	return intTag;
    }
    
    public short getShortValue(String _tagName, short _default) {
	return (short) getIntValue(_tagName, _default);
    }
    
    public void setIntValue(String _tagName, int _value) {
	String strValue = String.valueOf(_value);
	setValue(_tagName, strValue);
    }
    
    public long getLongValue(String _tagName, long _default) {
	String tag = getValue(_tagName);
	long longTag;
	do {
	    if (tag.length() > 0) {
		try {
		    longTag = Long.parseLong(tag);
		    break;
		} catch (NumberFormatException e) {
		    longTag = _default;
		    break;
		}
	    }
	    longTag = _default;
	} while (false);
	return longTag;
    }
    
    public void setLongValue(String _tagName, long _value) {
	setValue(_tagName, String.valueOf(_value));
    }
    
    public String getValue(String _tagName) {
	String value = getRawValue(_tagName);
	if (value == null)
	    value = "";
	return value;
    }
    
    protected String getRawValue(String _tagName) {
	String value = myTags.getProperty(_tagName);
	return value;
    }
    
    protected void setRawValue(String _tagName, String _value) {
	if (_value == null)
	    myTags.remove(_tagName);
	else
	    myTags.put(_tagName, _value);
    }
    
    public void setValue(String _tagName, String _value) {
	setValue(_tagName, _value, true);
    }
    
    public void setValue(String _tagName, String _value, boolean _fireEvents) {
	boolean changed;
	if (myListener != null && _tagName != "length") {
	    String rawValue = getRawValue(_tagName);
	    String oldValue = rawValue == null ? "" : rawValue;
	    String newValue = _value == null ? "" : _value;
	    if (!oldValue.equals(newValue)) {
		if (_fireEvents)
		    myListener.beforeNodeTagModified(null, _tagName, oldValue,
						     newValue);
		setRawValue(_tagName, _value);
		if (_fireEvents) {
		    myListener.afterNodeTagModified(null, _tagName, oldValue,
						    newValue);
		    changed = true;
		} else
		    changed = false;
	    } else
		changed = false;
	} else {
	    setRawValue(_tagName, _value);
	    changed = true;
	}
	if (changed)
	    setDirty(true);
    }
    
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
