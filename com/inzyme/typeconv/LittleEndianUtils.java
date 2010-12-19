/* LittleEndianUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.typeconv;

public class LittleEndianUtils
{
    public static final byte[] toSigned16Array(int _value) {
	byte[] bytez = new byte[2];
	toSigned16Array(bytez, _value);
	return bytez;
    }
    
    public static final void toSigned16Array(byte[] _bytes, int _value) {
	toUnsigned16Array(_bytes, _value);
    }
    
    public static final byte[] toUnsigned16Array(int _value) {
	byte[] bytez = new byte[2];
	toUnsigned16Array(bytez, _value);
	return bytez;
    }
    
    public static final void toUnsigned16Array(byte[] _bytes, int _value) {
	_bytes[0] = (byte) _value;
	_bytes[1] = (byte) (_value >> 8);
    }
    
    public static final byte[] toUnsigned24Array(long _value) {
	byte[] bytez = new byte[3];
	toUnsigned24Array(bytez, _value);
	return bytez;
    }
    
    public static final void toUnsigned24Array(byte[] _bytes, long _value) {
	_bytes[0] = (byte) (int) _value;
	_bytes[1] = (byte) (int) (_value >> 8);
	_bytes[2] = (byte) (int) (_value >> 16);
    }
    
    public static final byte[] toSigned32Array(long _value) {
	byte[] bytez = new byte[4];
	toSigned32Array(bytez, _value);
	return bytez;
    }
    
    public static final void toSigned32Array(byte[] _bytes, long _value) {
	toUnsigned32Array(_bytes, _value);
    }
    
    public static final byte[] toUnsigned32Array(long _value) {
	byte[] bytez = new byte[4];
	toUnsigned32Array(bytez, _value);
	return bytez;
    }
    
    public static final void toUnsigned32Array(byte[] _bytes, long _value) {
	_bytes[0] = (byte) (int) _value;
	_bytes[1] = (byte) (int) (_value >> 8);
	_bytes[2] = (byte) (int) (_value >> 16);
	_bytes[3] = (byte) (int) (_value >> 24);
    }
    
    public static final byte[] toUnsigned64Array(long _value) {
	byte[] bytez = new byte[8];
	toUnsigned64Array(bytez, _value);
	return bytez;
    }
    
    public static final void toUnsigned64Array(byte[] _bytes, long _value) {
	_bytes[0] = (byte) (int) _value;
	_bytes[1] = (byte) (int) (_value >> 8);
	_bytes[2] = (byte) (int) (_value >> 16);
	_bytes[3] = (byte) (int) (_value >> 24);
	_bytes[4] = (byte) (int) (_value >> 32);
	_bytes[5] = (byte) (int) (_value >> 40);
	_bytes[6] = (byte) (int) (_value >> 48);
	_bytes[7] = (byte) (int) (_value >> 56);
    }
    
    public static final short toSigned16(byte a, byte b) {
	short la = (short) (a & 0xff);
	short lb = (short) (b & 0xff);
	return (short) (lb << 8 | la);
    }
    
    public static final int toUnsigned16(byte a, byte b) {
	int la = a & 0xff;
	int lb = b & 0xff;
	return lb << 8 | la;
    }
    
    public static final int toSigned32(byte a, byte b, byte c, byte d) {
	int la = a & 0xff;
	int lb = b & 0xff;
	int lc = c & 0xff;
	int ld = d & 0xff;
	return ((ld << 8 | lc) << 8 | lb) << 8 | la;
    }
    
    public static final int toUnsigned24(byte a, byte b, byte c) {
	int la = a & 0xff;
	int lb = b & 0xff;
	int lc = c & 0xff;
	return (lc << 8 | lb) << 8 | la;
    }
    
    public static final long toUnsigned32(byte a, byte b, byte c, byte d) {
	long la = (long) a & 0xffL;
	long lb = (long) b & 0xffL;
	long lc = (long) c & 0xffL;
	long ld = (long) d & 0xffL;
	return ((ld << 8 | lc) << 8 | lb) << 8 | la;
    }
    
    public static final long toUnsigned64(byte a, byte b, byte c, byte d,
					  byte e, byte f, byte g, byte h) {
	long la = (long) a & 0xffL;
	long lb = (long) b & 0xffL;
	long lc = (long) c & 0xffL;
	long ld = (long) d & 0xffL;
	long le = (long) e & 0xffL;
	long lf = (long) f & 0xffL;
	long lg = (long) g & 0xffL;
	long lh = (long) h & 0xffL;
	return (((((((lh << 8 | lg) << 8 | lf) << 8 | le) << 8 | ld) << 8
		  | lc) << 8
		 | lb) << 8
		| la);
    }
}
