/* FormatRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class FormatRequestPacket extends AbstractRequestPacket
{
    private UINT32 myStorage;
    
    public FormatRequestPacket(UINT32 _storage) {
	super(new PacketHeader(18));
	myStorage = _storage;
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	myStorage.write(_os);
    }
}
