package org.jempeg.empeg.protocol;

import java.io.IOException;
import java.io.InputStream;

class Win32USBInputStream extends InputStream {
  private int myNativeConnection;

  public Win32USBInputStream(int _nativeConnection) {
    myNativeConnection = _nativeConnection;
  }

  public int read() throws IOException {
    int ch = read0();
    return ch;
  }

  private native int read0();
}

