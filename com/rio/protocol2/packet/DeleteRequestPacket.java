/* DeleteRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class DeleteRequestPacket extends AbstractRequestPacket
{
    private UINT32 myFID;
    
    public DeleteRequestPacket(UINT32 _fid) {
	super(new PacketHeader(17));
	myFID = _fid;
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	myFID.write(_os);
    }
}
