/* NotifySyncCompleteRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class NotifySyncCompleteRequestPacket extends AbstractEmpegRequestPacket
{
    private UINT32 myUnused;
    
    public NotifySyncCompleteRequestPacket(UINT32 _packetID) {
	this(_packetID, new UINT32(0L));
    }
    
    public NotifySyncCompleteRequestPacket(UINT32 _packetID, UINT32 _unused) {
	super(_packetID);
	myUnused = _unused;
    }
    
    protected int getDataSize() {
	return myUnused.getLength();
    }
    
    protected short getOpcode() {
	return (short) 17;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myUnused.updateCRC(_crc);
    }
    
    protected void write0(LittleEndianOutputStream _os) throws IOException {
	myUnused.write(_os);
    }
    
    public String toString() {
	return ("[NotifySyncCompleteRequestPacket: header = " + getHeader()
		+ "; crc = " + getCRC() + "]");
    }
}
