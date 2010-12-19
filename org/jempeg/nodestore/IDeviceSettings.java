package org.jempeg.nodestore;

import java.util.Properties;

public interface IDeviceSettings {
	public String getName();
	public void setName(String _name);
	
	public long getDeviceGeneration();
	
	public long getSerialNumber();
	
	public int getWendyFlagCount();
	public void setWendyFlagCount(int _wendyCount);
	
	public String getWendyFlag(int _wendyFlagNum);
	public void setWendyFlag(int _wendyFlagNum, String _value);
	
	// this requires a PlayerDatabase because we have to clean up existing wendy
	// filter references if a user removes a wendy filter
	public void setWendyFilters(WendyFilters _wendyFilters, PlayerDatabase _db);
	public WendyFilters getWendyFilters();
	
	public boolean getBooleanValue(String _section, String _key, boolean _defaultValue);
	public void setBooleanValue(String _section, String _key, boolean _value);
	
	public String getStringValue(String _section, String _key, String _defaultValue);
	public void setStringValue(String _section, String _key, String _value);
	
	public void fromString(String _deviceSetingsStr);
	public void fromProperties(String _sectionName, Properties _properties);
	public Properties toProperties(String _sectionName);
	
	public boolean isDirty();
	public void setDirty(boolean _dirty);
}
