/* BigEndianUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.typeconv;

public class BigEndianUtils
{
    public static final int toUnsigned24(byte a, byte b, byte c) {
	int la = a & 0xff;
	int lb = b & 0xff;
	int lc = c & 0xff;
	return (la << 8 | lb) << 8 | lc;
    }
    
    public static final long toUnsigned32(byte a, byte b, byte c, byte d) {
	long la = (long) a & 0xffL;
	long lb = (long) b & 0xffL;
	long lc = (long) c & 0xffL;
	long ld = (long) d & 0xffL;
	return ((la << 8 | lb) << 8 | lc) << 8 | ld;
    }
}
