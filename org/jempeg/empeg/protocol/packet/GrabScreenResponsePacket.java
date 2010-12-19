/* GrabScreenResponsePacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.STATUS;

public class GrabScreenResponsePacket extends AbstractEmpegResponsePacket
{
    private STATUS myResult = new STATUS();
    private CharArray myScreen = new CharArray(2048);
    
    public GrabScreenResponsePacket(EmpegPacketHeader _header) {
	super(_header);
    }
    
    public STATUS getResult() {
	return myResult;
    }
    
    public CharArray getScreen() {
	return myScreen;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myResult.updateCRC(_crc);
	myScreen.updateCRC(_crc);
    }
    
    protected void read0(LittleEndianInputStream _is) throws IOException {
	myResult.read(_is);
	myScreen.read(_is);
    }
    
    public String toString() {
	return ("[GrabScreenResponsePacket: header = " + getHeader()
		+ "; result = " + myResult + "; screen = " + "not shown"
		+ "; crc = " + getCRC() + "]");
    }
}
