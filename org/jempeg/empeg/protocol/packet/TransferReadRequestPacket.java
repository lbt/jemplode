/* TransferReadRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class TransferReadRequestPacket extends AbstractEmpegRequestPacket
{
    private UINT32 myFileID;
    private UINT32 myChunkOffset;
    private UINT32 myChunkSize;
    
    public TransferReadRequestPacket(UINT32 _packetID, UINT32 _fileID,
				     UINT32 _chunkOffset, UINT32 _chunkSize) {
	super(_packetID);
	myFileID = _fileID;
	myChunkOffset = _chunkOffset;
	myChunkSize = _chunkSize;
    }
    
    protected int getDataSize() {
	return (myFileID.getLength() + myChunkOffset.getLength()
		+ myChunkSize.getLength());
    }
    
    protected short getOpcode() {
	return (short) 5;
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
	return ("[TransferReadRequestPacket: header = " + getHeader()
		+ "; fileID = " + myFileID + "; chunkOffset = " + myChunkOffset
		+ "; chunkSize = " + myChunkSize + "; crc = " + getCRC()
		+ "]");
    }
}
