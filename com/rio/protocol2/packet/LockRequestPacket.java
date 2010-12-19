/* LockRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class LockRequestPacket extends AbstractRequestPacket
{
    public static final UINT32 READ = new UINT32(0L);
    public static final UINT32 WRITE = new UINT32(1L);
    private UINT32 myType;
    
    public LockRequestPacket(UINT32 _type) {
	super(new PacketHeader(9));
	myType = _type;
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	myType.write(_os);
    }
}
