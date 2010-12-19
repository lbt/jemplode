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
public class StatFSRequestPacket extends AbstractEmpegRequestPacket {
	public StatFSRequestPacket(UINT32 _packetID) {
		super(_packetID);
	}
	
	protected int getDataSize() {
		return 0;
	}
	
	protected short getOpcode() {
		return PacketConstants.OP_STATFS;
	}
	
	protected void updateCRC(CRC16 _crc) {
	}
	
	protected void write0(LittleEndianOutputStream _os) throws IOException {
	}

	public String toString() {
		return "[StatFSRequestPacket: header = " + getHeader() + "; crc = " + getCRC() + "]";
	}
}
	
