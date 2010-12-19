/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol.packet;

import java.io.IOException;

import org.jempeg.empeg.protocol.EmpegProtocolException;

import com.inzyme.typeconv.CRC16;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

/**
* Packet
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class EmpegPacketHeader {
	private int myDataSize;
	private short myOpcode;
	private short myType;
	private UINT32 myPacketID;
	
	public EmpegPacketHeader() {
    myPacketID = new UINT32();
	}
	
	public EmpegPacketHeader(UINT32 _packetID) {
		this(-1, (short)-1, (short)-1, _packetID);
	}
	
	public EmpegPacketHeader(short _type, short _opcode, UINT32 _packetID) {
		this(-1, _type, _opcode, _packetID);
	}
	
	public EmpegPacketHeader(int _dataSize, short _type, short _opcode, UINT32 _packetID) {
		myDataSize = _dataSize;
		myOpcode = _opcode;
		myType = _type;
		myPacketID = _packetID;
	}
	
	void setOpcode(short _opcode) {
		myOpcode = _opcode;
	}
	
	public short getOpcode() {
		return myOpcode;
	}
	
	void setType(short _type) {
		myType = _type;
	}
	
	public short getType() {
		return myType;
	}

	public UINT32 getPacketID() {
		return myPacketID;
	}
	
	void setDataSize(int _dataSize) {
		myDataSize = _dataSize;
	}
	
	public int getDataSize() {
		return myDataSize;
	}
	
	public void write(LittleEndianOutputStream _os) throws IOException {
		_os.writeSigned8(PacketConstants.PSOH);
		_os.writeUnsigned16(myDataSize);
		_os.writeUnsigned8(myOpcode);
		_os.writeUnsigned8(myType);
    myPacketID.write(_os);
	}
	
	public void read(LittleEndianInputStream _is) throws EmpegProtocolException, IOException {
		byte b = _is.readSigned8();
		if (b != PacketConstants.PSOH) {
			switch (b) {
				case PacketConstants.TIMED_OUT:
					throw new EmpegProtocolException("Connection timed out.");
				case PacketConstants.NAK_DROPOUT:
					throw new EmpegProtocolException("Got NAK_DROPOUT instead of PSOH.");
				default:
					throw new EmpegProtocolException("Stream did not contain PSOH. (Got " + b + " instead)", b);
			}
		}
//		org.jempeg.util.Debug.println("We got a valid packet!");
		
		myDataSize = _is.readUnsigned16();
		myOpcode = _is.readUnsigned8();
		myType = _is.readUnsigned8();
    myPacketID.read(_is);
	}
		
	protected void updateCRC(CRC16 _crc) {
		_crc.updateUnsigned8(myOpcode);
		_crc.updateUnsigned8(myType);
    myPacketID.updateCRC(_crc);
	}
	
	public String toString() {
		return "[EmpegPacketHeader: size = " + myDataSize + "; type = " + myType + "; opCode = " + myOpcode + "; packetID = " + myPacketID + "]";
	}
}
	
