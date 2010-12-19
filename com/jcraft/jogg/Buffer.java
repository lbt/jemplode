/* Buffer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jogg;

public class Buffer
{
    private static final int BUFFER_INCREMENT = 256;
    private static final int[] mask
	= { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191,
	    16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151,
	    4194303, 8388607, 16777215, 33554431, 67108863, 134217727,
	    268435455, 536870911, 1073741823, 2147483647, -1 };
    int ptr = 0;
    byte[] buffer = null;
    int endbit = 0;
    int endbyte = 0;
    int storage = 0;
    
    public void writeinit() {
	buffer = new byte[256];
	ptr = 0;
	buffer[0] = (byte) 0;
	storage = 256;
    }
    
    public void write(byte[] s) {
	for (int i = 0; i < s.length; i++) {
	    if (s[i] == 0)
		break;
	    write(s[i], 8);
	}
    }
    
    public void read(byte[] s, int bytes) {
	int i = 0;
	while (bytes-- != 0)
	    s[i++] = (byte) read(8);
    }
    
    void reset() {
	ptr = 0;
	buffer[0] = (byte) 0;
	endbit = endbyte = 0;
    }
    
    public void writeclear() {
	buffer = null;
    }
    
    public void readinit(byte[] buf, int bytes) {
	readinit(buf, 0, bytes);
    }
    
    public void readinit(byte[] buf, int start, int bytes) {
	ptr = start;
	buffer = buf;
	endbit = endbyte = 0;
	storage = bytes;
    }
    
    public void write(int value, int bits) {
	if (endbyte + 4 >= storage) {
	    byte[] foo = new byte[storage + 256];
	    System.arraycopy(buffer, 0, foo, 0, storage);
	    buffer = foo;
	    storage += 256;
	}
	value &= mask[bits];
	bits += endbit;
	buffer[ptr] |= (byte) (value << endbit);
	if (bits >= 8) {
	    buffer[ptr + 1] = (byte) (value >>> 8 - endbit);
	    if (bits >= 16) {
		buffer[ptr + 2] = (byte) (value >>> 16 - endbit);
		if (bits >= 24) {
		    buffer[ptr + 3] = (byte) (value >>> 24 - endbit);
		    if (bits >= 32) {
			if (endbit > 0)
			    buffer[ptr + 4] = (byte) (value >>> 32 - endbit);
			else
			    buffer[ptr + 4] = (byte) 0;
		    }
		}
	    }
	}
	endbyte += bits / 8;
	ptr += bits / 8;
	endbit = bits & 0x7;
    }
    
    public int look(int bits) {
	int m = mask[bits];
	bits += endbit;
	if (endbyte + 4 >= storage && endbyte + (bits - 1) / 8 >= storage)
	    return -1;
	int ret = (buffer[ptr] & 0xff) >>> endbit;
	if (bits > 8) {
	    ret |= (buffer[ptr + 1] & 0xff) << 8 - endbit;
	    if (bits > 16) {
		ret |= (buffer[ptr + 2] & 0xff) << 16 - endbit;
		if (bits > 24) {
		    ret |= (buffer[ptr + 3] & 0xff) << 24 - endbit;
		    if (bits > 32 && endbit != 0)
			ret |= (buffer[ptr + 4] & 0xff) << 32 - endbit;
		}
	    }
	}
	return m & ret;
    }
    
    public int look1() {
	if (endbyte >= storage)
	    return -1;
	return buffer[ptr] >> endbit & 0x1;
    }
    
    public void adv(int bits) {
	bits += endbit;
	ptr += bits / 8;
	endbyte += bits / 8;
	endbit = bits & 0x7;
    }
    
    public void adv1() {
	endbit++;
	if (endbit > 7) {
	    endbit = 0;
	    ptr++;
	    endbyte++;
	}
    }
    
    public int read(int bits) {
	int m = mask[bits];
	bits += endbit;
	if (endbyte + 4 >= storage) {
	    int ret = -1;
	    if (endbyte + (bits - 1) / 8 >= storage) {
		ptr += bits / 8;
		endbyte += bits / 8;
		endbit = bits & 0x7;
		return ret;
	    }
	}
	int ret = (buffer[ptr] & 0xff) >>> endbit;
	if (bits > 8) {
	    ret |= (buffer[ptr + 1] & 0xff) << 8 - endbit;
	    if (bits > 16) {
		ret |= (buffer[ptr + 2] & 0xff) << 16 - endbit;
		if (bits > 24) {
		    ret |= (buffer[ptr + 3] & 0xff) << 24 - endbit;
		    if (bits > 32 && endbit != 0)
			ret |= (buffer[ptr + 4] & 0xff) << 32 - endbit;
		}
	    }
	}
	ret &= m;
	ptr += bits / 8;
	endbyte += bits / 8;
	endbit = bits & 0x7;
	return ret;
    }
    
    public int read1() {
	if (endbyte >= storage) {
	    int ret = -1;
	    endbit++;
	    if (endbit > 7) {
		endbit = 0;
		ptr++;
		endbyte++;
	    }
	    return ret;
	}
	int ret = buffer[ptr] >> endbit & 0x1;
	endbit++;
	if (endbit > 7) {
	    endbit = 0;
	    ptr++;
	    endbyte++;
	}
	return ret;
    }
    
    public int bytes() {
	return endbyte + (endbit + 7) / 8;
    }
    
    public int bits() {
	return endbyte * 8 + endbit;
    }
    
    public byte[] buffer() {
	return buffer;
    }
    
    public static int ilog(int v) {
	int ret = 0;
	for (/**/; v > 0; v >>>= 1)
	    ret++;
	return ret;
    }
    
    public static void report(String in) {
	System.err.println(in);
	System.exit(1);
    }
}
