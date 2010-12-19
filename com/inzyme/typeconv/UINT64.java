/* UINT64 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.typeconv;
import java.io.IOException;

public class UINT64 implements IPrimitive
{
    private long myValue;
    
    public UINT64() {
	/* empty */
    }
    
    public UINT64(long _value) {
	myValue = _value;
    }
    
    public void setValue(long _value) {
	myValue = _value;
    }
    
    public long getValue() {
	return myValue;
    }
    
    public byte[] toArray() {
	byte[] uint64Array = LittleEndianUtils.toUnsigned64Array(myValue);
	return uint64Array;
    }
    
    public void write(LittleEndianOutputStream _outputStream)
	throws IOException {
	_outputStream.writeUnsigned64(myValue);
    }
    
    public void updateCRC(CRC16 _crc) {
	/* empty */
    }
    
    public void read(LittleEndianInputStream _inputStream) throws IOException {
	myValue = _inputStream.readUnsigned64();
    }
    
    public int getLength() {
	return 4;
    }
    
    public boolean equals(Object _obj) {
	boolean equals = (_obj != null && _obj instanceof UINT64
			  && ((UINT64) _obj).myValue == myValue);
	return equals;
    }
    
    public int hashCode() {
	return (int) myValue;
    }
    
    public byte getByteValue() {
	return (byte) (int) myValue;
    }
    
    public short getShortValue() {
	return (short) (int) myValue;
    }
    
    public int getIntValue() {
	return (int) myValue;
    }
    
    public long getLongValue() {
	return myValue;
    }
    
    public String toString() {
	return "[UINT64: " + myValue + "]";
    }
}
