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
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

/**
* Packet
*
* @author Mike Schrag
* @version $Revision: 1.5 $
*/
public class GrabScreenRequestPacket extends AbstractEmpegRequestPacket {
	private UINT32 myCommand;
	
	public GrabScreenRequestPacket(UINT32 _packetID, UINT32 _command) {
		super(_packetID);
		myCommand = _command;
	}
	
	protected int getDataSize() {
		return myCommand.getLength();
	}
	
	protected short getOpcode() {
		return PacketConstants.OP_GRABSCREEN;
	}
	
	protected void updateCRC(CRC16 _crc) {
    myCommand.updateCRC(_crc);
	}
	
	protected void write0(LittleEndianOutputStream _os) throws IOException {
    myCommand.write(_os);
	}

	public String toString() {
		return "[GrabScreenRequestPacket: header = " + getHeader() + "; command = " + myCommand + "; crc = " + getCRC() + "]";
	}
}
	
