/* BasicEmpegResponsePacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianInputStream;

public class BasicEmpegResponsePacket extends AbstractEmpegResponsePacket
{
    public BasicEmpegResponsePacket() {
	/* empty */
    }
    
    public BasicEmpegResponsePacket(EmpegPacketHeader _header) {
	super(_header);
    }
    
    protected void updateCRC(CRC16 _crc) {
	/* empty */
    }
    
    protected void read0(LittleEndianInputStream _os) throws IOException {
	/* empty */
    }
}
