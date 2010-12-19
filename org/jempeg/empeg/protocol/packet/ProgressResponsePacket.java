/* ProgressResponsePacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.UINT32;

public class ProgressResponsePacket extends AbstractEmpegResponsePacket
{
    private UINT32 myNewTimeout = new UINT32();
    private UINT32 myStage = new UINT32();
    private UINT32 myStageMaximum = new UINT32();
    private UINT32 myCurrent = new UINT32();
    private UINT32 myMaximum = new UINT32();
    private CharArray myString = new CharArray(64);
    
    public ProgressResponsePacket(EmpegPacketHeader _header) {
	super(_header);
    }
    
    public UINT32 getNewTimeout() {
	return myNewTimeout;
    }
    
    public UINT32 getStage() {
	return myStage;
    }
    
    public UINT32 getStageMaximum() {
	return myStageMaximum;
    }
    
    public UINT32 getCurrent() {
	return myCurrent;
    }
    
    public UINT32 getMaximum() {
	return myMaximum;
    }
    
    public CharArray getString() {
	return myString;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myNewTimeout.updateCRC(_crc);
	myStage.updateCRC(_crc);
	myStageMaximum.updateCRC(_crc);
	myCurrent.updateCRC(_crc);
	myMaximum.updateCRC(_crc);
	myString.updateCRC(_crc);
    }
    
    protected void read0(LittleEndianInputStream _is) throws IOException {
	myNewTimeout.read(_is);
	myStage.read(_is);
	myStageMaximum.read(_is);
	myCurrent.read(_is);
	myMaximum.read(_is);
	myString.read(_is);
    }
    
    public String toString() {
	return ("[ProgressResponsePacket: header = " + getHeader()
		+ "; newTimeout = " + myNewTimeout + "; stage = " + myStage
		+ "; stageMax = " + myStageMaximum + "; current = " + myCurrent
		+ "; max = " + myMaximum + "; str = " + "not displayed"
		+ "; crc = " + getCRC() + "]");
    }
}
