package org.jempeg.empeg.protocol.packet;

import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;

public class TCPFastHeader {
  private UINT32 myOperation; // 0 == write FID
  private UINT32 myFID;
  private UINT32 myOffset;
  private UINT32 mySize;
  
  public TCPFastHeader() {
  }
  
  public void setOperation(UINT32 _operation) {
    myOperation = _operation;
  }

  public void setFID(UINT32 _fid) {
    myFID = _fid;
  }

  public void setOffset(UINT32 _offset) {
    myOffset = _offset;
  }

  public void setSize(UINT32 _size) {
    mySize = _size;
  }

  public void write(LittleEndianOutputStream _eos) throws IOException {
    myOperation.write(_eos);
    myFID.write(_eos);
    myOffset.write(_eos);
    mySize.write(_eos);
  }
}
