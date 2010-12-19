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
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.STATUS;
import com.inzyme.typeconv.UINT32;

/**
* Packet
*
* @author Mike Schrag
* @version $Revision: 1.5 $
*/
public class TransferResponsePacket extends AbstractEmpegResponsePacket {
  private STATUS myResult = new STATUS();
  private UINT32 myFileID = new UINT32();
	private UINT32 myChunkOffset = new UINT32();
	private UINT32 myChunkSize = new UINT32();	// NTS: Spec claims this is signed, but it barfs when I set it to that
	private CharArray myBuffer = new CharArray();
	
	public TransferResponsePacket(EmpegPacketHeader _header) {
		super(_header);
	}
	
  public STATUS getResult() {
    return myResult;
  }

	public UINT32 getFileID() {
		return myFileID;
	}
	
	public UINT32 getChunkOffset() {
		return myChunkOffset;
	}
	
	public UINT32 getChunkSize() {
		return myChunkSize;
	}
	
	public CharArray getBuffer() {
		return myBuffer;
	}
	
	protected void updateCRC(CRC16 _crc) {
    myResult.updateCRC(_crc);
    myFileID.updateCRC(_crc);
    myChunkOffset.updateCRC(_crc);
    myChunkSize.updateCRC(_crc);
    myBuffer.updateCRC(_crc);
	}
	
	protected void read0(LittleEndianInputStream _is) throws IOException {
    myResult.read(_is);
    myFileID.read(_is);
    myChunkOffset.read(_is);
    myChunkSize.read(_is);
		
		int bufferSize = getHeader().getDataSize() - (myResult.getLength() + myFileID.getLength() + myChunkOffset.getLength() + myChunkSize.getLength());
    myBuffer.read(bufferSize, _is);
	}

	public String toString() {
		return "[TransferResponsePacket: header = " + getHeader() + "; fileID = " + myFileID + "; chunkOffset = " + myChunkOffset + "; chunkSize = " + myChunkSize + "; buffer size = " + myBuffer.getLength() + "; crc = " + getCRC() + "]";
	}
}
	
