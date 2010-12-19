/* AbstractEmpegPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.UINT16;

public abstract class AbstractEmpegPacket
{
    private EmpegPacketHeader myHeader;
    
    public AbstractEmpegPacket() {
	/* empty */
    }
    
    public AbstractEmpegPacket(EmpegPacketHeader _header) {
	setHeader(_header);
    }
    
    protected void setHeader(EmpegPacketHeader _header) {
	myHeader = _header;
    }
    
    public EmpegPacketHeader getHeader() {
	return myHeader;
    }
    
    public abstract UINT16 getCRC();
    
    public UINT16 calcCRC() {
	CRC16 crc = new CRC16();
	myHeader.updateCRC(crc);
	updateCRC(crc);
	return crc.getValue();
    }
    
    protected abstract void updateCRC(CRC16 crc16);
    
    public String toString() {
	return ("[" + this.getClass().getName() + ": header = " + myHeader
		+ "; crc = " + getCRC() + "]");
    }
}
