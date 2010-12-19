package com.inzyme.typeconv;


public class TypeConversionUtils {
	public static final short toUnsigned8(int _b) {
		return (short)(_b & 0xFF);
	}
}
