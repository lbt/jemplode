/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package com.inzyme.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
* FileSeekableInputStream is a an implementation
* of SeekableInputStream on a File.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class FileSeekableInputStream extends SeekableInputStream {
  private RandomAccessFile myRAF;

  /**
  * Constructs a new FileSeekableInputStream.
  *
  * @param _file the File to proxy
  */
  public FileSeekableInputStream(File _file) throws IOException {
    this(new RandomAccessFile(_file, "r"));
  }

  /**
  * Constructs a new FileSeekableInputStream.
  *
  * @param _raf the RandomAccessFile to proxy
  */
  public FileSeekableInputStream(RandomAccessFile _raf) {
    myRAF = _raf;
  }

  public int available() throws IOException {
    int available = (int)(myRAF.length() - myRAF.getFilePointer());
    return available;
  }

  public int read() throws IOException {
    int ch = myRAF.read();
    return ch;
  }

  public int read(byte[] _bytes) throws IOException {
    int count = myRAF.read(_bytes);
    return count;
  }

  public int read(byte[] _bytes, int _offset, int _length) throws IOException {
    int count = myRAF.read(_bytes, _offset, _length);
    return count;
  }

  public long skip(long _length) throws IOException {
    int count = myRAF.skipBytes((int)_length);
    return count;
  }

  public void reset() throws IOException {
    myRAF.seek(0);
  }

  public boolean markSupported() {
    return false;
  }

  public void close() throws IOException {
    myRAF.close();
  }

  /**
  * Returns the current position in the stream.
  *
  * @returns the current position in the stream
  */
  public long tell() throws IOException {
    long position = myRAF.getFilePointer();
    return position;
  }

  /**
  * Seeks to the given position in the stream.
  *
  * @param _position the position to seek to (absolute)
  */
  public void seek(long _position) throws IOException {
    myRAF.seek(_position);
  }

  /**
  * Returns the length of this stream.
  *
  * @returns the length of this stream
  */
  public long length() throws IOException {
    long length = myRAF.length();
    return length;
  }
}
