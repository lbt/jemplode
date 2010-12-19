/**
 * This file is licensed under the GPL.
 *
 * See the LICENSE0 file included in this release, or
 * http://www.opensource.org/licenses/gpl-license.html
 * for the details of the license.
 */
package com.inzyme.typeconv;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.inzyme.text.StringUtils;
import com.inzyme.util.Debug;

/**
 * Represents an Empeg "char[]".
 *
 * @author Mike Schrag
 * @version $Revision: 1.3 $
 */
public class CharArray implements IPrimitive {
	private byte[] myValue;

	public CharArray() {
	}

	public CharArray(int _length) {
		this(new byte[_length]);
	}

	public CharArray(byte[] _value) {
		myValue = _value;
	}

	public CharArray(String _value, int _length) {
		setValue(_value, _length);
	}

	public void setValue(String _value, int _length) {
		byte[] bytez;
		try {
			bytez = _value.getBytes(StringUtils.ISO_8859_1);
		}
		catch (UnsupportedEncodingException e) {
			Debug.println(e);
			bytez = _value.getBytes();
		}
		setLength(_length);
		if (bytez.length >= _length) {
			System.arraycopy(bytez, 0, myValue, 0, myValue.length);
		}
		else {
			System.arraycopy(bytez, 0, myValue, 0, bytez.length);
		}
	}

	public void setValue(byte[] _value) {
		myValue = _value;
	}

	public byte[] getValue() {
		return myValue;
	}

	public String getStringValue(String _encoding) {
		return getStringValue(_encoding, 0);
	}
	
	public String getStringValue(String _encoding, int _offsetFromEnd) {
		int actualLength = StringUtils.getNullTerminatedLength(myValue, _offsetFromEnd);

		String strValue;
		try {
			if (_encoding == StringUtils.DEFAULT_ENCODING) {
				strValue = new String(myValue, 0, actualLength);
			}
			else {
				strValue = new String(myValue, 0, actualLength, _encoding);
			}
		}
		catch (UnsupportedEncodingException e) {
			Debug.println(e);
			strValue = new String(myValue, 0, actualLength);
		}
		return strValue;
	}

	public String getTrimmedStringValue(String _encoding) {
		String strValue = getTrimmedStringValue(_encoding, 0);
		return strValue;
	}
	
	public String getTrimmedStringValue(String _encoding, int _offsetFromEnd) {
		String strValue = getStringValue(_encoding, _offsetFromEnd).trim();
		return strValue;
	}

	public String getNullTerminatedStringValue(String _encoding) {
		int actualLength = StringUtils.getNullTerminatedLength(myValue, 0);
		String strValue;
		try {
			if (_encoding == StringUtils.DEFAULT_ENCODING) {
				strValue = new String(myValue, 0, actualLength);
			}
			else {
				strValue = new String(myValue, 0, actualLength, _encoding);
			}
		}
		catch (UnsupportedEncodingException e) {
			Debug.println(e);
			strValue = new String(myValue, 0, actualLength);
		}
		return strValue;
	}

	public void write(LittleEndianOutputStream _outputStream) throws IOException {
		_outputStream.write(myValue);
	}

	public void updateCRC(CRC16 _crc) {
		_crc.update(myValue);
	}

	public void read(int _length, LittleEndianInputStream _inputStream) throws IOException {
		setLength(_length);
		read(_inputStream);
	}

	public void read(LittleEndianInputStream _inputStream) throws IOException {
		_inputStream.read(myValue);
	}

	public int getLength() {
		return myValue.length;
	}

	public void setLength(int _length) {
		if (myValue == null) {
			myValue = new byte[_length];
		}
		else if (myValue.length != _length) {
			myValue = new byte[_length];
		}
	}

	public boolean equals(Object _obj) {
		boolean equals = (_obj instanceof CharArray);
		if (equals) {
			byte[] otherValue = ((CharArray) _obj).myValue;
			equals = (otherValue == null && myValue == null);
			if (!equals) {
				if (otherValue != null && myValue != null) {
					equals = (otherValue.length == myValue.length);
					if (equals) {
						for (int i = 0; equals && i < myValue.length; i ++) {
							equals = myValue[i] == otherValue[i];
						}
					}
				}
				else {
					equals = false;
				}
			}
		}
		return equals;
	}

	public int hashCode() {
		return myValue.hashCode();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[EmpegCharArray: Length = ");
		sb.append(getLength());
		sb.append("; ");
		for (int i = 0; i < myValue.length; i ++) {
			sb.append(myValue[i]);
			if (Character.isLetterOrDigit((char) myValue[i])) {
				sb.append("(");
				sb.append((char) myValue[i]);
				sb.append(")");
			}
			if (i < myValue.length - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
