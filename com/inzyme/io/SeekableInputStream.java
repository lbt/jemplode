/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package com.inzyme.io;

import java.io.IOException;
import java.io.InputStream;

/**
* SeekableInputStream is a Stream implementation
* that provides random access to the underlying
* data source.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public abstract class SeekableInputStream extends InputStream {
  /**
  * Returns the current position in the stream.
  *
  * @returns the current position in the stream
  */
  public abstract long tell() throws IOException;

  /**
  * Seeks to the given position in the stream.
  *
  * @param _position the position to seek to (absolute)
  */
  public abstract void seek(long _position) throws IOException;

  /**
  * Returns the length of this stream.
  *
  * @returns the length of this stream
  */
  public abstract long length() throws IOException;

  /**
  * Reads _length bytes into the buffer starting at _pos.
  *
  * @param _bytes the buffer to read into
  * @param _pos the position in the buffer to start reading
  * @param _length the length of the bytes to read
  * @throws IOException if the bytes cannot be read
  */
  public void readFully(byte[] _buffer, int _pos, int _length) throws IOException {
    int i = 0;
    while (i < _length) {
      i += read(_buffer, _pos + i, _length - i);
    }
  }

  /**
  * Reads _buffer.length bytes into the buffer starting at 0.
  *
  * @param _bytes the buffer to read into
  * @throws IOException if the bytes cannot be read
  */
  public void readFully(byte[] _buffer) throws IOException {
    readFully(_buffer, 0, _buffer.length);
  }
}
