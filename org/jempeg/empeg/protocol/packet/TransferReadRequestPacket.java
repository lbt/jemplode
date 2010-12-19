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
public class TransferReadRequestPacket extends AbstractEmpegRequestPacket {
	private UINT32 myFileID;
	private UINT32 myChunkOffset;
	private UINT32 myChunkSize;  // NTS: Spec claims this is signed, but it barfs when I set it to that
	
	public TransferReadRequestPacket(UINT32 _packetID, UINT32 _fileID, UINT32 _chunkOffset, UINT32 _chunkSize) {
		super(_packetID);
		myFileID = _fileID;
		myChunkOffset = _chunkOffset;
		myChunkSize = _chunkSize;
	}
	
	protected int getDataSize() {
		return myFileID.getLength() + myChunkOffset.getLength() + myChunkSize.getLength();
	}
	
	protected short getOpcode() {
		return PacketConstants.OP_READFID;
	}
	
	protected void updateCRC(CRC16 _crc) {
    myFileID.updateCRC(_crc);
    myChunkOffset.updateCRC(_crc);
    myChunkSize.updateCRC(_crc);
	}
	
	protected void write0(LittleEndianOutputStream _os) throws IOException {
    myFileID.write(_os);
    myChunkOffset.write(_os);
    myChunkSize.write(_os);
	}

	public String toString() {
		return "[TransferReadRequestPacket: header = " + getHeader() + "; fileID = " + myFileID + "; chunkOffset = " + myChunkOffset + "; chunkSize = " + myChunkSize + "; crc = " + getCRC() + "]";
	}
}
	
