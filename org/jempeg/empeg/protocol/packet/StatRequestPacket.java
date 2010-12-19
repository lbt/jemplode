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
public class StatRequestPacket extends AbstractEmpegRequestPacket {
	private UINT32 myFileID;
	
	public StatRequestPacket(UINT32 _packetID, UINT32 _fileID) {
		super(_packetID);
		myFileID = _fileID;
	}
	
	protected int getDataSize() {
		return myFileID.getLength();
	}
	
	protected short getOpcode() {
		return PacketConstants.OP_STATFID;
	}
	
	protected void updateCRC(CRC16 _crc) {
    myFileID.updateCRC(_crc);
	}
	
	protected void write0(LittleEndianOutputStream _os) throws IOException {
    myFileID.write(_os);
	}

	public String toString() {
		return "[StatRequestPacket: header = " + getHeader() + "; fileID = " + myFileID + "; crc = " + getCRC() + "]";
	}
}
	
