package org.jempeg.empeg.protocol;

import java.util.StringTokenizer;

import org.jempeg.nodestore.AbstractDeviceSettings;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.WendyFilter;
import org.jempeg.nodestore.WendyFilters;
import org.jempeg.nodestore.WendyFlags;

public class EmpegDeviceSettings extends AbstractDeviceSettings {
	protected EmpegDeviceSettings(String _configStr) {
		super(_configStr);
	}
	
	public String getName() {
		return getStringValue("options", "name", "empegcar");
	}

	public void setName(String _name) {
		setStringValue("options", "name", _name);
	}
	
	public long getDeviceGeneration() {
		return getIntValue("", "device_generation", 0);
	}
	
	public long getSerialNumber() {
		return getLongValue("", "serial", 0);
	}
	
	public void setWendyFlagCount(int _wendyCount) {
		setIntValue("wendy", "flag_amount", _wendyCount);
	}

	public void setWendyFlag(int _wendyFlagNum, String _value) {
		setStringValue("wendy", "flag" + _wendyFlagNum, _value);
	}

	public int getWendyFlagCount() {
		return getIntValue("wendy", "flag_amount", 0);
	}

	public String getWendyFlag(int _wendyFlagNum) {
		return getStringValue("wendy", "flag" + _wendyFlagNum, "");
	}

	public WendyFilters getWendyFilters() {
		WendyFilters wendyFilters = new WendyFilters();
		WendyFlags wendyFlags = wendyFilters.getWendyFlags();
		wendyFlags.readFlags(this);
		String wendyFlagsStr = getStringValue("custom", "wendy", "");
		StringTokenizer tokenizer = new StringTokenizer(wendyFlagsStr, "/");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			WendyFilter filter = new WendyFilter("");
			filter.fromConfigFormat(wendyFlags, token);
			wendyFilters.addFilter(filter);
		}
		return wendyFilters;
	}

	public void setWendyFilters(WendyFilters _wendyFilters, PlayerDatabase _db) {
		WendyFlags wendyFlags = _wendyFilters.getWendyFlags();
		wendyFlags.writeFlags(this, _db);
		
		StringBuffer sb = new StringBuffer();
		int size = _wendyFilters.getSize();
		for (int i = 0; i < size; i ++) {
			WendyFilter filter = _wendyFilters.getFilterAt(i);
			String configFormat = filter.toConfigFormat(wendyFlags);
			sb.append(configFormat);
			if (i < size - 1) {
				sb.append("/");
			}
		}
		
		setStringValue("custom", "wendy", sb.toString());
	}
}
