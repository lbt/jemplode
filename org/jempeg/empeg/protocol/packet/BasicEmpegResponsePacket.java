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
import com.inzyme.typeconv.LittleEndianInputStream;

/**
* Packet
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public class BasicEmpegResponsePacket extends AbstractEmpegResponsePacket {
	public BasicEmpegResponsePacket() {
	}
	
	public BasicEmpegResponsePacket(EmpegPacketHeader _header) {
		super(_header);
	}
	
	protected void updateCRC(CRC16 _crc) {
	}
	
	protected void read0(LittleEndianInputStream _os) throws IOException {
	}
}
	
