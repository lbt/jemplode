package com.inzyme.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * CounterInputStream keeps a counter of how many bytes have been read off of
 * this stream.
 * 
 * @author Mike Schrag
 */
public class CounterInputStream extends FilterInputStream {
	private long myCounter;
	
	/**
	 * Constructor for CounterInputStream.
	 * 
	 * @param _is the InputStream to proxy
	 */
	public CounterInputStream(InputStream _is) {
		super(_is);
	}
	
	/**
	 * Returns the InputStream that is currently being proxied.
	 * 
	 * @return InputStream the InputStream that is currently being proxied
	 */
	public InputStream getProxiedInputStream() {
		return in;
	}
	
	/**
	 * Returns the number of bytes that have been read from this stream.
	 * 
	 * @return long the number of bytes that have been read from this stream
	 */
	public long getCounter() {
		return myCounter;
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		int read = in.read();
		if (read != -1) {
			myCounter ++;
		}
		return read;
	}
	
	/**
	 * @see java.io.InputStream#read(byte)
	 */
	public int read(byte[] b) throws IOException {
		int numRead = in.read(b);
		if (numRead > 0) {
			myCounter += numRead;
		}
		return numRead;
	}
	
	/**
	 * @see java.io.InputStream#read(byte, int, int)
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		int numRead = in.read(b, off, len);
		if (numRead > 0) {
			myCounter += numRead;
		}
		return numRead;
	}
	
	/**
	 * @see java.io.InputStream#reset()
	 */
	public synchronized void reset() throws IOException {
		super.reset();
		myCounter = 0;
	}
	
	/**
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		long skipped = super.skip(n);
		if (skipped > 0) {
			myCounter += skipped;
		}
		return skipped;
	}
}
