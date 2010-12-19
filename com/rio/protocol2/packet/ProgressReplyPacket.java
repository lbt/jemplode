/* ProgressReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.UINT32;

public class ProgressReplyPacket extends AbstractReplyPacket
{
    private UINT32 myNum = new UINT32();
    private UINT32 myDenom = new UINT32();
    
    public ProgressReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public UINT32 getNum() {
	return myNum;
    }
    
    public UINT32 getDenom() {
	return myDenom;
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException {
	myNum.read(_is);
	myDenom.read(_is);
    }
}
