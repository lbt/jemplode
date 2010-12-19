/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol.packet;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.UINT16;

/**
* The superclass of all EmpegPackets.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public abstract class AbstractEmpegPacket {
	private EmpegPacketHeader myHeader;
	
	public AbstractEmpegPacket() {
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
	
	protected abstract void updateCRC(CRC16 _crc);

	public String toString() {
		return "[" + getClass().getName() + ": header = " + myHeader + "; crc = " + getCRC() + "]";
	}
}
	
