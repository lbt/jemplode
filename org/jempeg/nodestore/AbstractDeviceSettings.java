package org.jempeg.nodestore;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import com.inzyme.util.NumericEnum;

/**
* GroupedProperties is the data format that is 
* used to represent an Empeg config file.  This
* is essentially Windows INI format.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public abstract class AbstractDeviceSettings implements Serializable, IDeviceSettings {
	private Vector myLines;
	private boolean myDirty;
	
	public AbstractDeviceSettings(String _configStr) {
		this();
		fromString(_configStr);
		myDirty = false;
	}
	
	public AbstractDeviceSettings() {
		myLines = new Vector();
	}

	public void fromString(String _configStr) {
		clear();
		StringTokenizer tokenizer = new StringTokenizer(_configStr, "\n", true);
    String lastToken = "";
		while (tokenizer.hasMoreElements()) {
			String line = tokenizer.nextToken();
      // skip newlines at the end of lines, but if they are "decorative" newlines, keep them
      if (!line.equals("\n") || lastToken.equals("\n")) {
	  		ConfigLine configLine = new ConfigLine(line);
		  	myLines.addElement(configLine);
      }
      lastToken = line;
		}
		
		myDirty = true;
	}

	public String getStringValue(String _section, String _key, String _defaultValue) {
		ConfigLine configLine = findLine(_section, _key);
		String value;
		if (configLine != null) {
			value = configLine.getValue();
		} else {
			value = _defaultValue;
		}
		return value;
	}
	
	public void setStringValue(String _section, String _key, String _value) {
		if (_section.indexOf('[') != -1) {
			throw new IllegalArgumentException("You can't have a '[' in a section name.");
		}
		if (_key.indexOf('=') != -1) {
			throw new IllegalArgumentException("You can't have an '=' in a key name.");
		}
		
		ConfigLine line = findLine(_section, _key);
		if (line != null) {
			// We found an existing key, update it.
			line.setContent(_key + "=" + _value);
		} else {
			int sectionPos = findSectionPos(0, _section);
			
			if (sectionPos == -1) {
			  // We didn't even find the section, create it at the end
			  // of the file
			  ConfigLine newLine = new ConfigLine(AbstractDeviceSettings.ConfigLine.Type.SECTION, '[' + _section + ']');
				myLines.addElement(newLine);
				sectionPos = myLines.size();
			} else {
				sectionPos ++;
			}
			// Now insert a new key=value pair.
			ConfigLine newLine = new ConfigLine(AbstractDeviceSettings.ConfigLine.Type.KEY, _key + '=' + _value);
			myLines.insertElementAt(newLine, sectionPos);
		}
		
		myDirty = true;
	}

	public boolean getBooleanValue(String _section, String _key, boolean _defaultValue) {
		String value = getStringValue(_section, _key, (_defaultValue) ? "1" : "0");
		boolean booleanValue = _defaultValue;
		if (value != null) {
			if (value.equals("0")) {
				booleanValue = false;
			} else if (value.equals("1")) {
				booleanValue = true;
			}
		}
		return booleanValue;
	}

	public void setBooleanValue(String _section, String _key, boolean _value) {
		setStringValue(_section, _key, (_value) ? "1" : "0");
	}
	
	public int getIntValue(String _section, String _key, int _defaultValue) {
		String strValue = getStringValue(_section, _key, String.valueOf(_defaultValue));
		int value = Integer.parseInt(strValue);
		return value;
	}

	public void setIntValue(String _section, String _key, int _value) {
		setStringValue(_section, _key, String.valueOf(_value));
	}
	
	public long getLongValue(String _section, String _key, long _defaultValue) {
		String strValue = getStringValue(_section, _key, String.valueOf(_defaultValue));
		long value = Long.parseLong(strValue);
		return value;
	}

	public ConfigLine getLine(int _pos) {
		return (ConfigLine)myLines.elementAt(_pos);
	}
	
	public ConfigLine findSection(int _startingOffset, String _section) {
		int pos = findSectionPos(_startingOffset, _section);
		if (pos == -1) {
			return null;
		} else {
			return getLine(pos);
		}
	}
	
	public ConfigLine findLine(String _section, String _key) {
		int pos = findLinePos(_section, _key);
		if (pos == -1) {
			return null;
		} else {
			return getLine(pos);
		}
	}
	
	private int findLinePos(String _section, String _key) {
		int size = myLines.size();
		int sectionPos = 0;
		while ((sectionPos = findSectionPos(sectionPos, _section)) != size && sectionPos != -1) {
			for (int keyPos = sectionPos + 1; keyPos != size && keyPos != -1 && !(getLine(keyPos).getType().equals(AbstractDeviceSettings.ConfigLine.Type.SECTION)); keyPos ++) {
				ConfigLine line = getLine(keyPos);
				if (line.getType().equals(AbstractDeviceSettings.ConfigLine.Type.KEY) && line.getKey().equalsIgnoreCase(_key)) {
					return keyPos;
				}
			}
			sectionPos ++;
		}
		return -1;
	}

	private int findSectionPos(int _startingOffset, String _section) {
		int size = myLines.size();
		for (int i = _startingOffset; i < size; i ++) {
			ConfigLine configLine = getLine(i);
			if (configLine.getType().equals(AbstractDeviceSettings.ConfigLine.Type.SECTION) && configLine.getSection().equalsIgnoreCase(_section)) {
				return i;
			}
		}
		return -1;
	}
	
	public void fromProperties(String _section, Properties _props) {
		Enumeration keys = _props.keys();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String value =_props.getProperty(key);
			setStringValue(_section, key, value);
		}
		
		myDirty = true;
	}
	
	public Properties toProperties(String _section) {
		Properties props = new Properties();
		int sectionPos = findSectionPos(0, _section);
		if (sectionPos != -1) {
			boolean done = false;
			do {
				ConfigLine line = getLine(sectionPos);
				if (line.getSection().equalsIgnoreCase(_section)) {
					if (line.getType().equals(AbstractDeviceSettings.ConfigLine.Type.KEY)) {
						String key = line.getKey();
						String value = line.getValue();
						props.put(key, value);
					}
				} else {
					done = true;
				}
			} while (!done); 
		}
		return props;
	}

	public void clear() {
		myLines.removeAllElements();
		myDirty = true;
	}
	
	public boolean isDirty() {
		return myDirty;
	}
	
	public void setDirty(boolean _dirty) {
		myDirty = _dirty;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		int size = myLines.size();
		for (int i = 0; i < size; i ++) {
			ConfigLine line = (ConfigLine)myLines.elementAt(i);
			sb.append(line.getContent());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private static class ConfigLine implements Serializable {
		private AbstractDeviceSettings.ConfigLine.Type myType;
		private String myContent;
		
		public ConfigLine(String _content) {
			myContent = _content.trim();
			if (_content.equals("\n") || _content.length() == 0 || _content.charAt(0) == '#' || _content.charAt(0) == ';') {
				myType = AbstractDeviceSettings.ConfigLine.Type.COMMENT;
			} else if (_content.charAt(0) == '[' && _content.charAt(_content.length() - 1) == ']') {
				myType = AbstractDeviceSettings.ConfigLine.Type.SECTION;
			} else if (_content.indexOf('=') == -1) {
				myType = AbstractDeviceSettings.ConfigLine.Type.COMMENT;
				myContent = "; ??? " + _content;
			} else {
				myType = AbstractDeviceSettings.ConfigLine.Type.KEY;
			}
		}
		
		public ConfigLine(AbstractDeviceSettings.ConfigLine.Type _type, String _content) {
			myType = _type;
			myContent = _content;
		}
		
		public AbstractDeviceSettings.ConfigLine.Type getType() {
			return myType;
		}
		
		public void setContent(String _content) {
			myContent = _content;
		}
		
		public String getContent() {
			return myContent;
		}
		
		public String getKey() {
			return myContent.substring(0, myContent.indexOf('='));
		}
		
		public String getSection() {
			return myContent.substring(1, myContent.length() - 1);
		}
		
		public String getValue() {
			return myContent.substring(myContent.indexOf('=') + 1);
		}
		
		public String toString() {
			return "[ConfigLine: type = " + myType + "; content = \"" + myContent + "\"]";
		}
		
		private static class Type extends NumericEnum implements Serializable {
			public static final Type COMMENT = new Type(0);
			public static final Type SECTION = new Type(1);
			public static final Type KEY     = new Type(2);
			
			public Type(int _value) {
				super(_value);
			}
			
			public String toString() {
				int value = Type.this.getValue();
				return (value == 0) ? "Comment" : (value == 1) ? "Section" : "Key";
			}
		}
	}
}