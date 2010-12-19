package org.jempeg.empeg.protocol;

import java.io.IOException;
import java.io.OutputStream;

class Win32USBOutputStream extends OutputStream {
  private int myNativeConnection;

  public Win32USBOutputStream(int _nativeConnection) {
    myNativeConnection = _nativeConnection;
  }

	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		writeBytes0(b, off, len);
	}
	
	public void flush() throws IOException {
		super.flush();
	}

  public void write(int _b) throws IOException {
    write0(_b);
  }

  private native void write0(int _b);

	private native void writeBytes0(byte[] _bytez, int _offset, int _length);
}

