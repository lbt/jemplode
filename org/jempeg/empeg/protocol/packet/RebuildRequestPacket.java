/* RebuildRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class RebuildRequestPacket extends AbstractEmpegRequestPacket
{
    private UINT32 myOperation;
    
    public RebuildRequestPacket(UINT32 _packetID, UINT32 _operation) {
	super(_packetID);
	myOperation = _operation;
    }
    
    protected int getDataSize() {
	return myOperation.getLength();
    }
    
    protected short getOpcode() {
	return (short) 9;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myOperation.updateCRC(_crc);
    }
    
    protected void write0(LittleEndianOutputStream _os) throws IOException {
	myOperation.write(_os);
    }
    
    public String toString() {
	return ("[RebuildRequestPacket: header = " + getHeader()
		+ "; operation = " + myOperation + "; crc = " + getCRC()
		+ "]");
    }
}
