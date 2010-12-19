/* PearlDeviceSettings - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2;
import org.jempeg.nodestore.AbstractDeviceSettings;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.WendyFilters;

public class PearlDeviceSettings extends AbstractDeviceSettings
{
    protected PearlDeviceSettings() {
	/* empty */
    }
    
    public long getDeviceGeneration() {
	return getLongValue("", "device_generation", 0L);
    }
    
    public long getSerialNumber() {
	return getLongValue("", "serial", 0L);
    }
    
    public String getName() {
	return getStringValue("", "name", "Karma");
    }
    
    public void setName(String _name) {
	/* empty */
    }
    
    public int getWendyFlagCount() {
	return 0;
    }
    
    public void setWendyFlagCount(int _wendyCount) {
	/* empty */
    }
    
    public String getWendyFlag(int _wendyFlagNum) {
	return null;
    }
    
    public void setWendyFlag(int _wendyFlagNum, String _value) {
	/* empty */
    }
    
    public void setWendyFilters(WendyFilters _wendyFilters,
				PlayerDatabase _db) {
	/* empty */
    }
    
    public WendyFilters getWendyFilters() {
	return new WendyFilters();
    }
}
