/* StatRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class StatRequestPacket extends AbstractEmpegRequestPacket
{
    private UINT32 myFileID;
    
    public StatRequestPacket(UINT32 _packetID, UINT32 _fileID) {
	super(_packetID);
	myFileID = _fileID;
    }
    
    protected int getDataSize() {
	return myFileID.getLength();
    }
    
    protected short getOpcode() {
	return (short) 7;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myFileID.updateCRC(_crc);
    }
    
    protected void write0(LittleEndianOutputStream _os) throws IOException {
	myFileID.write(_os);
    }
    
    public String toString() {
	return ("[StatRequestPacket: header = " + getHeader() + "; fileID = "
		+ myFileID + "; crc = " + getCRC() + "]");
    }
}
