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
import com.inzyme.typeconv.STATUS;

/**
* Packet
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public class CommandResponsePacket extends AbstractEmpegResponsePacket {
	private STATUS myResult = new STATUS();
	
	public CommandResponsePacket(EmpegPacketHeader _header) {
		super(_header);
	}
	
	public STATUS getResult() {
		return myResult;
	}
	
	protected void updateCRC(CRC16 _crc) {
    myResult.updateCRC(_crc);
	}
	
	protected void read0(LittleEndianInputStream _is) throws IOException {
    myResult.read(_is);
	}

	public String toString() {
		return "[CommandResponsePacket: header = " + getHeader() + "; response = " + myResult + "; crc = " + getCRC() + "]";
	}
}
	
