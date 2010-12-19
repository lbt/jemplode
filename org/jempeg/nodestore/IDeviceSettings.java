/* IDeviceSettings - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.util.Properties;

public interface IDeviceSettings
{
    public String getName();
    
    public void setName(String string);
    
    public long getDeviceGeneration();
    
    public long getSerialNumber();
    
    public int getWendyFlagCount();
    
    public void setWendyFlagCount(int i);
    
    public String getWendyFlag(int i);
    
    public void setWendyFlag(int i, String string);
    
    public void setWendyFilters(WendyFilters wendyfilters,
				PlayerDatabase playerdatabase);
    
    public WendyFilters getWendyFilters();
    
    public boolean getBooleanValue(String string, String string_0_,
				   boolean bool);
    
    public void setBooleanValue(String string, String string_1_, boolean bool);
    
    public String getStringValue(String string, String string_2_,
				 String string_3_);
    
    public void setStringValue(String string, String string_4_,
			       String string_5_);
    
    public void fromString(String string);
    
    public void fromProperties(String string, Properties properties);
    
    public Properties toProperties(String string);
    
    public boolean isDirty();
    
    public void setDirty(boolean bool);
}
