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
* The superclass of all Empeg Response Packets.
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public abstract class AbstractEmpegResponsePacket extends AbstractEmpegPacket {
	private UINT16 myCRC = new UINT16(-1);
	
	public AbstractEmpegResponsePacket() {
		super();
	}
	
	public AbstractEmpegResponsePacket(EmpegPacketHeader _header) {
		super(_header);
	}

	protected void setCRC(UINT16 _crc) {
		myCRC = _crc;
	}
	
	public UINT16 getCRC() {
		return myCRC;
	}
	
	public void read(LittleEndianInputStream _is) throws IOException {
		EmpegPacketHeader header = getHeader();
		if (header == null) {
			header = new EmpegPacketHeader();
			header.read(_is);
			setHeader(header);
		}
		read0(_is);
    myCRC.read(_is);
		UINT16 calcCRC = calcCRC();
		if (!myCRC.equals(calcCRC)) {
			throw new IOException("CRC " + myCRC + " does not match calculated CRC " + calcCRC + " on " + header + ".");
		}
	}
	
	protected abstract void updateCRC(CRC16 _crc);

	protected abstract void read0(LittleEndianInputStream _is) throws IOException;
}
	
