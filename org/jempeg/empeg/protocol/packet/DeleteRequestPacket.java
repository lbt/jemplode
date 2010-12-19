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
public class DeleteRequestPacket extends AbstractEmpegRequestPacket {
	private UINT32 myFileID;
	private UINT32 myIDMask;
	
	public DeleteRequestPacket(UINT32 _packetID, UINT32 _fileID, UINT32 _idMask) {
		super(_packetID);
		myFileID = _fileID;
		myIDMask = _idMask;
	}
	
	protected int getDataSize() {
		return myFileID.getLength() + myIDMask.getLength();
	}
	
	protected short getOpcode() {
		return PacketConstants.OP_DELETEFID;
	}
	
	protected void updateCRC(CRC16 _crc) {
    myFileID.updateCRC(_crc);
    myIDMask.updateCRC(_crc);
	}
	
	protected void write0(LittleEndianOutputStream _os) throws IOException {
    myFileID.write(_os);
    myIDMask.write(_os);
	}

	public String toString() {
		return "[DeleteRequestPacket: header = " + getHeader() + "; fileID = " + myFileID + "; mask = " + myIDMask + "; crc = " + getCRC() + "]";
	}
}
	
