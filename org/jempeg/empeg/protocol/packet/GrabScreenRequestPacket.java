/* GrabScreenRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class GrabScreenRequestPacket extends AbstractEmpegRequestPacket
{
    private UINT32 myCommand;
    
    public GrabScreenRequestPacket(UINT32 _packetID, UINT32 _command) {
	super(_packetID);
	myCommand = _command;
    }
    
    protected int getDataSize() {
	return myCommand.getLength();
    }
    
    protected short getOpcode() {
	return (short) 13;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myCommand.updateCRC(_crc);
    }
    
    protected void write0(LittleEndianOutputStream _os) throws IOException {
	myCommand.write(_os);
    }
    
    public String toString() {
	return ("[GrabScreenRequestPacket: header = " + getHeader()
		+ "; command = " + myCommand + "; crc = " + getCRC() + "]");
    }
}
