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
* Represents an Empeg "INT32".
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class INT32 implements IPrimitive {
  private int myValue;

  public INT32() {
  }

  public INT32(int _value) {
    myValue = _value;
  }

  public void setValue(int _value) {
    myValue = _value;
  }

  public long getValue() {
    return myValue;
  }

  public void write(LittleEndianOutputStream _outputStream) throws IOException {
		_outputStream.writeSigned32(myValue);
  }

  public void updateCRC(CRC16 _crc) {
		_crc.updateSigned32(myValue);
  }

  public void read(LittleEndianInputStream _inputStream) throws IOException {
		myValue = _inputStream.readSigned32();
  }

  public int getLength() {
    return 4;
  }

  public boolean equals(Object _obj) {
    boolean equals = ((_obj != null) && (_obj instanceof INT32) && (((INT32)_obj).myValue == myValue));
    return equals;
  }

  public String toString() {
    return "[INT32: " + myValue + "]";
  }
}
