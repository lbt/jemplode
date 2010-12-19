/* RefByteArrayOutputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class RefByteArrayOutputStream extends OutputStream
{
    protected byte[] buf;
    protected int count;
    
    public RefByteArrayOutputStream() {
	this(32);
    }
    
    public RefByteArrayOutputStream(int size) {
	if (size < 0)
	    throw new IllegalArgumentException("Negative initial size: "
					       + size);
	buf = new byte[size];
    }
    
    public synchronized void write(int b) {
	int newcount = count + 1;
	if (newcount > buf.length) {
	    byte[] newbuf = new byte[Math.max(buf.length << 1, newcount)];
	    System.arraycopy(buf, 0, newbuf, 0, count);
	    buf = newbuf;
	}
	buf[count] = (byte) b;
	count = newcount;
    }
    
    public synchronized void write(byte[] b, int off, int len) {
	if (off < 0 || off > b.length || len < 0 || off + len > b.length
	    || off + len < 0)
	    throw new IndexOutOfBoundsException();
	if (len != 0) {
	    int newcount = count + len;
	    if (newcount > buf.length) {
		byte[] newbuf = new byte[Math.max(buf.length << 1, newcount)];
		System.arraycopy(buf, 0, newbuf, 0, count);
		buf = newbuf;
	    }
	    System.arraycopy(b, off, buf, count, len);
	    count = newcount;
	}
    }
    
    public synchronized void writeTo(OutputStream out) throws IOException {
	out.write(buf, 0, count);
    }
    
    public synchronized void reset() {
	count = 0;
    }
    
    public synchronized byte[] getByteArray() {
	return buf;
    }
    
    public int size() {
	return count;
    }
    
    public String toString() {
	return new String(buf, 0, count);
    }
    
    public String toString(String enc) throws UnsupportedEncodingException {
	return new String(buf, 0, count, enc);
    }
    
    /**
     * @deprecated
     */
    public String toString(int hibyte) {
	return new String(buf, hibyte, 0, count);
    }
    
    public void close() throws IOException {
	/* empty */
    }
}
