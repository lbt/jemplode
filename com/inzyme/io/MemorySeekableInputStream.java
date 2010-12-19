/* MemorySeekableInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.IOException;

public class MemorySeekableInputStream extends SeekableInputStream
{
    private byte[] myBytes;
    private int myPosition;
    
    public MemorySeekableInputStream(byte[] _bytes) {
	myBytes = _bytes;
    }
    
    public int available() throws IOException {
	int available = myBytes.length - myPosition;
	return available;
    }
    
    public int read() throws IOException {
	try {
	    int ch = myBytes[myPosition++];
	    return ch;
	} catch (ArrayIndexOutOfBoundsException e) {
	    return -1;
	}
    }
    
    public int read(byte[] _bytes) throws IOException {
	int length = read(_bytes, 0, _bytes.length);
	return length;
    }
    
    public int read(byte[] _bytes, int _offset, int _length)
	throws IOException {
	if (_bytes == null)
	    throw new NullPointerException("Target byte array is null.");
	if (_offset < 0)
	    throw new IndexOutOfBoundsException("Offset " + _offset + " < 0");
	if (_offset + _length > _bytes.length)
	    throw new IndexOutOfBoundsException("Offset " + _offset
						+ " + length " + _length
						+ " > array (" + _bytes.length
						+ ")");
	int length;
	if (_length == 0)
	    length = 0;
	else {
	    int available = available();
	    if (available == 0)
		length = -1;
	    else {
		length = Math.min(available, _length);
		System.arraycopy(myBytes, myPosition, _bytes, _offset, length);
		myPosition += length;
	    }
	}
	return length;
    }
    
    public long skip(long _length) throws IOException {
	long length = 0L;
	if (_length > 0L) {
	    length = Math.min((long) available(), _length);
	    myPosition += length;
	}
	return length;
    }
    
    public void reset() throws IOException {
	myPosition = 0;
    }
    
    public boolean markSupported() {
	return false;
    }
    
    public void close() throws IOException {
	myBytes = null;
    }
    
    public long tell() throws IOException {
	return (long) myPosition;
    }
    
    public void seek(long _position) throws IOException {
	myPosition = (int) _position;
    }
    
    public long length() throws IOException {
	return (long) myBytes.length;
    }
}
