/* PrepareRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class PrepareRequestPacket extends AbstractEmpegRequestPacket
{
    private UINT32 myFileID;
    private UINT32 myFileSize;
    
    public PrepareRequestPacket(UINT32 _packetID, UINT32 _fileID,
				UINT32 _fileSize) {
	super(_packetID);
	myFileID = _fileID;
	myFileSize = _fileSize;
    }
    
    protected int getDataSize() {
	return myFileID.getLength() + myFileSize.getLength();
    }
    
    protected short getOpcode() {
	return (short) 6;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myFileID.updateCRC(_crc);
	myFileSize.updateCRC(_crc);
    }
    
    protected void write0(LittleEndianOutputStream _os) throws IOException {
	myFileID.write(_os);
	myFileSize.write(_os);
    }
    
    public String toString() {
	return ("[PrepareRequestPacket: header = " + getHeader()
		+ "; fileID = " + myFileID + "; size = " + myFileSize
		+ "; crc = " + getCRC() + "]");
    }
}
