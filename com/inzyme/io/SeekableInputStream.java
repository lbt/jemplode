/* SeekableInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.IOException;
import java.io.InputStream;

public abstract class SeekableInputStream extends InputStream
{
    public abstract long tell() throws IOException;
    
    public abstract void seek(long l) throws IOException;
    
    public abstract long length() throws IOException;
    
    public void readFully(byte[] _buffer, int _pos, int _length)
	throws IOException {
	for (int i = 0; i < _length;
	     i += read(_buffer, _pos + i, _length - i)) {
	    /* empty */
	}
    }
    
    public void readFully(byte[] _buffer) throws IOException {
	readFully(_buffer, 0, _buffer.length);
    }
}
