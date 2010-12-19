/**
 * Copyright (c) 2001, Mike Schrag & Daniel Zimmerman All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
 * other contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package com.inzyme.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import com.inzyme.util.Debug;

/**
 * PropertiesManager provides a series of type-safe interfaces to a Properties
 * file (that is a multiple line "key=value" format).
 * 
 * The primary use in Emplode is that this interface is used to save runtime
 * configuration properties for future sessions.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.5 $
 */
public class PropertiesManager {
	private static PropertiesManager INSTANCE;
	private static PropertiesManager DEFAULTS = new PropertiesManager("Defaults", null);
	
	private Properties myProperties;
	private String myName;
	private File myPropertiesFile;

	public PropertiesManager(String _name, File _propertiesFile) {
		myName = _name;
		myProperties = new Properties();
		myPropertiesFile = _propertiesFile;
	}

	public Properties getProperties() {
		return myProperties;
	}
	
	public File getPropertiesFile() {
		return myPropertiesFile;
	}
	
	public static synchronized PropertiesManager getInstance() {
		if (INSTANCE == null) {
			throw new RuntimeException("You have not initialized this PropertiesManager prior to attempting to use it.");
		}
		return INSTANCE;
	}

	public static synchronized void initializeInstance(String _name, File _propertiesFile) {
		if (INSTANCE == null) {
			try {
				INSTANCE = new PropertiesManager(_name, _propertiesFile);
				if (_propertiesFile != null) {
					if (_propertiesFile.exists()) {
						INSTANCE.load();
					}
					else {
						new File(_propertiesFile.getParent()).mkdirs();
					}
				}
			}
			catch (Throwable e) {
				Debug.println(e);
				INSTANCE = new PropertiesManager(_name, null);
			}
		}
	}

	public static synchronized PropertiesManager getDefaults() {
		return DEFAULTS;
	}
	
	public String getProperty(String _property) {
		String value = myProperties.getProperty(_property);
		if (value == null && this != DEFAULTS) {
			value = DEFAULTS.getProperty(_property);
		}
		return value;
	}

	public String getProperty(String _property, String _default) {
		String value = getProperty(_property);
		if (value == null) {
			value = _default;
		}
		return value;
	}
		
	public void setProperty(String _name, String _value) {
		if (_value == null) {
			removeProperty(_name);
		}
		else {
			myProperties.put(_name, _value);
		}
	}

	public void removeProperty(String _name) {
		myProperties.remove(_name);
	}

	public void setIntProperty(String _name, int _value) {
		myProperties.put(_name, String.valueOf(_value));
	}

	public int getIntProperty(String _intProperty) {
		return getIntProperty(_intProperty, 0);
	}
	
	public int getIntProperty(String _intProperty, int _default) {
		String strValue = getProperty(_intProperty);
		int intValue;
		if (strValue == null) {
			intValue = _default;
		} else {
			try {
				intValue = Integer.parseInt(strValue);
			}
			catch (NumberFormatException e) {
				intValue = _default;
			}
		}
		return intValue;
	}

	public String[] getStringArrayProperty(String _strArrayProperty) {
		return getStringArrayProperty(_strArrayProperty, null);
	}
	
	public String[] getStringArrayProperty(String _strArrayProperty, String[] _default) {
		String prop = getProperty(_strArrayProperty);
		String[] values;
		if (prop == null) {
			values = _default;
		}
		else {
			Vector valuesVec = new Vector();
			StringTokenizer tokenizer = new StringTokenizer(prop, ",");
			while (tokenizer.hasMoreElements()) {
				String valueStr = tokenizer.nextToken().trim();
				valuesVec.addElement(valueStr);
			}
			values = new String[valuesVec.size()];
			valuesVec.copyInto(values);
		}
		return values;
	}

	public void setStringArrayProperty(String _strArrayProperty, String[] _values) {
		StringBuffer valuesStr = new StringBuffer();
		for (int i = 0; i < _values.length; i++) {
			valuesStr.append(_values[i]);
			valuesStr.append(',');
		}
		if (valuesStr.length() > 0) {
			valuesStr.setLength(valuesStr.length() - 1);
		}
		myProperties.put(_strArrayProperty, valuesStr.toString());
	}

	public int[] getIntArrayProperty(String _intArrayProperty) {
		int[] values;
		String[] strValues = getStringArrayProperty(_intArrayProperty);
		values = new int[strValues.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = Integer.parseInt(strValues[i]);
		}
		return values;
	}

	public void setIntArrayProperty(String _intArrayProperty, int[] _values) {
		StringBuffer valuesStr = new StringBuffer();
		for (int i = 0; i < _values.length; i++) {
			valuesStr.append(_values[i]);
			valuesStr.append(',');
		}
		if (valuesStr.length() > 0) {
			valuesStr.setLength(valuesStr.length() - 1);
		}
		myProperties.put(_intArrayProperty, valuesStr.toString());
	}

	public boolean getBooleanProperty(String _booleanProperty) {
		return getBooleanProperty(_booleanProperty, false);
	}
	
	public boolean getBooleanProperty(String _booleanProperty, boolean _default) {
		String prop = getProperty(_booleanProperty);
		if (prop == null) {
			prop = String.valueOf(_default);
		}
		boolean propValue = prop.equals("true");
		return propValue;
	}

	public void setBooleanProperty(String _booleanProperty, boolean _value) {
		setProperty(_booleanProperty, String.valueOf(_value));
	}

	public synchronized void save() throws IOException {
		try {
			if (myPropertiesFile != null) {
				FileOutputStream fos = new FileOutputStream(myPropertiesFile);
				try {
					myProperties.save(fos, myName);
				}
				finally {
					fos.close();
				}
			}
		}
		catch (SecurityException e) {
			Debug.println(e);
			// This is OK, I guess -- it means you're in an applet
		}
	}

	public synchronized void load() throws IOException {
		if (myPropertiesFile.exists()) {
			FileInputStream fis = new FileInputStream(myPropertiesFile);
			try {
				myProperties.load(fis);
			}
			finally {
				fis.close();
			}
		}
	}
}
