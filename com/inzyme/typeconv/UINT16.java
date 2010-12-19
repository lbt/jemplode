/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package com.inzyme.typeconv;

import java.io.IOException;

/**
* Represents an Empeg "UINT16".
*
* @author Mike Schrag
* @version $Revision: 1.2 $
*/
public class UINT16 implements INumericPrimitive {
  private int myValue;

  public UINT16() {
  }

  public UINT16(int _value) {
    myValue = _value;
  }

  public void setValue(int _value) {
    myValue = _value;
  }

  public int getValue() {
    return myValue;
  }

  public byte[] toArray() {
  	byte[] uint16Array = LittleEndianUtils.toUnsigned16Array(myValue);
    return uint16Array;
  }

  public void write(LittleEndianOutputStream _outputStream) throws IOException {
		_outputStream.writeUnsigned16(myValue);
  }

  public void updateCRC(CRC16 _crc) {
		_crc.updateUnsigned16(myValue);
  }

  public void read(LittleEndianInputStream _inputStream) throws IOException {
		myValue = _inputStream.readUnsigned16();
  }

  public int getLength() {
    return 2;
  }

  public boolean equals(Object _obj) {
    boolean equals = ((_obj != null) && (_obj instanceof UINT16) && (((UINT16)_obj).myValue == myValue));
    return equals;
  }

  public int hashCode() {
    return myValue;
  }

	public byte getByteValue() {
		return (byte)myValue;
	}

	public short getShortValue() {
		return (short)myValue;
	}

	public int getIntValue() {
		return (int)myValue;
	}

	public long getLongValue() {
		return (long)myValue;
	}

  public String toString() {
    return "[UINT16: " + myValue + "]";
  }
}
