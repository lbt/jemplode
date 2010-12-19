/* ReadRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;
import com.inzyme.typeconv.UINT64;

public class ReadRequestPacket extends AbstractRequestPacket
{
    private UINT64 myOffset;
    private UINT64 mySize;
    private UINT32 myFID;
    
    public ReadRequestPacket(UINT64 _offset, UINT64 _size, UINT32 _fid) {
	super(new PacketHeader(16));
	myOffset = _offset;
	mySize = _size;
	myFID = _fid;
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	myOffset.write(_os);
	mySize.write(_os);
	myFID.write(_os);
    }
    
    public UINT32 getFID() {
	return myFID;
    }
    
    public UINT64 getSize() {
	return mySize;
    }
    
    public UINT64 getOffset() {
	return myOffset;
    }
}
