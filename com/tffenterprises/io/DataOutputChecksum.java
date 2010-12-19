/* DataOutputChecksum - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.io;
import java.io.DataOutput;
import java.io.UnsupportedEncodingException;
import java.util.zip.Checksum;

public class DataOutputChecksum implements DataOutput, Checksum
{
    private byte[] buf;
    private Checksum cs;
    
    private DataOutputChecksum() {
	buf = new byte[8];
	cs = null;
    }
    
    public DataOutputChecksum(Checksum checksum) {
	buf = new byte[8];
	cs = null;
	cs = checksum;
    }
    
    public final void update(byte[] b) {
	update(b, 0, b.length);
    }
    
    public void update(byte[] b, int off, int len) {
	cs.update(b, off, len);
    }
    
    public void update(int b) {
	cs.update(b);
    }
    
    public long getValue() {
	return cs.getValue();
    }
    
    public void reset() {
	cs.reset();
    }
    
    public void write(int b) {
	cs.update(b);
    }
    
    public void write(byte[] b) {
	cs.update(b, 0, b.length);
    }
    
    public void write(byte[] b, int off, int len) {
	cs.update(b, off, len);
    }
    
    public void writeBoolean(boolean bool) {
	if (bool)
	    cs.update(1);
	else
	    cs.update(0);
    }
    
    public void writeByte(int b) {
	cs.update(b);
    }
    
    public void writeShort(int s) {
	buf[0] = (byte) (0xff & s >> 8);
	buf[1] = (byte) (0xff & s);
	cs.update(buf, 0, 2);
    }
    
    public void writeChar(int c) {
	buf[0] = (byte) (0xff & c >> 8);
	buf[1] = (byte) (0xff & c);
	cs.update(buf, 0, 2);
    }
    
    public void writeInt(int i) {
	buf[0] = (byte) (0xff & i >> 24);
	buf[1] = (byte) (0xff & i >> 16);
	buf[2] = (byte) (0xff & i >> 8);
	buf[3] = (byte) (0xff & i);
	cs.update(buf, 0, 4);
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
	cs.update(buf, 0, 8);
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
	    cs.update(bytes, 0, bytes.length);
	else {
	    char[] chars = s.toCharArray();
	    bytes = new byte[chars.length];
	    for (int i = 0; i < chars.length; i++)
		bytes[i] = (byte) (chars[i] & '\u00ff');
	    cs.update(bytes, 0, bytes.length);
	}
    }
    
    public void writeChars(String s) {
	char[] chars = s.toCharArray();
	byte[] bytes = new byte[2 * chars.length];
	for (int i = 0; i < chars.length; i++) {
	    bytes[2 * i] = (byte) (0xff & chars[i] >> 8);
	    bytes[2 * i + 1] = (byte) (0xff & chars[i]);
	}
	cs.update(bytes, 0, bytes.length);
    }
    
    public void writeUTF(String s) {
	int i = 0;
	for (int sl = s.length(); i < sl; i++) {
	    int c = s.charAt(i) & 0xffff;
	    if (c < 128 && c > 0)
		cs.update(c);
	    else if (c < 2048) {
		buf[0] = (byte) (0xc0 | 0x1f & c >> 6);
		buf[1] = (byte) (0x80 | 0x3f & c);
		cs.update(buf, 0, 2);
	    } else {
		buf[0] = (byte) (0xc0 | 0xf & c >> 12);
		buf[1] = (byte) (0x80 | 0x3f & c >> 6);
		buf[2] = (byte) (0x80 | 0x3f & c);
		cs.update(buf, 0, 3);
	    }
	}
    }
    
    public Checksum getChecksum() {
	return cs;
    }
}
