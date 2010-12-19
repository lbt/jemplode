package com.inzyme.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * CounterOutputStream keeps a counter of how many bytes have been written to of
 * this stream.
 * 
 * @author Mike Schrag
 */
public class CounterOutputStream extends FilterOutputStream {
	private long myCounter;
	
	/**
	 * Constructor for CounterOutputStream.
	 * 
	 * @param _out the OutputStream to proxy
	 */
	public CounterOutputStream(OutputStream _out) {
		super(_out);
	}
	
	/**
	 * Returns the number of bytes that have been written to this stream.
	 * 
	 * @return long the number of bytes that have been written to this stream
	 */
	public long getCounter() {
		return myCounter;
	}
	
	/**
	 * @see java.io.FilterOutputStream#write(byte)
	 */
	public void write(byte[] b) throws IOException {
		out.write(b);
		myCounter += b.length;
	}
	
	/**
	 * @see java.io.FilterOutputStream#write(byte, int, int)
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
		myCounter += len;
	}
	
	/**
	 * @see java.io.FilterOutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		out.write(b);
		myCounter ++;
	}
}
