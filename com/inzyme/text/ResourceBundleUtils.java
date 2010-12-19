/* ResourceBundleUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.text;
import java.util.Properties;

import com.inzyme.util.Debug;

public class ResourceBundleUtils
{
    public static final String ERRORS_KEY = "errors";
    public static final String UI_KEY = "ui";
    private static Properties BASENAMEKEY_TO_BASENAME = new Properties();
    private static String UI_BASENAME = null;
    private static String ERRORS_BASENAME = null;
    
    public static void putBaseName(String _baseNameKey, String _baseName) {
	BASENAMEKEY_TO_BASENAME.put(_baseNameKey, _baseName);
	if (_baseNameKey == "ui")
	    UI_BASENAME = _baseName;
	else if (_baseNameKey == "errors")
	    ERRORS_BASENAME = _baseName;
    }
    
    public static String getBaseName(String _baseNameKey) {
	String baseName = null;
	if (_baseNameKey == "ui")
	    baseName = UI_BASENAME;
	else if (_baseNameKey == "errors")
	    baseName = ERRORS_BASENAME;
	if (baseName == null) {
	    baseName = BASENAMEKEY_TO_BASENAME.getProperty(_baseNameKey);
	    if (baseName == null) {
		Debug.println
		    (8,
		     ("There is no resource bundle basename mapped for type '"
		      + _baseNameKey + "'."));
		baseName = _baseNameKey;
	    }
	}
	return baseName;
    }
    
    public static String getUIString(String _key) {
	return getString("ui", _key, _key);
    }
    
    public static String getUIString(String _key, String _default) {
	return getString("ui", _key, _default);
    }
    
    public static String getUIString(String _key, Object[] _values) {
	return getString("ui", _key, _values, _key);
    }
    
    public static String getUIString(String _key, Object[] _values,
				     String _default) {
	return getString("ui", _key, _values, _default);
    }
    
    public static String getErrorString(String _key) {
	return getString("errors", _key, _key);
    }
    
    public static String getErrorString(String _key, String _default) {
	return getString("errors", _key, _default);
    }
    
    public static String getErrorString(String _key, Object[] _values) {
	return getString("errors", _key, _values, _key);
    }
    
    public static String getErrorString(String _key, Object[] _values,
					String _default) {
	return getString("errors", _key, _values, _default);
    }
    
    public static String getString(String _baseNameKey, String _key) {
	return new ResourceBundleKey(_baseNameKey, _key, _key).getString();
    }
    
    public static String getString(String _baseNameKey, String _key,
				   String _default) {
	return new ResourceBundleKey(_baseNameKey, _key, _default).getString();
    }
    
    public static String getString(String _baseNameKey, String _key,
				   Object[] _values) {
	return new ResourceBundleKey(_baseNameKey, _key, _values, _key)
		   .getString();
    }
    
    public static String getString(String _baseNameKey, String _key,
				   Object[] _values, String _default) {
	return new ResourceBundleKey(_baseNameKey, _key, _values, _default)
		   .getString();
    }
}
