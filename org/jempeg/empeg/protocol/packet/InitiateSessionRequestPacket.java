/* InitiateSessionRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class InitiateSessionRequestPacket extends AbstractEmpegRequestPacket
{
    private CharArray myPassword;
    private CharArray myHostDescription;
    
    public InitiateSessionRequestPacket(UINT32 _packetID, String _password,
					String _hostDescription) {
	this(_packetID, new CharArray(_password, 64),
	     new CharArray(_hostDescription, 256));
    }
    
    public InitiateSessionRequestPacket(UINT32 _packetID, CharArray _password,
					CharArray _hostDescription) {
	super(_packetID);
	myPassword = _password;
	myHostDescription = _hostDescription;
    }
    
    protected int getDataSize() {
	return myPassword.getLength() + myHostDescription.getLength();
    }
    
    protected short getOpcode() {
	return (short) 14;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myPassword.updateCRC(_crc);
	myHostDescription.updateCRC(_crc);
    }
    
    protected void write0(LittleEndianOutputStream _os) throws IOException {
	myPassword.write(_os);
	myHostDescription.write(_os);
    }
    
    public String toString() {
	return ("[InitiateSessionRequestPacket: header = " + getHeader()
		+ "; password = " + myPassword + "; hostDescription = "
		+ myHostDescription + "; crc = " + getCRC() + "]");
    }
}
