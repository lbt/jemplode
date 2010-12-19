/* PaddedInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.properties.PropertiesUtils;

public class PaddedInputStream extends CounterInputStream
{
    private int myPad;
    private boolean myTerminatingZeroFound;
    private Hashtable myStartsWithToEncoding;
    
    public PaddedInputStream(InputStream _in, int _pad) {
	super(_in);
	myPad = _pad;
    }
    
    public void addStartsWithToEncodingMap(String _startsWith,
					   String _encoding) {
	try {
	    if (myStartsWithToEncoding == null)
		myStartsWithToEncoding = new Hashtable();
	    myStartsWithToEncoding.put(_startsWith.getBytes("UTF-8"),
				       _encoding);
	} catch (UnsupportedEncodingException e) {
	    throw new ChainedRuntimeException("Unable to convert to UTF-8.",
					      e);
	}
    }
    
    public String readString() throws IOException {
	if (myTerminatingZeroFound)
	    return null;
	RefByteArrayOutputStream baos = new RefByteArrayOutputStream(80);
	boolean done = false;
	int ch;
	do {
	    ch = read();
	    if (ch == 0)
		done = true;
	    else
		baos.write(ch);
	} while (!done);
	checkForTermination(ch);
	String str = new String(baos.getByteArray(), 0, baos.size(), "UTF8");
	return str;
    }
    
    private String readLine() throws IOException {
	if (myTerminatingZeroFound)
	    return null;
	RefByteArrayOutputStream baos = new RefByteArrayOutputStream(80);
	boolean done = false;
	int ch;
	do {
	    ch = read();
	    if (ch == 10 || ch == 0 || ch == -1)
		done = true;
	    else
		baos.write(ch);
	} while (!done);
	checkForTermination(ch);
	byte[] strBytes = baos.getByteArray();
	int size = baos.size();
	String str
	    = new String(strBytes, 0, size, getEncoding(strBytes, size));
	return str;
    }
    
    protected String getEncoding(byte[] _bytes, int _size) {
	String encoding = null;
	if (myStartsWithToEncoding == null)
	    encoding = "UTF-8";
	else {
	    Enumeration keys = myStartsWithToEncoding.keys();
	    while (encoding == null && keys.hasMoreElements()) {
		byte[] startsWith = (byte[]) keys.nextElement();
		if (_size >= startsWith.length) {
		    boolean matches = true;
		    for (int i = 0; matches && i < startsWith.length; i++)
			matches = (Character.toLowerCase((char) _bytes[i])
				   == startsWith[i]);
		    if (matches)
			encoding
			    = (String) myStartsWithToEncoding.get(startsWith);
		}
	    }
	    if (encoding == null)
		encoding = "UTF-8";
	}
	return encoding;
    }
    
    public Properties readProperties() throws IOException {
	if (myTerminatingZeroFound)
	    return null;
	Properties props = new Properties();
	boolean done = false;
	do {
	    String line = readLine();
	    done = PropertiesUtils.readProperty(props, line);
	} while (!done);
	if (!props.keys().hasMoreElements())
	    props = null;
	return props;
    }
    
    protected void checkForTermination(int ch) {
	if (ch == 0)
	    myTerminatingZeroFound = true;
    }
    
    public void pad() throws IOException {
	if (myPad > 0) {
	    long counter = getCounter();
	    long bytesIntoPad = counter % (long) myPad;
	    if (bytesIntoPad > 0L) {
		long pad = (long) myPad - bytesIntoPad;
		for (int i = 0; (long) i < pad; i++)
		    read();
	    }
	}
    }
}
