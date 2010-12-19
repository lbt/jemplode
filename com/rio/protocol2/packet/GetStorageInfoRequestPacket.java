/* GetStorageInfoRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class GetStorageInfoRequestPacket extends AbstractRequestPacket
{
    private UINT32 myWhich;
    
    public GetStorageInfoRequestPacket(UINT32 _which) {
	super(new PacketHeader(6));
	myWhich = _which;
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	myWhich.write(_os);
    }
}
