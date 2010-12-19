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
* Represents an Empeg "UINT32".
*
* @author Mike Schrag
* @version $Revision: 1.2 $
*/
public class UINT32 implements INumericPrimitive {
  private long myValue;

  public UINT32() {
  }

  public UINT32(long _value) {
    myValue = _value;
  }

  public void setValue(long _value) {
    myValue = _value;
  }

  public long getValue() {
    return myValue;
  }

  public byte[] toArray() {
  	byte[] uint32Array = LittleEndianUtils.toUnsigned32Array(myValue);
    return uint32Array;
  }

  public void write(LittleEndianOutputStream _outputStream) throws IOException {
		_outputStream.writeUnsigned32(myValue);
  }

  public void updateCRC(CRC16 _crc) {
		_crc.updateUnsigned32(myValue);
  }

  public void read(LittleEndianInputStream _inputStream) throws IOException {
		myValue = _inputStream.readUnsigned32();
  }

  public int getLength() {
    return 4;
  }

	public boolean equals(int _value) {
		return myValue == _value;
	}
	
  public boolean equals(Object _obj) {
    boolean equals = ((_obj != null) && (_obj instanceof UINT32) && (((UINT32)_obj).myValue == myValue));
    return equals;
  }

  public int hashCode() {
    return (int)myValue;
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
    return "[UINT32: " + myValue + "]";
  }
}
