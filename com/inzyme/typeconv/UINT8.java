/* UINT8 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.typeconv;
import java.io.IOException;

public class UINT8 implements INumericPrimitive
{
    private short myValue;
    
    public UINT8() {
	/* empty */
    }
    
    public UINT8(short _value) {
	myValue = _value;
    }
    
    public void setValue(short _value) {
	myValue = _value;
    }
    
    public short getValue() {
	return myValue;
    }
    
    public short[] toArray() {
	short[] uint8Array = { TypeConversionUtils.toUnsigned8(myValue) };
	return uint8Array;
    }
    
    public void write(LittleEndianOutputStream _outputStream)
	throws IOException {
	_outputStream.writeUnsigned8(myValue);
    }
    
    public void updateCRC(CRC16 _crc) {
	_crc.updateUnsigned8(myValue);
    }
    
    public void read(LittleEndianInputStream _inputStream) throws IOException {
	myValue = _inputStream.readUnsigned8();
    }
    
    public int getLength() {
	return 1;
    }
    
    public boolean equals(Object _obj) {
	boolean equals = (_obj != null && _obj instanceof UINT8
			  && ((UINT8) _obj).myValue == myValue);
	return equals;
    }
    
    public int hashCode() {
	return myValue;
    }
    
    public byte getByteValue() {
	return (byte) myValue;
    }
    
    public short getShortValue() {
	return myValue;
    }
    
    public int getIntValue() {
	return myValue;
    }
    
    public long getLongValue() {
	return (long) myValue;
    }
    
    public String toString() {
	return "[UINT8: " + myValue + "]";
    }
}
