/* SessionHeartbeatRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class SessionHeartbeatRequestPacket extends AbstractEmpegRequestPacket
{
    private UINT32 mySessionCookie;
    
    public SessionHeartbeatRequestPacket(UINT32 _packetID,
					 UINT32 _sessionCookie) {
	super(_packetID);
	mySessionCookie = _sessionCookie;
    }
    
    protected int getDataSize() {
	return mySessionCookie.getLength();
    }
    
    protected short getOpcode() {
	return (short) 15;
    }
    
    protected void updateCRC(CRC16 _crc) {
	mySessionCookie.updateCRC(_crc);
    }
    
    protected void write0(LittleEndianOutputStream _os) throws IOException {
	mySessionCookie.write(_os);
    }
    
    public String toString() {
	return ("[SessionHeartbeatRequestPacket: header = " + getHeader()
		+ "; sessionCookie = " + mySessionCookie + "; crc = "
		+ getCRC() + "]");
    }
}
