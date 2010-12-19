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
* Represents an Empeg "UINT0". UINT0 is just a stub for where you need a
* NumericPrimitive that takes up no space.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class UINT0 implements INumericPrimitive {
	public UINT0() {
	}

	public int getValue() {
		return 0;
	}

	public short[] toArray() {
		short[] uint0Array = new short[0];
		return uint0Array;
	}

	public void write(LittleEndianOutputStream _outputStream) throws IOException {
	}

	public void updateCRC(CRC16 _crc) {
	}

	public void read(LittleEndianInputStream _inputStream) throws IOException {
	}

	public int getLength() {
		return 0;
	}

	public boolean equals(Object _obj) {
		boolean equals = ((_obj != null) && (_obj instanceof UINT0));
		return equals;
	}

	public int hashCode() {
		return 0;
	}

	public byte getByteValue() {
		return 0;
	}

	public short getShortValue() {
		return 0;
	}

	public int getIntValue() {
		return 0;
	}

	public long getLongValue() {
		return 0;
	}

	public String toString() {
		return "[UINT0]";
	}
}
