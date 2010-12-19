/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol.packet;

import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

/**
* Packet
*
* @author Mike Schrag
* @version $Revision: 1.5 $
*/
public class MountRequestPacket extends AbstractEmpegRequestPacket {
  public static UINT32 MODE_READONLY  = new UINT32(0);
  public static UINT32 MODE_READWRITE = new UINT32(1);

	private UINT32 myPartition;
	private UINT32 myMode;
	
	public MountRequestPacket(UINT32 _packetID, UINT32 _partition, UINT32 _mode) {
		super(_packetID);
		myPartition = _partition;
		myMode = _mode;
	}
	
	protected int getDataSize() {
		return myPartition.getLength() + myMode.getLength();
	}
	
	protected short getOpcode() {
		return PacketConstants.OP_MOUNT;
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
		return "[MountRequestPacket: header = " + getHeader() + "; partition = " + myPartition + "; mode = " + myMode + "; crc = " + getCRC() + "]";
	}
}
	
