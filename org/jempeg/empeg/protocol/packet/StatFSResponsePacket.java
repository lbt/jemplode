/* StatFSResponsePacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;
import java.io.IOException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.UINT32;

public class StatFSResponsePacket extends AbstractEmpegResponsePacket
{
    private UINT32 myDrive0Size = new UINT32();
    private UINT32 myDrive0Space = new UINT32();
    private UINT32 myDrive0BlockSize = new UINT32();
    private UINT32 myDrive1Size = new UINT32();
    private UINT32 myDrive1Space = new UINT32();
    private UINT32 myDrive1BlockSize = new UINT32();
    
    public StatFSResponsePacket(EmpegPacketHeader _header) {
	super(_header);
    }
    
    public UINT32 getDrive0Size() {
	return myDrive0Size;
    }
    
    public UINT32 getDrive0Space() {
	return myDrive0Space;
    }
    
    public UINT32 getDrive0BlockSize() {
	return myDrive0BlockSize;
    }
    
    public UINT32 getDrive1Size() {
	return myDrive1Size;
    }
    
    public UINT32 getDrive1Space() {
	return myDrive1Space;
    }
    
    public UINT32 getDrive1BlockSize() {
	return myDrive1BlockSize;
    }
    
    protected void updateCRC(CRC16 _crc) {
	myDrive0Size.updateCRC(_crc);
	myDrive0Space.updateCRC(_crc);
	myDrive0BlockSize.updateCRC(_crc);
	myDrive1Size.updateCRC(_crc);
	myDrive1Space.updateCRC(_crc);
	myDrive1BlockSize.updateCRC(_crc);
    }
    
    protected void read0(LittleEndianInputStream _is) throws IOException {
	myDrive0Size.read(_is);
	myDrive0Space.read(_is);
	myDrive0BlockSize.read(_is);
	myDrive1Size.read(_is);
	myDrive1Space.read(_is);
	myDrive1BlockSize.read(_is);
    }
    
    public String toString() {
	return ("[StatFSResponsePacket: header = " + getHeader()
		+ "; drive0Size = " + myDrive0Size + "; drive0Space = "
		+ myDrive0Space + "; drive0BlockSize = " + myDrive0BlockSize
		+ "; drive1Size = " + myDrive1Size + "; drive1Space = "
		+ myDrive1Space + "; drive1BlockSize = " + myDrive1BlockSize
		+ "; crc = " + getCRC() + "]");
    }
}
