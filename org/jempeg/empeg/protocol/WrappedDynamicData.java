/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

/**
* A WrappedDynamicData is actually received from
* the player and is used to check version
* information.
*
* @author Mike Schrag
* @version $Revision: 1.5 $
*/
public class WrappedDynamicData {
  private UINT32 myVersion;
  private UINT32 myItemSize;
  private DynamicData myDynamicData;

  /**
  * Constructs a new WrappedDynamicData
  */
  public WrappedDynamicData() {
    myVersion = new UINT32();
    myItemSize = new UINT32();
    myDynamicData = new DynamicData();
  }

	/**
	* Constructs a new WrappedDynamicData.
	*
	* @param _data the DynamicData to wrap
	*/
	public WrappedDynamicData(DynamicData _data) {
		setDynamicData(_data);
	}

  /**
  * Returns the DynamicData that was read.
  *
  * @returns the DynamicData that was read
  */
  public DynamicData getDynamicData() {
    return myDynamicData;
  }

	/**
	* Sets the DynamicData for writing, sets the
	* version to the version of the DynamicData,
	* and the item size to DynamicData.getLength()
	*
	* @param _data the DynamicData for writing
	*/
	public void setDynamicData(DynamicData _data) {
		myVersion = new UINT32(_data.getVersion());
		myItemSize = new UINT32(_data.getLength());
		myDynamicData = _data;
	}

  /**
  * Reads from the given buffer.
  *
  * @param _buffer the buffer to read from
  * @param _offset the offset to read from
  */
  public void read(byte[] _buffer, int _offset) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(_buffer, _offset, _buffer.length - _offset);
    LittleEndianInputStream eis = new LittleEndianInputStream(bais);
    readHeader(eis);
  }

  /**
  * Read from the given EmpegInputStream.
  *
  * @param _eis an EmpegInputStream
  * @throws IOException if the read fails
  */ 
  public void readHeader(LittleEndianInputStream _eis) throws IOException {
    myVersion.read(_eis);
    myItemSize.read(_eis);
  }

  /**
  * Write to the given EmpegOutputStream.
  *
  * @param _eos an EmpegOutputStream
  * @throws IOException if the write fails
  */ 
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

  /**
  * Read the next DynamicData from the given EmpegInputStream
  *
  * @param _eis an EmpegInputStream
  * @throws IOException if the read fails
  */ 
  public DynamicData readNextDynamicData(LittleEndianInputStream _eis) throws IOException {
    DynamicData dynData = null;
    if (myDynamicData.checkVersion(myVersion.getValue())) {
      myDynamicData.read(_eis);
      dynData = myDynamicData;
    } else {
      _eis.skip(myItemSize.getValue());
    }
    return dynData;
  }

  public String toString() {
    return "[WrappedDynamicData: version = " + myVersion + "; itemSize = " + myItemSize + "; dynamicData = " + myDynamicData + "]";
  }
}
