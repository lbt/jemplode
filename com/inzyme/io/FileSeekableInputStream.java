/* FileSeekableInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileSeekableInputStream extends SeekableInputStream
{
    private RandomAccessFile myRAF;
    
    public FileSeekableInputStream(File _file) throws IOException {
	this(new RandomAccessFile(_file, "r"));
    }
    
    public FileSeekableInputStream(RandomAccessFile _raf) {
	myRAF = _raf;
    }
    
    public int available() throws IOException {
	int available = (int) (myRAF.length() - myRAF.getFilePointer());
	return available;
    }
    
    public int read() throws IOException {
	int ch = myRAF.read();
	return ch;
    }
    
    public int read(byte[] _bytes) throws IOException {
	int count = myRAF.read(_bytes);
	return count;
    }
    
    public int read(byte[] _bytes, int _offset, int _length)
	throws IOException {
	int count = myRAF.read(_bytes, _offset, _length);
	return count;
    }
    
    public long skip(long _length) throws IOException {
	int count = myRAF.skipBytes((int) _length);
	return (long) count;
    }
    
    public void reset() throws IOException {
	myRAF.seek(0L);
    }
    
    public boolean markSupported() {
	return false;
    }
    
    public void close() throws IOException {
	myRAF.close();
    }
    
    public long tell() throws IOException {
	long position = myRAF.getFilePointer();
	return position;
    }
    
    public void seek(long _position) throws IOException {
	myRAF.seek(_position);
    }
    
    public long length() throws IOException {
	long length = myRAF.length();
	return length;
    }
}
