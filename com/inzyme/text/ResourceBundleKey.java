/* ResourceBundleKey - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.text;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.inzyme.util.Debug;
import com.inzyme.util.ReflectionUtils;

public class ResourceBundleKey
{
    private String myBaseNameKey;
    private String myKey;
    private String myDefault;
    private Object[] myValues;
    
    public ResourceBundleKey(String _baseNameKey, String _key,
			     Object[] _values) {
	this(_baseNameKey, _key, _values, _key);
    }
    
    public ResourceBundleKey(String _baseNameKey, String _key,
			     Object[] _values, String _default) {
	this(_baseNameKey, _key, _default);
	myValues = _values;
    }
    
    public ResourceBundleKey(String _baseNameKey, String _key) {
	this(_baseNameKey, _key, _key);
    }
    
    public ResourceBundleKey(String _baseNameKey, String _key,
			     String _default) {
	myBaseNameKey = _baseNameKey;
	myKey = _key;
	myDefault = _default;
    }
    
    public String getKey() {
	return myKey;
    }
    
    public String getBaseNameKey() {
	return myBaseNameKey;
    }
    
    public Object[] getValues() {
	return myValues;
    }
    
    public String getString() {
	String value;
	if (myValues != null)
	    value = getString(myValues);
	else {
	    String baseName = null;
	    try {
		baseName = ResourceBundleUtils.getBaseName(myBaseNameKey);
		ResourceBundle resourceBundle
		    = ResourceBundle.getBundle(baseName);
		String str = resourceBundle.getString(myKey);
		value = str;
	    } catch (MissingResourceException e) {
		Debug.println(8, ("Missing entry for '" + myKey
				  + "' in resource bundle '" + baseName + " ("
				  + myBaseNameKey + ")'."));
		value = myDefault;
	    } catch (Throwable e) {
		Debug.println(8, e);
		value = myDefault;
	    }
	}
	return value;
    }
    
    public String getString(Object[] _replacementsVars) {
	String baseName = null;
	try {
	    baseName = ResourceBundleUtils.getBaseName(myBaseNameKey);
	    ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName);
	    String str = resourceBundle.getString(myKey);
	    MessageFormat messageFormat = new MessageFormat(str);
	    String formattedStr = messageFormat.format(_replacementsVars);
	    return formattedStr;
	} catch (Throwable e) {
	    Debug.println(8, ("Missing entry for '" + myKey
			      + "' in resource bundle '" + baseName + " ("
			      + myBaseNameKey + ")'."));
	    return myDefault;
	}
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
