/* CommandRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class CommandRequestPacket extends AbstractEmpegRequestPacket
{
    private UINT32 myCommand;
    private UINT32 myParameter0;
    private UINT32 myParameter1;
    private CharArray myParameter2;
    
    public CommandRequestPacket(UINT32 _packetID, UINT32 _command,
				UINT32 _parameter0, UINT32 _parameter1,
				String _parameter2) {
	this(_packetID, _command, _parameter0, _parameter1,
	     new CharArray(_parameter2, 256));
    }
    
    public CommandRequestPacket(UINT32 _packetID, UINT32 _command,
				UINT32 _parameter0, UINT32 _parameter1,
				CharArray _parameter2) {
	super(_packetID);
	myCommand = _command;
	myParameter0 = _parameter0;
	myParameter1 = _parameter1;
	myParameter2 = _parameter2;
    }
    
    protected int getDataSize() {
	return (myCommand.getLength() + myParameter0.getLength()
		+ myParameter1.getLength() + myParameter2.getLength());
    }
    
    protected short getOpcode() {
	return (short) 12;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myCommand.updateCRC(_crc);
	myParameter0.updateCRC(_crc);
	myParameter1.updateCRC(_crc);
	myParameter2.updateCRC(_crc);
    }
    
    protected void write0(LittleEndianOutputStream _os) throws IOException {
	myCommand.write(_os);
	myParameter0.write(_os);
	myParameter1.write(_os);
	myParameter2.write(_os);
    }
    
    public String toString() {
	return ("[CommandRequestPacket: header = " + getHeader()
		+ "; command = " + myCommand + "; param0 = " + myParameter0
		+ "; param1 = " + myParameter1 + "; param2 = " + myParameter2
		+ "; crc = " + getCRC() + "]");
    }
}
