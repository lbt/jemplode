/**
 * Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
 * other contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.inzyme.typeconv;

/**
 * All the C-to-Java type conversion
 * you might ever want to do, packed into
 * one handy class.  It slices, it dices....
 *
 * @author Mike Schrag
 * @version $Revision: 1.2 $
 */
public class LittleEndianUtils {
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
		_bytes[0] = (byte)_value;
		_bytes[1] = (byte)(_value >> 8);
	}

	public static final byte[] toUnsigned24Array(long _value) {
		byte[] bytez = new byte[3];
		toUnsigned24Array(bytez, _value);
		return bytez;
	}

	public static final void toUnsigned24Array(byte[] _bytes, long _value) {
		_bytes[0] = (byte)_value;
		_bytes[1] = (byte)(_value >> 8);
		_bytes[2] = (byte)(_value >> 16);
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
		_bytes[0] = (byte)_value;
		_bytes[1] = (byte)(_value >> 8);
		_bytes[2] = (byte)(_value >> 16);
		_bytes[3] = (byte)(_value >> 24);
	}

	public static final byte[] toUnsigned64Array(long _value) {
		byte[] bytez = new byte[8];
		toUnsigned64Array(bytez, _value);
		return bytez;
	}

	public static final void toUnsigned64Array(byte[] _bytes, long _value) {
		_bytes[0] = (byte)_value;
		_bytes[1] = (byte)(_value >> 8);
		_bytes[2] = (byte)(_value >> 16);
		_bytes[3] = (byte)(_value >> 24);
		_bytes[4] = (byte)(_value >> 32);
		_bytes[5] = (byte)(_value >> 40);
		_bytes[6] = (byte)(_value >> 48);
		_bytes[7] = (byte)(_value >> 56);
	}

	public static final short toSigned16(byte a, byte b) {
		short la = (short) (a & 0xFF);
		short lb = (short) (b & 0xFF);
		return (short) ((lb << 8) | la);
	}

	public static final int toUnsigned16(byte a, byte b) {
		int la = ((int) a) & 0xFF;
		int lb = ((int) b) & 0xFF;
		return (lb << 8) | la;
	}

	public static final int toSigned32(byte a, byte b, byte c, byte d) {
		int la = ((int) a) & 0xFF;
		int lb = ((int) b) & 0xFF;
		int lc = ((int) c) & 0xFF;
		int ld = ((int) d) & 0xFF;
		return (((((ld << 8) | lc) << 8) | lb) << 8) | la;
	}

	public static final int toUnsigned24(byte a, byte b, byte c) {
		int la = ((int) a) & 0xFF;
		int lb = ((int) b) & 0xFF;
		int lc = ((int) c) & 0xFF;
		return (((lc << 8) | lb) << 8) | la;
	}

	public static final long toUnsigned32(byte a, byte b, byte c, byte d) {
		long la = ((long) a) & 0xFF;
		long lb = ((long) b) & 0xFF;
		long lc = ((long) c) & 0xFF;
		long ld = ((long) d) & 0xFF;
		return (((((ld << 8) | lc) << 8) | lb) << 8) | la;
	}

	public static final long toUnsigned64(byte a, byte b, byte c, byte d, byte e, byte f, byte g, byte h) {
		long la = ((long) a) & 0xFF;
		long lb = ((long) b) & 0xFF;
		long lc = ((long) c) & 0xFF;
		long ld = ((long) d) & 0xFF;
		long le = ((long) e) & 0xFF;
		long lf = ((long) f) & 0xFF;
		long lg = ((long) g) & 0xFF;
		long lh = ((long) h) & 0xFF;
		return (((((((((((((lh << 8) | lg) << 8) | lf) << 8) | le) << 8) | ld) << 8) | lc) << 8) | lb) << 8) | la;
	}
}
