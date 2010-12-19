/* InitiateSessionResponsePacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.STATUS;
import com.inzyme.typeconv.UINT32;

public class InitiateSessionResponsePacket extends AbstractEmpegResponsePacket
{
    private STATUS myResult = new STATUS();
    private UINT32 mySessionCookie = new UINT32();
    private CharArray myFailureReason = new CharArray(256);
    
    public InitiateSessionResponsePacket(EmpegPacketHeader _header) {
	super(_header);
    }
    
    public STATUS getResult() {
	return myResult;
    }
    
    public UINT32 getSessionCookie() {
	return mySessionCookie;
    }
    
    public CharArray getFailureReason() {
	return myFailureReason;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myResult.updateCRC(_crc);
	mySessionCookie.updateCRC(_crc);
	myFailureReason.updateCRC(_crc);
    }
    
    protected void read0(LittleEndianInputStream _is) throws IOException {
	myResult.read(_is);
	mySessionCookie.read(_is);
	myFailureReason.read(_is);
    }
    
    public String toString() {
	return ("[InitiateSessionResponsePacket: header = " + getHeader()
		+ "; result = " + myResult + "; sessionCookie = "
		+ mySessionCookie + "; failureReason = "
		+ myFailureReason.getStringValue("ISO-8859-1") + "; crc = "
		+ getCRC() + "]");
    }
}
