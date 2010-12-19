package org.jempeg.nodestore;


public class BasicDeviceSettings extends AbstractDeviceSettings {
	public BasicDeviceSettings() {
	}

	public String getName() {
		return "none";
	}

	public void setName(String _name) {
	}

	public long getDeviceGeneration() {
		return 0;
	}
	
	public long getSerialNumber() {
		return 0;
	}

	public int getWendyFlagCount() {
		return 0;
	}

	public void setWendyFlagCount(int _wendyCount) {
	}

	public String getWendyFlag(int _wendyFlagNum) {
		return null;
	}

	public void setWendyFlag(int _wendyFlagNum, String _value) {
	}

	public void setWendyFilters(WendyFilters _wendyFilters, PlayerDatabase _db) {
	}

	public WendyFilters getWendyFilters() {
		return new WendyFilters();
	}

}
