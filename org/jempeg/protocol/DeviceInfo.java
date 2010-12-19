/* DeviceInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.io.IOException;

import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.UINT32;

public class DeviceInfo
{
    private CharArray myName = new CharArray(32);
    private CharArray mySoftwareVersion = new CharArray(32);
    private UINT32 myStorages = new UINT32();
    
    public String getName() {
	return myName.getNullTerminatedStringValue("ISO-8859-1");
    }
    
    public String getSoftwareVersion() {
	return mySoftwareVersion.getNullTerminatedStringValue("ISO-8859-1");
    }
    
    public int getStorages() {
	return (int) myStorages.getValue();
    }
    
    public void read(LittleEndianInputStream _is) throws IOException {
	myName.read(_is);
	mySoftwareVersion.read(_is);
	myStorages.read(_is);
    }
}
