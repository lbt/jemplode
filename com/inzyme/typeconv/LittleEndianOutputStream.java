/* LittleEndianOutputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.typeconv;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class LittleEndianOutputStream extends OutputStream
{
    private OutputStream myOS;
    private byte[] myBuf16 = new byte[2];
    private byte[] myBuf32 = new byte[4];
    private byte[] myBuf64;
    
    public LittleEndianOutputStream(OutputStream _os) {
	myOS = _os;
    }
    
    public void write(byte[] _buffer, int _offset, int _length)
	throws IOException {
	myOS.write(_buffer, _offset, _length);
    }
    
    public void write(byte[] _array) throws IOException {
	myOS.write(_array);
    }
    
    public void write(int _value) throws IOException {
	myOS.write(_value);
    }
    
    public void write(String _str, String _encoding) throws IOException {
	byte[] bytes;
	try {
	    bytes = _str.getBytes(_encoding);
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	    bytes = _str.getBytes();
	}
	write(bytes);
    }
    
    public void writeNullTerminated(String _str, String _encoding)
	throws IOException {
	byte[] bytes;
	try {
	    bytes = _str.getBytes(_encoding);
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	    bytes = _str.getBytes();
	}
	write(bytes);
	write(0);
    }
    
    public void writeSigned8(int _value) throws IOException {
	myOS.write(_value);
    }
    
    public void writeSigned16(short _value) throws IOException {
	LittleEndianUtils.toSigned16Array(myBuf16, _value);
	myOS.write(myBuf16[0]);
	myOS.write(myBuf16[1]);
    }
    
    public void writeSigned32(int _value) throws IOException {
	LittleEndianUtils.toSigned32Array(myBuf32, (long) _value);
	myOS.write(myBuf32[0]);
	myOS.write(myBuf32[1]);
	myOS.write(myBuf32[2]);
	myOS.write(myBuf32[3]);
    }
    
    public void writeUnsigned8(int _value) throws IOException {
	myOS.write(TypeConversionUtils.toUnsigned8(_value));
    }
    
    public void writeUnsigned16(int _value) throws IOException {
	LittleEndianUtils.toUnsigned16Array(myBuf16, _value);
	myOS.write(myBuf16[0]);
	myOS.write(myBuf16[1]);
    }
    
    public void writeUnsigned24(int _value) throws IOException {
	LittleEndianUtils.toUnsigned24Array(myBuf32, (long) _value);
	myOS.write(myBuf32[0]);
	myOS.write(myBuf32[1]);
	myOS.write(myBuf32[2]);
    }
    
    public void writeUnsigned32(long _value) throws IOException {
	LittleEndianUtils.toUnsigned32Array(myBuf32, _value);
	myOS.write(myBuf32[0]);
	myOS.write(myBuf32[1]);
	myOS.write(myBuf32[2]);
	myOS.write(myBuf32[3]);
    }
    
    public void writeUnsigned64(long _value) throws IOException {
	if (myBuf64 == null)
	    myBuf64 = new byte[8];
	LittleEndianUtils.toUnsigned64Array(myBuf64, _value);
	myOS.write(myBuf64[0]);
	myOS.write(myBuf64[1]);
	myOS.write(myBuf64[2]);
	myOS.write(myBuf64[3]);
	myOS.write(myBuf64[4]);
	myOS.write(myBuf64[5]);
	myOS.write(myBuf64[6]);
	myOS.write(myBuf64[7]);
    }
    
    public void writeFloat(float _value) throws IOException {
	LittleEndianUtils
	    .toSigned32Array(myBuf32, (long) Float.floatToRawIntBits(_value));
	myOS.write(myBuf32[3]);
	myOS.write(myBuf32[2]);
	myOS.write(myBuf32[1]);
	myOS.write(myBuf32[0]);
    }
    
    public void flush() throws IOException {
	myOS.flush();
    }
    
    public void close() throws IOException {
	myOS.close();
    }
}
