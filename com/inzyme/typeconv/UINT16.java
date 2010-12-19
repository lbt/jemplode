/* UINT16 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.typeconv;
import java.io.IOException;

public class UINT16 implements INumericPrimitive
{
    private int myValue;
    
    public UINT16() {
	/* empty */
    }
    
    public UINT16(int _value) {
	myValue = _value;
    }
    
    public void setValue(int _value) {
	myValue = _value;
    }
    
    public int getValue() {
	return myValue;
    }
    
    public byte[] toArray() {
	byte[] uint16Array = LittleEndianUtils.toUnsigned16Array(myValue);
	return uint16Array;
    }
    
    public void write(LittleEndianOutputStream _outputStream)
	throws IOException {
	_outputStream.writeUnsigned16(myValue);
    }
    
    public void updateCRC(CRC16 _crc) {
	_crc.updateUnsigned16(myValue);
    }
    
    public void read(LittleEndianInputStream _inputStream) throws IOException {
	myValue = _inputStream.readUnsigned16();
    }
    
    public int getLength() {
	return 2;
    }
    
    public boolean equals(Object _obj) {
	boolean equals = (_obj != null && _obj instanceof UINT16
			  && ((UINT16) _obj).myValue == myValue);
	return equals;
    }
    
    public int hashCode() {
	return myValue;
    }
    
    public byte getByteValue() {
	return (byte) myValue;
    }
    
    public short getShortValue() {
	return (short) myValue;
    }
    
    public int getIntValue() {
	return myValue;
    }
    
    public long getLongValue() {
	return (long) myValue;
    }
    
    public String toString() {
	return "[UINT16: " + myValue + "]";
    }
}
