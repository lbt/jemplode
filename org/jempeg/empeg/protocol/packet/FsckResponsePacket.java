/* FsckResponsePacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.STATUS;
import com.inzyme.typeconv.UINT32;

public class FsckResponsePacket extends AbstractEmpegResponsePacket
{
    private STATUS myResult = new STATUS();
    private UINT32 myFlags = new UINT32();
    
    public FsckResponsePacket(EmpegPacketHeader _header) {
	super(_header);
    }
    
    public STATUS getResult() {
	return myResult;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myResult.updateCRC(_crc);
	myFlags.updateCRC(_crc);
    }
    
    protected void read0(LittleEndianInputStream _is) throws IOException {
	myResult.read(_is);
	myFlags.read(_is);
    }
    
    public String toString() {
	return ("[FsckResponsePacket: header = " + getHeader()
		+ "; response = " + myResult + "; crc = " + getCRC() + "]");
    }
}
