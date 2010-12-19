/* NotifySyncCompleteResponsePacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.STATUS;

public class NotifySyncCompleteResponsePacket
    extends AbstractEmpegResponsePacket
{
    private STATUS myResult = new STATUS();
    
    public NotifySyncCompleteResponsePacket(EmpegPacketHeader _header) {
	super(_header);
    }
    
    public STATUS getResult() {
	return myResult;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myResult.updateCRC(_crc);
    }
    
    protected void read0(LittleEndianInputStream _is) throws IOException {
	myResult.read(_is);
    }
    
    public String toString() {
	return ("[NotifySyncCompleteResponsePacket: header = " + getHeader()
		+ "; result = " + myResult + "; crc = " + getCRC() + "]");
    }
}
