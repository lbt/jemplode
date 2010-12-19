package com.inzyme.util;

/**
 * A set of assertion methods.
 * 
 * @author Mike Schrag
 */
public class Assertions {
	public static void assertNotEquals(String _name, long _parsed, long _expected) throws IllegalArgumentException {
		if (_parsed == _expected) {
			throw new IllegalArgumentException("Expected " + _name + " != " + _expected + ", but parsed '" + _parsed + "'.");
		}
	}
	
	public static void assertEquals(String _name, long _parsed, long[] _expected) throws IllegalArgumentException {
		boolean matches = false;
		for (int i = 0; !matches && i < _expected.length; i ++) {
			if (_expected[i] == _parsed) {
				matches = true;
			}
		}
		if (!matches) {
			throw new IllegalArgumentException("Expected " + _name + " in [" + _expected + "], but parsed '" + _parsed + "'.");
		}
	}
			
	public static void assertEquals(String _name, long _parsed, long _expected) throws IllegalArgumentException {
		if (_parsed != _expected) {
			throw new IllegalArgumentException("Expected " + _name + " == " + _expected + ", but parsed '" + _parsed + "'.");
		}
	}
	
	public static void assertLessThanOrEqualTo(String _name, long _parsed, long _expected) throws IllegalArgumentException {
		if (_parsed > _expected) {
			throw new IllegalArgumentException("Expected " + _name + " <= " + _expected + ", but parsed '" + _parsed + "'.");
		}
	}
	
	public static void assertGreaterThanOrEqualTo(String _name, long _parsed, long _expected) throws IllegalArgumentException {
		if (_parsed < _expected) {
			throw new IllegalArgumentException("Expected " + _name + " >= " + _expected + ", but parsed '" + _parsed + "'.");
		}
	}
	
	public static void assertGreaterThan(String _name, long _parsed, long _expected) throws IllegalArgumentException {
		if (_parsed <= _expected) {
			throw new IllegalArgumentException("Expected " + _name + " > " + _expected + ", but parsed '" + _parsed + "'.");
		}
	}
	
	public static void assertLessThan(String _name, long _parsed, long _expected) throws IllegalArgumentException {
		if (_parsed >= _expected) {
			throw new IllegalArgumentException("Expected " + _name + " < " + _expected + ", but parsed '" + _parsed + "'.");
		}
	}
}
