package com.inzyme.model;

import java.io.IOException;

import com.inzyme.text.StringUtils;
import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.TypeConversionUtils;
import com.inzyme.typeconv.UINT16;
import com.inzyme.typeconv.UINT32;

public class GUID {
	public UINT32 myData1 = new UINT32();
	public UINT16 myData2 = new UINT16();
	public UINT16 myData3 = new UINT16();
	public CharArray myData4 = new CharArray(8);

	public GUID() {
	}

	public GUID(long l, int w1, int w2, int b1, int b2, int b3, int b4, int b5, int b6, int b7, int b8) {
		myData1.setValue(l);
		myData2.setValue(w1);
		myData3.setValue(w2);
		myData4.setValue(new byte[] {(byte) b1, (byte) b2, (byte) b3, (byte) b4, (byte) b5, (byte) b6, (byte) b7, (byte) b8 });
	}

	public void read(LittleEndianInputStream _is) throws IOException {
		myData1.read(_is);
		myData2.read(_is);
		myData3.read(_is);
		myData4.read(_is);
	}

	public boolean equals(Object _obj) {
		boolean equals = (_obj instanceof GUID);
		if (equals) {
			GUID otherGuid = (GUID) _obj;
			equals = myData1.equals(otherGuid.myData1) && myData2.equals(otherGuid.myData2) && myData3.equals(otherGuid.myData3) && myData4.equals(otherGuid.myData4);
		}
		return equals;
	}

	public int hashCode() {
		return (myData1.hashCode() + myData2.hashCode() + myData3.hashCode() + myData4.hashCode());
	}
	
	public String toGUIDString() {
		byte[] bytez = myData4.getValue();
		StringBuffer st = new StringBuffer();
		st.append("{");
		StringUtils.padAppend(st, myData1.getLongValue(), 8, 16);
		st.append("-");
		StringUtils.padAppend(st, myData2.getLongValue(), 4, 16);
		st.append("-");
		StringUtils.padAppend(st, myData3.getLongValue(), 4, 16);
		st.append("-");
		for (int i = 0; i <= 1; i ++) {
			StringUtils.padAppend(st, TypeConversionUtils.toUnsigned8(bytez[i]), 2, 16);
		}
		st.append("-");
		for (int i = 2; i < bytez.length; i ++) {
			StringUtils.padAppend(st, TypeConversionUtils.toUnsigned8(bytez[i]), 2, 16);
		}
		st.append("}");
		
		return st.toString();
	}
	
	public String toString() {
		return "[GUID: " + toGUIDString() + "]";
	}
}