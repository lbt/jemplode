package com.inzyme.typeconv;

public class BigEndianUtils {
	public static final int toUnsigned24(byte a, byte b, byte c) {
		int la = ((int) a) & 0xFF;
		int lb = ((int) b) & 0xFF;
		int lc = ((int) c) & 0xFF;
		return (((la << 8) | lb) << 8) | lc;
	}
	
	public static final long toUnsigned32(byte a, byte b, byte c, byte d) {
		long la = ((long) a) & 0xFF;
		long lb = ((long) b) & 0xFF;
		long lc = ((long) c) & 0xFF;
		long ld = ((long) d) & 0xFF;
		return (((((la << 8) | lb) << 8) | lc) << 8) | ld;
	}
}
