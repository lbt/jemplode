/* PrepareRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;
import com.inzyme.typeconv.UINT64;

public class PrepareRequestPacket extends AbstractRequestPacket
{
    private UINT64 mySize;
    private UINT32 myFID;
    private UINT32 myStorage;
    
    public PrepareRequestPacket(UINT64 _size, UINT32 _fid, UINT32 _storage) {
	super(new PacketHeader(11));
	mySize = _size;
	myFID = _fid;
	myStorage = _storage;
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	mySize.write(_os);
	myFID.write(_os);
	myStorage.write(_os);
    }
}
