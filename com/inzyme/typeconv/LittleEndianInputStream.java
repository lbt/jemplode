/* LittleEndianInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.typeconv;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianInputStream extends InputStream
{
    private byte[] myBuf16 = new byte[2];
    private byte[] myBuf32 = new byte[4];
    private byte[] myBuf64 = new byte[8];
    private InputStream myIS;
    
    public LittleEndianInputStream(InputStream _is) {
	myIS = _is;
    }
    
    public int available() throws IOException {
	return myIS.available();
    }
    
    public long skip(long _skipCount) throws IOException {
	return myIS.skip(_skipCount);
    }
    
    public int read() throws IOException {
	return myIS.read();
    }
    
    public void mark(int _mark) {
	myIS.mark(_mark);
    }
    
    public boolean markSupported() {
	return myIS.markSupported();
    }
    
    public void reset() throws IOException {
	myIS.reset();
    }
    
    public void close() throws IOException {
	myIS.close();
    }
    
    public int read(byte[] _buffer) throws IOException {
	int bytesRead;
	int pos;
	for (pos = 0; pos < _buffer.length; pos += bytesRead) {
	    bytesRead = read(_buffer, pos, _buffer.length - pos);
	    if (bytesRead == -1)
		throw new IOException
			  ("The connection to the device dropped while attemping to read data from it.");
	    if (bytesRead == 0)
		throw new IOException("The connection had no data to read.");
	}
	return pos;
    }
    
    public int read(byte[] _buffer, int _offset, int _length)
	throws IOException {
	return myIS.read(_buffer, _offset, _length);
    }
    
    public byte readSigned8() throws IOException {
	return (byte) read();
    }
    
    public short readUnsigned8() throws IOException {
	return TypeConversionUtils.toUnsigned8((byte) read());
    }
    
    public short readSigned16() throws IOException {
	read(myBuf16);
	return LittleEndianUtils.toSigned16(myBuf16[0], myBuf16[1]);
    }
    
    public int readUnsigned16() throws IOException {
	read(myBuf16);
	return LittleEndianUtils.toUnsigned16(myBuf16[0], myBuf16[1]);
    }
    
    public int readSigned32() throws IOException {
	read(myBuf32);
	return LittleEndianUtils.toSigned32(myBuf32[0], myBuf32[1], myBuf32[2],
					    myBuf32[3]);
    }
    
    public int readUnsigned24() throws IOException {
	read(myBuf32, 0, 3);
	return LittleEndianUtils.toUnsigned24(myBuf32[0], myBuf32[1],
					      myBuf32[2]);
    }
    
    public long readUnsigned32() throws IOException {
	read(myBuf32);
	return LittleEndianUtils.toUnsigned32(myBuf32[0], myBuf32[1],
					      myBuf32[2], myBuf32[3]);
    }
    
    public long readUnsigned64() throws IOException {
	read(myBuf64);
	return LittleEndianUtils.toUnsigned64(myBuf64[0], myBuf64[1],
					      myBuf64[2], myBuf64[3],
					      myBuf64[4], myBuf64[5],
					      myBuf64[6], myBuf64[7]);
    }
    
    public String readUTF16LE(int _length) throws IOException {
	String value;
	if (_length > 0) {
	    byte[] bytes = new byte[_length];
	    read(bytes);
	    value = new String(bytes, 0, _length - 2, "UnicodeLittle");
	} else
	    value = "";
	return value;
    }
}
