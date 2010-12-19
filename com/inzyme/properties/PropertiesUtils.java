package com.inzyme.properties;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.util.Debug;

/**
 * Handy dandy utilities for working with Properties.
 * 
 * @author Mike Schrag
 */
public class PropertiesUtils {
	/**
	 * Writes a Properties object to the given stream.  We don't use load and save
	 * because it writes headers onto the stream and we need control over the
	 * character encoding.
	 * 
	 * @param _properties the properties to write
	 * @param _os the OutputStream to write to
	 * @param _encoding the character encoding to write with
	 * @throws IOException if the properties cannot be written
	 */
	public static void write(Properties _properties, OutputStream _os, String _encoding) throws IOException {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new OutputStreamWriter(_os, _encoding));
		}
		catch (UnsupportedEncodingException e) {
			Debug.println(e);
			pw = new PrintWriter(new OutputStreamWriter(_os));
		}
		write(_properties, pw);
	}

	/**
	 * Writes a Properties object to the given stream.  We don't use load and save
	 * because it writes headers onto the stream and we need control over the
	 * character encoding.
	 * 
	 * @param _properties the properties to write
	 * @param _os the OutputStream to write to
	 * @param _encoding the character encoding to write with
	 * @throws IOException if the properties cannot be written
	 */
	public static void write(Properties _properties, Writer _writer) {
		PrintWriter pw;
		if (_writer instanceof PrintWriter) {
			pw = (PrintWriter) _writer;
		}
		else {
			pw = new PrintWriter(_writer);
		}

		Enumeration propNames = _properties.keys();
		while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			String value = _properties.getProperty(name);
			if (value != null && value.length() > 0) {
				pw.print(name);
				pw.print("=");
				pw.print(value);
				pw.print("\n");
			}
		}
		pw.flush();
	}

	/**
	 * Writes a Properties object to the given stream.  We don't use load and save
	 * because it writes headers onto the stream and we need control over the
	 * character encoding.
	 * 
	 * @param _properties the properties to write
	 * @param _os the OutputStream to write to
	 * @param _encoding the character encoding to write with
	 * @throws IOException if the properties cannot be written
	 */
	public static void write(Properties _properties, OutputStream _os, Hashtable _nameToEncoding) {
		try {
			PrintWriter utf8Writer = new PrintWriter(new OutputStreamWriter(_os, "UTF-8"));
			PrintWriter iso8859Writer = null;

			Enumeration propNames = _properties.keys();
			while (propNames.hasMoreElements()) {
				PrintWriter pw;
				String name = (String) propNames.nextElement();
				if (_nameToEncoding == null) {
					pw = utf8Writer;
				}
				else {
					String encoding = (String) _nameToEncoding.get(name);
					if (encoding == null) {
						pw = utf8Writer;
					}
					else if ("ISO-8859-1".equals(encoding)) {
						if (iso8859Writer == null) {
							iso8859Writer = new PrintWriter(new OutputStreamWriter(_os, encoding));
						}
						pw = iso8859Writer;
					}
					else if ("UTF-8".equals(encoding)) {
						pw = utf8Writer;
					}
					else {
						throw new IllegalArgumentException("Unable to handle encoding type " + encoding);
					}
				}
				String value = _properties.getProperty(name);
				if (value != null && value.length() > 0) {
					pw.print(name);
					pw.print("=");
					pw.print(value);
					pw.print("\n");
				}
				pw.flush();
			}
		}
		catch (UnsupportedEncodingException e) {
			throw new ChainedRuntimeException("This should never happen.", e);
		}
	}

	/**
	 * Reads the line into the given Properties object.
	 * 
	 * @param _props the Properties to save into
	 * @param _line the key=value line (or a comment)
	 * @returns whether or not we are done with this properties
	 */
	public static boolean readProperty(Properties _props, String _line) {
		boolean done;
		if (_line == null || _line.length() == 0) {
			done = true;
		}
		else {
			char firstCh = _line.charAt(0);
			if (firstCh == '#' || firstCh == ';') {
				// comment
			}
			else {
				int equalsIndex = _line.indexOf('=');
				if (equalsIndex != -1) {
					String key = _line.substring(0, equalsIndex);
					String value = _line.substring(equalsIndex + 1);
					_props.put(key, value);
				}
			}
			done = false;
		}
		return done;
	}
}
