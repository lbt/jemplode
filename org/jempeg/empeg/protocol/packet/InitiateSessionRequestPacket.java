/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol.packet;

import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

/**
* Packet
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public class InitiateSessionRequestPacket extends AbstractEmpegRequestPacket {
	private CharArray myPassword;
  private CharArray myHostDescription;
	
	public InitiateSessionRequestPacket(UINT32 _packetID, String _password, String _hostDescription) {
    this(_packetID, new CharArray(_password, 64), new CharArray(_hostDescription, 256));
  }

	public InitiateSessionRequestPacket(UINT32 _packetID, CharArray _password, CharArray _hostDescription) {
		super(_packetID);
    myPassword = _password;
    myHostDescription = _hostDescription;
	}
	
	protected int getDataSize() {
		return myPassword.getLength() + myHostDescription.getLength();
	}
	
	protected short getOpcode() {
		return PacketConstants.OP_INITIATESESSION;
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
		return "[InitiateSessionRequestPacket: header = " + getHeader() + "; password = " + myPassword + "; hostDescription = " + myHostDescription + "; crc = " + getCRC() + "]";
	}
}
	
