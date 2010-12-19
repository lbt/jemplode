package com.inzyme.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Properties;

import com.inzyme.properties.PropertiesUtils;

/**
 * PaddedOutputStream keeps a count of the number of bytes that have been
 * written to this stream and allows the user to call pad() when they are done
 * to ensure that the backing stream has been padded to the correct length.
 * 
 * @author Mike Schrag
 */
public class PaddedOutputStream extends CounterOutputStream {
	private int myPad;

	/**
	 * Constructor for PaddedOutputStream.
	 * 
	 * @param _out the OutputStream to proxy
	 * @param _pad the padding size
	 */
	public PaddedOutputStream(OutputStream _out, int _pad) {
		super(_out);
		myPad = _pad;
	}

	/**
	 * Writes a null-terminated string to the stream in UTF8.
	 * 
	 * @param _str the String to write
	 * @throws IOException if the String cannot be written
	 */
	public void writeString(String _str) throws IOException {
		byte[] bytes = _str.getBytes("UTF8");
		write(bytes);
		write(0);
	}

	/**
	 * Writes the given properties to the stream with a null terminator at the
	 * end.
	 * 
	 * @param _props the Properties to write to the stream
	 * @param _nameToEncoding the name to character encoding map
	 * @throws IOException if the Properties cannot be written
	 */
	public void writeProperties(Properties _props, Hashtable _nameToEncoding, boolean _nullTerminated) throws IOException {
		PropertiesUtils.write(_props, this, _nameToEncoding);
		if (_nullTerminated) {
			write(0);
		}
	}

	/**
	 * Ensures that the backing stream is padded to the correct length.
	 * 
	 * @throws IOException if the padding cannot be added
	 */
	public void pad() throws IOException {
		long counter = getCounter();
		long bytesIntoPad = (counter % myPad);
		if (bytesIntoPad > 0) {
			long pad = myPad - bytesIntoPad;
			for (int i = 0; i < pad; i++) {
				write(0);
			}
		}
	}
}
