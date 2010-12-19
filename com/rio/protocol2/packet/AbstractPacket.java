/* AbstractPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.util.ReflectionUtils;

public abstract class AbstractPacket
{
    private PacketHeader myHeader;
    
    static {
	ResourceBundleUtils.putBaseName
	    ("packetTypes", "com.rio.protocol2.packet.packetTypes");
    }
    
    public AbstractPacket(PacketHeader _header) {
	myHeader = _header;
    }
    
    public PacketHeader getHeader() {
	return myHeader;
    }
    
    public String getName() {
	ResourceBundleKey resourceBundleKey
	    = new ResourceBundleKey("packetTypes",
				    String.valueOf(getHeader().getType()
						       .getIntValue()));
	String name = resourceBundleKey.getString();
	return name;
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
