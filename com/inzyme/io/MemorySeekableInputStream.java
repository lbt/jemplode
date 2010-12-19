/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package com.inzyme.io;

import java.io.IOException;

/**
* MemorySeekableInputStream is a an implementation
* of SeekableInputStream on an array of bytes.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class MemorySeekableInputStream extends SeekableInputStream {
  private byte[] myBytes;
  private int myPosition;

  /**
  * Constructs a new MemorySeekableInputStream.
  *
  * @param _bytes the bytes to proxy
  */
  public MemorySeekableInputStream(byte[] _bytes) {
    myBytes = _bytes;
  }

  public int available() throws IOException {
    int available = (myBytes.length - myPosition);
    return available;
  }

  public int read() throws IOException {
    try {
      int ch = myBytes[myPosition ++];
      return ch;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      return -1;
    }
  }

  public int read(byte[] _bytes) throws IOException {
    int length = read(_bytes, 0, _bytes.length);
    return length;
  }

  public int read(byte[] _bytes, int _offset, int _length) throws IOException {
    int length;
    if (_bytes == null) {
      throw new NullPointerException("Target byte array is null.");
    } else if (_offset < 0) {
      throw new IndexOutOfBoundsException("Offset " + _offset + " < 0");
    } else if ((_offset + _length) > _bytes.length) {
      throw new IndexOutOfBoundsException("Offset " + _offset + " + length " + _length + " > array (" + _bytes.length + ")");
    } else if (_length == 0) {
      length = 0;
    } else {
      int available = available();
      if (available == 0) {
        length = -1;
      } else {
        length = Math.min(available, _length);
        System.arraycopy(myBytes, myPosition, _bytes, _offset, length);
        myPosition += length;
      }
    }
    return length;
  }

  public long skip(long _length) throws IOException {
    long length = 0;
    if (_length > 0) {
      length = Math.min(available(), _length);
      myPosition += length;
    }
    return length;
  }

  public void reset() throws IOException {
    myPosition = 0;
  }

  public boolean markSupported() {
    return false;
  }

  public void close() throws IOException {
    myBytes = null;
  }

  /**
  * Returns the current position in the stream.
  *
  * @returns the current position in the stream
  */
  public long tell() throws IOException {
    return myPosition;
  }

  /**
  * Seeks to the given position in the stream.
  *
  * @param _position the position to seek to (absolute)
  */
  public void seek(long _position) throws IOException {
    myPosition = (int)_position;
  }

  /**
  * Returns the length of this stream.
  *
  * @returns the length of this stream
  */
  public long length() throws IOException {
    return myBytes.length;
  }
}
