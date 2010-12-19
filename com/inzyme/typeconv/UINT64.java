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
* Represents an Empeg "UINT64".
*
* @author Mike Schrag
* @version $Revision: 1.2 $
*/
public class UINT64 implements IPrimitive {
  private long myValue;

  public UINT64() {
  }

  public UINT64(long _value) {
    myValue = _value;
  }

  public void setValue(long _value) {
    myValue = _value;
  }

  public long getValue() {
    return myValue;
  }

  public byte[] toArray() {
  	byte[] uint64Array = LittleEndianUtils.toUnsigned64Array(myValue);
    return uint64Array;
  }

  public void write(LittleEndianOutputStream _outputStream) throws IOException {
		_outputStream.writeUnsigned64(myValue);
  }

  public void updateCRC(CRC16 _crc) {
//		_crc.updateUnsigned64(myValue);
  }

  public void read(LittleEndianInputStream _inputStream) throws IOException {
		myValue = _inputStream.readUnsigned64();
  }

  public int getLength() {
    return 4;
  }

  public boolean equals(Object _obj) {
    boolean equals = ((_obj != null) && (_obj instanceof UINT64) && (((UINT64)_obj).myValue == myValue));
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
    return "[UINT64: " + myValue + "]";
  }
}
