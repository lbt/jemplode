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
import com.inzyme.typeconv.UINT16;

/**
* Packet
*
* @author Mike Schrag
* @version $Revision: 1.5 $
*/
public class PingResponsePacket extends AbstractEmpegResponsePacket {
	private UINT16 myVersionMinor = new UINT16();
	private UINT16 myVersionMajor = new UINT16();
	
	public PingResponsePacket(EmpegPacketHeader _header) {
		super(_header);
	}
	
	public UINT16 getVersionMinor() {
		return myVersionMinor;
	}
	
	public UINT16 getVersionMajor() {
		return myVersionMajor;
	}
	
	protected void updateCRC(CRC16 _crc) {
		myVersionMinor.updateCRC(_crc);
    myVersionMajor.updateCRC(_crc);
	}
	
	protected void read0(LittleEndianInputStream _is) throws IOException {
    myVersionMinor.read(_is);
    myVersionMajor.read(_is);
	}

	public String toString() {
		return "[PingResponsePacket: header = " + getHeader() + "; versionMajor = " + myVersionMajor + "; versionMinor = " + myVersionMinor + "; crc = " + getCRC() + "]";
	}
}
	
