/* ByteArrayOutputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.io;
import java.io.DataOutput;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;

public class ByteArrayOutputStream extends java.io.ByteArrayOutputStream
    implements AccountingOutput, DataOutput
{
    private byte[] buf;
    
    public ByteArrayOutputStream() {
	buf = new byte[8];
    }
    
    public ByteArrayOutputStream(int size) throws IllegalArgumentException {
	super(size);
	buf = new byte[8];
    }
    
    public void write(byte[] b) {
	super.write(b, 0, b.length);
    }
    
    public void flush() {
	/* empty */
    }
    
    public long written() {
	return (long) size();
    }
    
    public void writeBoolean(boolean bool) {
	if (bool)
	    this.write(1);
	else
	    this.write(0);
    }
    
    public void writeByte(int b) {
	this.write(b);
    }
    
    public void writeShort(int s) {
	buf[0] = (byte) (0xff & s >> 8);
	buf[1] = (byte) (0xff & s);
	this.write(buf, 0, 2);
    }
    
    public void writeChar(int c) {
	buf[0] = (byte) (0xff & c >> 8);
	buf[1] = (byte) (0xff & c);
	this.write(buf, 0, 2);
    }
    
    public void writeInt(int i) {
	buf[0] = (byte) (0xff & i >> 24);
	buf[1] = (byte) (0xff & i >> 16);
	buf[2] = (byte) (0xff & i >> 8);
	buf[3] = (byte) (0xff & i);
	this.write(buf, 0, 4);
    }
    
    public void writeLong(long l) {
	buf[0] = (byte) (int) (0xffL & l >> 56);
	buf[1] = (byte) (int) (0xffL & l >> 48);
	buf[2] = (byte) (int) (0xffL & l >> 40);
	buf[3] = (byte) (int) (0xffL & l >> 32);
	buf[4] = (byte) (int) (0xffL & l >> 24);
	buf[5] = (byte) (int) (0xffL & l >> 16);
	buf[6] = (byte) (int) (0xffL & l >> 8);
	buf[7] = (byte) (int) (0xffL & l);
	this.write(buf, 0, 8);
    }
    
    public void writeFloat(float f) {
	writeInt(Float.floatToIntBits(f));
    }
    
    public void writeDouble(double d) {
	writeLong(Double.doubleToLongBits(d));
    }
    
    public void writeBytes(String s) {
	byte[] bytes;
	try {
	    bytes = s.getBytes("ISO8859_1");
	} catch (UnsupportedEncodingException e) {
	    bytes = null;
	}
	if (bytes != null && bytes.length == s.length())
	    this.write(bytes, 0, bytes.length);
	else {
	    char[] chars = s.toCharArray();
	    bytes = new byte[chars.length];
	    for (int i = 0; i < chars.length; i++)
		bytes[i] = (byte) (chars[i] & '\u00ff');
	    this.write(bytes, 0, bytes.length);
	}
    }
    
    public void writeChars(String s) {
	char[] chars = s.toCharArray();
	byte[] bytes = new byte[2 * chars.length];
	for (int i = 0; i < chars.length; i++) {
	    bytes[2 * i] = (byte) (0xff & chars[i] >> 8);
	    bytes[2 * i + 1] = (byte) (0xff & chars[i]);
	}
	this.write(bytes, 0, bytes.length);
    }
    
    public void writeUTF(String s) throws UTFDataFormatException {
	ByteArrayOutputStream out = new ByteArrayOutputStream(3 * s.length());
	char[] chars = s.toCharArray();
	for (int i = 0; i < chars.length; i++) {
	    int c = chars[i] & 0xffff;
	    if (c < 128 && c > 0)
		out.write(c);
	    else if (c < 2048) {
		buf[0] = (byte) (0xc0 | 0x1f & c >> 6);
		buf[1] = (byte) (0x80 | 0x3f & c);
		out.write(buf, 0, 2);
	    } else {
		buf[0] = (byte) (0xc0 | 0xf & c >> 12);
		buf[1] = (byte) (0x80 | 0x3f & c >> 6);
		buf[2] = (byte) (0x80 | 0x3f & c);
		out.write(buf, 0, 3);
	    }
	}
	out.flush();
	byte[] bytes = out.toByteArray();
	int length = bytes.length;
	if (length > 65535)
	    throw new UTFDataFormatException
		      ("The length of the UTF string is too great! " + length
		       + " bytes " + "is too many bytes.");
	writeShort((short) length & 0xffff);
	this.write(bytes, 0, bytes.length);
    }
}
