/* BasicDeviceSettings - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;

public class BasicDeviceSettings extends AbstractDeviceSettings
{
    public String getName() {
	return "none";
    }
    
    public void setName(String _name) {
	/* empty */
    }
    
    public long getDeviceGeneration() {
	return 0L;
    }
    
    public long getSerialNumber() {
	return 0L;
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
