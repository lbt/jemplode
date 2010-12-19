package com.inzyme.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.properties.PropertiesUtils;

/**
 * PaddedInputStream keeps a count of the number of bytes that have been read
 * off of this stream and allows the user to call pad() when they are done to
 * ensure that the backing stream has been padded to the correct length.
 * 
 * @author Mike Schrag
 */
public class PaddedInputStream extends CounterInputStream {
	private int myPad;
	private boolean myTerminatingZeroFound;
	private Hashtable myStartsWithToEncoding;

	/**
	 * Constructor for PaddedInputStream.
	 * 
	 * @param _in the InputStream to proxy
	 * @param _pad the padding size
	 */
	public PaddedInputStream(InputStream _in, int _pad) {
		super(_in);
		myPad = _pad;
	}
	
	public void addStartsWithToEncodingMap(String _startsWith, String _encoding) {
		try {
			if (myStartsWithToEncoding == null) {
				myStartsWithToEncoding = new Hashtable();
			}
			myStartsWithToEncoding.put(_startsWith.getBytes("UTF-8"), _encoding);
		}
		catch (UnsupportedEncodingException e) {
			throw new ChainedRuntimeException("Unable to convert to UTF-8.", e);
		}
	}

	/**
	 * Reads a null-terminated String off of this InputStream.
	 *  
	 * @return String the String (without null termination)
	 * @throws IOException if the String cannot be read
	 */
	public String readString() throws IOException {
		if (myTerminatingZeroFound) {
			return null;
		}

		RefByteArrayOutputStream baos = new RefByteArrayOutputStream(80);
		boolean done = false;
		int ch;
		do {
			ch = read();
			if (ch == 0) {
				done = true;
			}
			else {
				baos.write(ch);
			}
		}
		while (!done);

		checkForTermination(ch);

		String str = new String(baos.getByteArray(), 0, baos.size(), "UTF8");
		return str;
	}

	/**
	 * Reads a null-terminated or newline terminated String off of this
	 * InputStream.
	 * 
	 * @return String the String (without null or newline termination) 
	 * @throws IOException if the String cannot be read
	 */
	private String readLine() throws IOException {
		if (myTerminatingZeroFound) {
			return null;
		}

		RefByteArrayOutputStream baos = new RefByteArrayOutputStream(80);
		boolean done = false;
		int ch;
		do {
			ch = read();
			if (ch == '\n' || ch == 0 || ch == -1) {
				done = true;
			}
			else {
				baos.write(ch);
			}
		}
		while (!done);

		checkForTermination(ch);

		byte[] strBytes = baos.getByteArray();
		int size = baos.size();
		String str = new String(strBytes, 0, size, getEncoding(strBytes, size));
		return str;
	}
	
	protected String getEncoding(byte[] _bytes, int _size) {
		String encoding = null;
		if (myStartsWithToEncoding == null) {
			encoding = "UTF-8";
		} else {
			Enumeration keys = myStartsWithToEncoding.keys();
			while (encoding == null && keys.hasMoreElements()) {
				byte[] startsWith = (byte[])keys.nextElement();
				
				// this bizarre block is to work around a terrible performance problem
				// with i18n on OS X
				if (_size >= startsWith.length) {
					boolean matches = true;
					for (int i = 0; matches && i < startsWith.length; i ++) {
						matches = (Character.toLowerCase((char)_bytes[i]) == startsWith[i]);
					}
					if (matches) {
						encoding = (String)myStartsWithToEncoding.get(startsWith);
					}
				}
			}
			if (encoding == null) {
				encoding = "UTF-8";
			}
		}
		return encoding;
	}

	/**
	 * Reads a set of key/value pairs off of this InputStream.  This reads until a
	 * null terminator or a blank line.
	 * @return Properties the Properties that were read
	 * @throws IOException if the Properties cannot be read
	 */
	public Properties readProperties() throws IOException {
		if (myTerminatingZeroFound) {
			return null;
		}

		Properties props = new Properties();
		boolean done = false;
		do {
			String line = readLine();
			done = PropertiesUtils.readProperty(props, line);
		}
		while (!done);
		if (!props.keys().hasMoreElements()) {
			props = null;
		}

		return props;
	}

	protected void checkForTermination(int ch) throws IOException {
		if (ch == 0) {
			myTerminatingZeroFound = true;
		}
	}

	/**
	 * Ensures that the backing stream has been padded to the correct length.
	 * 
	 * @throws IOException if the padding cannot be added
	 */
	public void pad() throws IOException {
		if (myPad > 0) {
			long counter = getCounter();
			long bytesIntoPad = (counter % myPad);
			if (bytesIntoPad > 0) {
				long pad = myPad - bytesIntoPad;
				for (int i = 0; i < pad; i++) {
					read();
				}
			}
		}
	}
}
