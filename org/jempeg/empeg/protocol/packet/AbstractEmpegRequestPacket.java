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
import com.inzyme.typeconv.UINT16;
import com.inzyme.typeconv.UINT32;

/**
* The superclass of all Empeg Request Packets.
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public abstract class AbstractEmpegRequestPacket extends AbstractEmpegPacket {
	public AbstractEmpegRequestPacket(UINT32 _packetID) {
		super();
		EmpegPacketHeader header = new EmpegPacketHeader(0, PacketConstants.OPTYPE_REQUEST, getOpcode(), _packetID);
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
	
	protected abstract void updateCRC(CRC16 _crc);
	
	protected abstract void write0(LittleEndianOutputStream _os) throws IOException;
}
	
