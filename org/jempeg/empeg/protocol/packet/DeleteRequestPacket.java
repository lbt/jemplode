/* DeleteRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class DeleteRequestPacket extends AbstractEmpegRequestPacket
{
    private UINT32 myFileID;
    private UINT32 myIDMask;
    
    public DeleteRequestPacket(UINT32 _packetID, UINT32 _fileID,
			       UINT32 _idMask) {
	super(_packetID);
	myFileID = _fileID;
	myIDMask = _idMask;
    }
    
    protected int getDataSize() {
	return myFileID.getLength() + myIDMask.getLength();
    }
    
    protected short getOpcode() {
	return (short) 8;
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
	return ("[DeleteRequestPacket: header = " + getHeader() + "; fileID = "
		+ myFileID + "; mask = " + myIDMask + "; crc = " + getCRC()
		+ "]");
    }
}
