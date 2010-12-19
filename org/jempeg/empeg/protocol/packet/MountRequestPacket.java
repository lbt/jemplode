/* MountRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class MountRequestPacket extends AbstractEmpegRequestPacket
{
    public static UINT32 MODE_READONLY = new UINT32(0L);
    public static UINT32 MODE_READWRITE = new UINT32(1L);
    private UINT32 myPartition;
    private UINT32 myMode;
    
    public MountRequestPacket(UINT32 _packetID, UINT32 _partition,
			      UINT32 _mode) {
	super(_packetID);
	myPartition = _partition;
	myMode = _mode;
    }
    
    protected int getDataSize() {
	return myPartition.getLength() + myMode.getLength();
    }
    
    protected short getOpcode() {
	return (short) 2;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myPartition.updateCRC(_crc);
	myMode.updateCRC(_crc);
    }
    
    protected void write0(LittleEndianOutputStream _os) throws IOException {
	myPartition.write(_os);
	myMode.write(_os);
    }
    
    public String toString() {
	return ("[MountRequestPacket: header = " + getHeader()
		+ "; partition = " + myPartition + "; mode = " + myMode
		+ "; crc = " + getCRC() + "]");
    }
}
