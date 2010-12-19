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
public class TransferWriteRequestPacket extends AbstractEmpegRequestPacket {
	private UINT32 myFileID;
	private UINT32 myChunkOffset;
	private UINT32 myChunkSize;	// NTS: Spec claims this is signed, but it barfs when I set it to that
	private CharArray myBuffer;
	
	public TransferWriteRequestPacket(UINT32 _packetID, UINT32 _fileID, UINT32 _chunkOffset, CharArray _buffer) {
		this(_packetID, _fileID, _chunkOffset, new UINT32(_buffer.getLength()), _buffer);
	}
	
	public TransferWriteRequestPacket(UINT32 _packetID, UINT32 _fileID, UINT32 _chunkOffset, UINT32 _chunkSize, CharArray _buffer) {
		super(_packetID);
		myFileID = _fileID;
		myChunkOffset = _chunkOffset;
		myChunkSize = _chunkSize;
		myBuffer = _buffer;
	}
	
	protected int getDataSize() {
		return (myFileID.getLength() + myChunkOffset.getLength() + myChunkSize.getLength() + myBuffer.getLength());
	}
	
	protected short getOpcode() {
		return PacketConstants.OP_WRITEFID;
	}
	
	protected void updateCRC(CRC16 _crc) {
    myFileID.updateCRC(_crc);
    myChunkOffset.updateCRC(_crc);
    myChunkSize.updateCRC(_crc);
    myBuffer.updateCRC(_crc);
	}
	
	protected void write0(LittleEndianOutputStream _os) throws IOException {
    myFileID.write(_os);
    myChunkOffset.write(_os);
    myChunkSize.write(_os);
    myBuffer.write(_os);
	}

	public String toString() {
		return "[TransferWriteRequestPacket: header = " + getHeader() + "; fileID = " + myFileID + "; chunkOffset = " + myChunkOffset + "; chunkSize = " + myChunkSize + "; buffer size = " + myBuffer.getLength() + "; crc = " + getCRC() + "]";
	}
}
	
