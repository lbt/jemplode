/* LimitedInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends InputStream
{
    private InputStream myIS;
    private int myPos;
    private long myLength;
    
    public LimitedInputStream(InputStream _is, long _length) {
	myIS = _is;
	myLength = _length;
    }
    
    public int read() throws IOException {
	int value;
	if ((long) myPos == myLength)
	    value = -1;
	else {
	    value = myIS.read();
	    if (value != -1)
		myPos++;
	}
	return value;
    }
    
    public int read(byte[] _bytes) throws IOException {
	return read(_bytes, 0, _bytes.length);
    }
    
    public int read(byte[] _bytes, int _offset, int _length)
	throws IOException {
	int numRead;
	if (myLength == (long) myPos)
	    numRead = -1;
	else {
	    long length = Math.min(myLength - (long) myPos, (long) _length);
	    numRead = myIS.read(_bytes, _offset,
				(int) Math.min(length, 2147483647L));
	    if (numRead > 0)
		myPos += numRead;
	}
	return numRead;
    }
    
    public int available() throws IOException {
	int available = (int) Math.min(myLength, 2147483647L) - myPos;
	return available;
    }
    
    public void close() throws IOException {
	/* empty */
    }
    
    public void acutallyClose() throws IOException {
	myIS.close();
    }
}
