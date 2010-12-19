/* PingRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class PingRequestPacket extends AbstractEmpegRequestPacket
{
    public PingRequestPacket(UINT32 _packetID) {
	super(_packetID);
    }
    
    protected int getDataSize() {
	return 0;
    }
    
    protected short getOpcode() {
	return (short) 0;
    }
    
    protected void updateCRC(CRC16 _crc) {
	/* empty */
    }
    
    protected void write0(LittleEndianOutputStream _os) throws IOException {
	/* empty */
    }
    
    public String toString() {
	return ("[PingRequestPacket: header = " + getHeader() + "; crc = "
		+ getCRC() + "]");
    }
}
