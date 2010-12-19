/* ByteArrayInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.io;
import java.io.DataInput;
import java.io.EOFException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

public class ByteArrayInputStream extends InputStream
    implements DataInput, AccountingInput
{
    private byte[] buf;
    private int pos;
    private int last;
    private int first;
    private int mark;
    
    private ByteArrayInputStream() {
	/* empty */
    }
    
    public ByteArrayInputStream(byte[] buf) throws IllegalArgumentException {
	this(buf, 0, buf.length);
    }
    
    public ByteArrayInputStream(byte[] buf, int offset, int length)
	throws IllegalArgumentException {
	if (buf == null)
	    throw new IllegalArgumentException
		      ("null was passed as a byte array to back a ByteArrayInputStream.");
	if (offset + length > buf.length || length < 0)
	    throw new IllegalArgumentException
		      ("Illogical arguments were passed when constructing a ByteArrayInputStream: either "
		       + (offset + length) + " > " + buf.length + ", or "
		       + length + " < " + 0);
	this.buf = buf;
	pos = offset;
	mark = offset;
	first = offset;
	last = offset + length;
    }
    
    public int available() {
	return last - pos;
    }
    
    public long consumed() {
	return (long) (pos - first);
    }
    
    public boolean markSupported() {
	return true;
    }
    
    public void mark(int readLimit) {
	mark = pos;
    }
    
    public void reset() {
	pos = mark;
    }
    
    public void close() {
	pos = last;
	mark = last;
    }
    
    public int read() {
	if (pos < last)
	    return buf[pos++] & 0xff;
	return -1;
    }
    
    public int read(byte[] b) {
	return read(b, 0, b.length);
    }
    
    public int read(byte[] b, int offset, int length) {
	if (pos >= last)
	    return -1;
	int k = length;
	if (k > last - pos)
	    k = last - pos;
	else if (length < 0)
	    throw new IllegalArgumentException
		      ("Cannot copy a negative number of bytes from one array to another.");
	System.arraycopy(buf, pos, b, offset, k);
	pos += k;
	return k;
    }
    
    public long skip(long n) {
	if (pos < last) {
	    int k = (int) n;
	    if (n > (long) (last - pos) || n > 65535L)
		k = last - pos;
	    pos += k;
	    return n;
	}
	return 0L;
    }
    
    public boolean readBoolean() throws EOFException {
	if (last - pos < 1)
	    throw new EOFException("EOF reached in readBoolean()");
	if (buf[pos++] != 0)
	    return true;
	return false;
    }
    
    public final byte readByte() throws EOFException {
	return (byte) readUnsignedByte();
    }
    
    public int readUnsignedByte() throws EOFException {
	if (last - pos < 1) {
	    pos = last;
	    throw new EOFException("EOF reached in readUnsignedByte()");
	}
	return buf[pos++] & 0xff;
    }
    
    public final short readShort() throws EOFException {
	return (short) readUnsignedShort();
    }
    
    public int readUnsignedShort() throws EOFException {
	if (last - pos < 2) {
	    pos = last;
	    throw new EOFException("EOF reached in readUnsignedShort()");
	}
	return (short) ((buf[pos++] & 0xff) << 8 | buf[pos++] & 0xff);
    }
    
    public final char readChar() throws EOFException {
	return (char) readUnsignedShort();
    }
    
    public int readInt() throws EOFException {
	if (last - pos < 4) {
	    pos = last;
	    throw new EOFException("EOF reached in readInt()");
	}
	return ((buf[pos++] & 0xff) << 24 | (buf[pos++] & 0xff) << 16
		| (buf[pos++] & 0xff) << 8 | buf[pos++] & 0xff);
    }
    
    public long readLong() throws EOFException {
	if (last - pos < 8) {
	    pos = last;
	    throw new EOFException("EOF reached in readLong()");
	}
	return ((long) (buf[pos++] & 0xff) << 56
		| (long) (buf[pos++] & 0xff) << 48
		| (long) (buf[pos++] & 0xff) << 40
		| (long) (buf[pos++] & 0xff) << 32
		| (long) (buf[pos++] & 0xff) << 24
		| (long) (buf[pos++] & 0xff) << 16
		| (long) (buf[pos++] & 0xff) << 8
		| (long) (buf[pos++] & 0xff));
    }
    
    public final float readFloat() throws EOFException {
	return Float.intBitsToFloat(readInt());
    }
    
    public final double readDouble() throws EOFException {
	return Double.longBitsToDouble(readLong());
    }
    
    public String readLine() {
	StringBuffer str = new StringBuffer();
	int value = 0;
	while (pos < last) {
	    value = buf[pos++];
	    if (value == 10)
		break;
	    if (value == 13) {
		if (buf[pos] == 10)
		    pos++;
		break;
	    }
	    str.append((char) (value & 0xff));
	}
	return str.toString();
    }
    
    public String readUTF() throws EOFException, UTFDataFormatException {
	int utflength = readUnsignedShort();
	int utflast = pos + utflength;
	if (utflast > last)
	    throw new EOFException("EOF reached in readUTF()");
	StringBuffer str = new StringBuffer();
	for (/**/; pos < utflast; pos++) {
	    try {
		if (buf[pos] != 0 && (buf[pos] & 0x80) == 0)
		    str.append((char) (buf[pos] & 0xff));
		else if ((buf[pos] & 0xe0) == 192
			 && (buf[pos + 1] & 0xc0) == 128) {
		    str.append((char) ((buf[pos] & 0x1f) << 6
				       | buf[pos + 1] & 0x3f));
		    pos++;
		} else if ((buf[pos] & 0xf0) == 224
			   && (buf[pos + 1] & 0xc0) == 128
			   && (buf[pos + 2] & 0xc0) == 128) {
		    str.append((char) ((buf[pos] & 0xf) << 12
				       | (buf[pos + 1] & 0x3f) << 6
				       | buf[pos + 2] & 0x3f));
		    pos++;
		    pos++;
		} else
		    throw new ArrayIndexOutOfBoundsException(pos);
	    } catch (ArrayIndexOutOfBoundsException e) {
		throw new UTFDataFormatException
			  ("Malformed UTF-8 was found at index " + pos
			   + " of the byte " + "array buffer.");
	    }
	}
	return str.toString();
    }
    
    public void readFully(byte[] b) throws EOFException {
	readFully(b, 0, b.length);
    }
    
    public void readFully(byte[] b, int offset, int length)
	throws EOFException {
	int r = read(b, offset, length);
	if (r < length)
	    throw new EOFException("EOF reached in readFully()");
    }
    
    public int skipBytes(int n) {
	return (int) skip((long) n);
    }
}
