/* FsckRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class FsckRequestPacket extends AbstractEmpegRequestPacket
{
    private CharArray myPartition;
    private UINT32 myForce;
    
    public FsckRequestPacket(UINT32 _packetID, String _partition,
			     UINT32 _force) {
	this(_packetID, new CharArray(_partition, 16), _force);
    }
    
    public FsckRequestPacket(UINT32 _packetID, CharArray _partition,
			     UINT32 _force) {
	super(_packetID);
	myPartition = _partition;
	myForce = _force;
    }
    
    protected int getDataSize() {
	return myPartition.getLength() + myForce.getLength();
    }
    
    protected short getOpcode() {
	return (short) 10;
    }
    
    protected CharArray getPartition() {
	return myPartition;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myPartition.updateCRC(_crc);
	myForce.updateCRC(_crc);
    }
    
    protected void write0(LittleEndianOutputStream _os) throws IOException {
	myPartition.write(_os);
	myForce.write(_os);
    }
    
    public String toString() {
	return ("[FsckRequestPacket: header = " + getHeader()
		+ "; partition = " + getPartition() + "; force = " + myForce
		+ "; crc = " + getCRC() + "]");
    }
}
