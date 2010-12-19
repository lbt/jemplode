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
* @version $Revision: 1.4 $
*/
public class RebuildRequestPacket extends AbstractEmpegRequestPacket {
	private UINT32 myOperation; // NTS: spec says INT32
	
	public RebuildRequestPacket(UINT32 _packetID, UINT32 _operation) {
		super(_packetID);
		myOperation = _operation;
	}
	
	protected int getDataSize() {
		return myOperation.getLength();
	}
	
	protected short getOpcode() {
		return PacketConstants.OP_REBUILD;
	}
	
	protected void updateCRC(CRC16 _crc) {
    myOperation.updateCRC(_crc);
	}
	
	protected void write0(LittleEndianOutputStream _os) throws IOException {
    myOperation.write(_os);
	}

	public String toString() {
		return "[RebuildRequestPacket: header = " + getHeader() + "; operation = " + myOperation + "; crc = " + getCRC() + "]";
	}
}
	
