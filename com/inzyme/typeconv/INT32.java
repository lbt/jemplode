/* INT32 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.typeconv;
import java.io.IOException;

public class INT32 implements IPrimitive
{
    private int myValue;
    
    public INT32() {
	/* empty */
    }
    
    public INT32(int _value) {
	myValue = _value;
    }
    
    public void setValue(int _value) {
	myValue = _value;
    }
    
    public long getValue() {
	return (long) myValue;
    }
    
    public void write(LittleEndianOutputStream _outputStream)
	throws IOException {
	_outputStream.writeSigned32(myValue);
    }
    
    public void updateCRC(CRC16 _crc) {
	_crc.updateSigned32(myValue);
    }
    
    public void read(LittleEndianInputStream _inputStream) throws IOException {
	myValue = _inputStream.readSigned32();
    }
    
    public int getLength() {
	return 4;
    }
    
    public boolean equals(Object _obj) {
	boolean equals = (_obj != null && _obj instanceof INT32
			  && ((INT32) _obj).myValue == myValue);
	return equals;
    }
    
    public String toString() {
	return "[INT32: " + myValue + "]";
    }
}
