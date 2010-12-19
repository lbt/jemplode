package com.inzyme.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * LimitedInputStream is a wrapper around another inputstream
 * that makes the wrapped stream appear shorter than it is (so
 * you can just read a segment of the stream).
 * 
 * @author Mike Schrag
 */
public class LimitedInputStream extends InputStream {
	private InputStream myIS;
	private int myPos;
	private long myLength;

	/**
	 * Constructor for LimitedInputStream.
	 */
	public LimitedInputStream(InputStream _is, long _length) {
		myIS = _is;
		myLength = _length;
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		int value;
		if (myPos == myLength) {
			value = -1;
		}
		else {
			value = myIS.read();
			if (value != -1) {
				myPos++;
			}
		}
		return value;
	}

	/**
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte[] _bytes) throws IOException {
		return read(_bytes, 0, _bytes.length);
	}

	/**
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] _bytes, int _offset, int _length) throws IOException {
		int numRead;
		if (myLength == myPos) {
			numRead = -1;
		}
		else {
			long length = Math.min(myLength - myPos, _length);
			numRead = myIS.read(_bytes, _offset, (int)Math.min(length, Integer.MAX_VALUE));
			if (numRead > 0) {
				myPos += numRead;
			}
		}
		return numRead;
	}

	/**
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException {
		int available = ((int)Math.min(myLength, Integer.MAX_VALUE) - myPos);
		return available;
	}

	/**
	 * NoOp.  See actuallyClose().
	 */
	public void close() throws IOException {
	}

	/**
	 * actuallyClose actually closes the backing stream.  We
	 * need this because we are only looking at a view of the stream.  Because
	 * of this, we need to be careful about how we allow users of this 
	 * wrapper to close the backing stream (since they may close the stream 
	 * even though there is a lot more data in the backing stream).
	 */
	public void acutallyClose() throws IOException {
		myIS.close();
	}
}
