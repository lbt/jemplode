/* WrappedDynamicData - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class WrappedDynamicData
{
    private UINT32 myVersion;
    private UINT32 myItemSize;
    private DynamicData myDynamicData;
    
    public WrappedDynamicData() {
	myVersion = new UINT32();
	myItemSize = new UINT32();
	myDynamicData = new DynamicData();
    }
    
    public WrappedDynamicData(DynamicData _data) {
	setDynamicData(_data);
    }
    
    public DynamicData getDynamicData() {
	return myDynamicData;
    }
    
    public void setDynamicData(DynamicData _data) {
	myVersion = new UINT32((long) _data.getVersion());
	myItemSize = new UINT32((long) _data.getLength());
	myDynamicData = _data;
    }
    
    public void read(byte[] _buffer, int _offset) throws IOException {
	ByteArrayInputStream bais
	    = new ByteArrayInputStream(_buffer, _offset,
				       _buffer.length - _offset);
	LittleEndianInputStream eis = new LittleEndianInputStream(bais);
	readHeader(eis);
    }
    
    public void readHeader(LittleEndianInputStream _eis) throws IOException {
	myVersion.read(_eis);
	myItemSize.read(_eis);
    }
    
    public void write(LittleEndianOutputStream _eos) throws IOException {
	writeHeader(_eos);
	myDynamicData.write(_eos);
    }
    
    public void writeHeader(LittleEndianOutputStream _eos) throws IOException {
	myVersion.write(_eos);
	myItemSize.write(_eos);
    }
    
    public byte[] toByteArray() throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	LittleEndianOutputStream eos = new LittleEndianOutputStream(baos);
	write(eos);
	eos.close();
	byte[] bytes = baos.toByteArray();
	return bytes;
    }
    
    public DynamicData readNextDynamicData(LittleEndianInputStream _eis)
	throws IOException {
	DynamicData dynData = null;
	if (myDynamicData.checkVersion(myVersion.getValue())) {
	    myDynamicData.read(_eis);
	    dynData = myDynamicData;
	} else
	    _eis.skip(myItemSize.getValue());
	return dynData;
    }
    
    public String toString() {
	return ("[WrappedDynamicData: version = " + myVersion + "; itemSize = "
		+ myItemSize + "; dynamicData = " + myDynamicData + "]");
    }
}
