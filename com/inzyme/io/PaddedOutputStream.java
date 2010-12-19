/* PaddedOutputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Properties;

import com.inzyme.properties.PropertiesUtils;

public class PaddedOutputStream extends CounterOutputStream
{
    private int myPad;
    
    public PaddedOutputStream(OutputStream _out, int _pad) {
	super(_out);
	myPad = _pad;
    }
    
    public void writeString(String _str) throws IOException {
	byte[] bytes = _str.getBytes("UTF8");
	write(bytes);
	write(0);
    }
    
    public void writeProperties(Properties _props, Hashtable _nameToEncoding,
				boolean _nullTerminated) throws IOException {
	PropertiesUtils.write(_props, this, _nameToEncoding);
	if (_nullTerminated)
	    write(0);
    }
    
    public void pad() throws IOException {
	long counter = getCounter();
	long bytesIntoPad = counter % (long) myPad;
	if (bytesIntoPad > 0L) {
	    long pad = (long) myPad - bytesIntoPad;
	    for (int i = 0; (long) i < pad; i++)
		write(0);
	}
    }
}
