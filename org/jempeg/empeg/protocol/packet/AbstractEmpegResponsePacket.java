/* AbstractEmpegResponsePacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.UINT16;

public abstract class AbstractEmpegResponsePacket extends AbstractEmpegPacket
{
    private UINT16 myCRC;
    
    public AbstractEmpegResponsePacket() {
	myCRC = new UINT16(-1);
    }
    
    public AbstractEmpegResponsePacket(EmpegPacketHeader _header) {
	super(_header);
	myCRC = new UINT16(-1);
    }
    
    protected void setCRC(UINT16 _crc) {
	myCRC = _crc;
    }
    
    public UINT16 getCRC() {
	return myCRC;
    }
    
    public void read(LittleEndianInputStream _is) throws IOException {
	EmpegPacketHeader header = getHeader();
	if (header == null) {
	    header = new EmpegPacketHeader();
	    header.read(_is);
	    setHeader(header);
	}
	read0(_is);
	myCRC.read(_is);
	UINT16 calcCRC = calcCRC();
	if (!myCRC.equals(calcCRC))
	    throw new IOException("CRC " + myCRC
				  + " does not match calculated CRC " + calcCRC
				  + " on " + header + ".");
    }
    
    protected abstract void updateCRC(CRC16 crc16);
    
    protected abstract void read0
	(LittleEndianInputStream littleendianinputstream) throws IOException;
}
