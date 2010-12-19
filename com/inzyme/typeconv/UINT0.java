/* UINT0 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.typeconv;
import java.io.IOException;

public class UINT0 implements INumericPrimitive
{
    public int getValue() {
	return 0;
    }
    
    public short[] toArray() {
	short[] uint0Array = new short[0];
	return uint0Array;
    }
    
    public void write(LittleEndianOutputStream _outputStream)
	throws IOException {
	/* empty */
    }
    
    public void updateCRC(CRC16 _crc) {
	/* empty */
    }
    
    public void read(LittleEndianInputStream _inputStream) throws IOException {
	/* empty */
    }
    
    public int getLength() {
	return 0;
    }
    
    public boolean equals(Object _obj) {
	boolean equals = _obj != null && _obj instanceof UINT0;
	return equals;
    }
    
    public int hashCode() {
	return 0;
    }
    
    public byte getByteValue() {
	return (byte) 0;
    }
    
    public short getShortValue() {
	return (short) 0;
    }
    
    public int getIntValue() {
	return 0;
    }
    
    public long getLongValue() {
	return 0L;
    }
    
    public String toString() {
	return "[UINT0]";
    }
}
