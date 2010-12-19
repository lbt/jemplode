/* AbstractEmpegRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT16;
import com.inzyme.typeconv.UINT32;

public abstract class AbstractEmpegRequestPacket extends AbstractEmpegPacket
{
    public AbstractEmpegRequestPacket(UINT32 _packetID) {
	EmpegPacketHeader header
	    = new EmpegPacketHeader(0, (short) 0, getOpcode(), _packetID);
	setHeader(header);
    }
    
    public AbstractEmpegRequestPacket(EmpegPacketHeader _header) {
	super(_header);
    }
    
    public UINT16 getCRC() {
	return calcCRC();
    }
    
    public void write(LittleEndianOutputStream _os) throws IOException {
	getHeader().setDataSize(getDataSize());
	getHeader().write(_os);
	write0(_os);
	getCRC().write(_os);
    }
    
    protected abstract int getDataSize();
    
    protected abstract short getOpcode();
    
    protected abstract void updateCRC(CRC16 crc16);
    
    protected abstract void write0
	(LittleEndianOutputStream littleendianoutputstream) throws IOException;
}
