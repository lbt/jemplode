/* StatResponsePacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.STATUS;
import com.inzyme.typeconv.UINT32;

public class StatResponsePacket extends AbstractEmpegResponsePacket
{
    private STATUS myResult = new STATUS();
    private UINT32 myFileID = new UINT32();
    private UINT32 mySize = new UINT32();
    private int myMajorVersion;
    
    public StatResponsePacket(EmpegPacketHeader _header) {
	super(_header);
	if (_header.getDataSize() == 8)
	    myMajorVersion = 1;
	else
	    myMajorVersion = 2;
    }
    
    public STATUS getResult() {
	return myResult;
    }
    
    public UINT32 getFileID() {
	return myFileID;
    }
    
    public UINT32 getSize() {
	return mySize;
    }
    
    protected void updateCRC(CRC16 _crc) {
	if (myMajorVersion > 1)
	    myResult.updateCRC(_crc);
	myFileID.updateCRC(_crc);
	mySize.updateCRC(_crc);
    }
    
    protected void read0(LittleEndianInputStream _is) throws IOException {
	if (myMajorVersion > 1)
	    myResult.read(_is);
	myFileID.read(_is);
	mySize.read(_is);
    }
    
    public String toString() {
	return ("[StatResponsePacket: header = " + getHeader() + "; fileID = "
		+ myFileID + "; size = " + mySize + "; crc = " + getCRC()
		+ "]");
    }
}
