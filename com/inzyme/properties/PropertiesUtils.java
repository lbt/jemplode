/* PropertiesUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.properties;
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

public class PropertiesUtils
{
    public static void write(Properties _properties, OutputStream _os,
			     String _encoding) {
	PrintWriter pw;
	try {
	    pw = new PrintWriter(new OutputStreamWriter(_os, _encoding));
	} catch (UnsupportedEncodingException e) {
	    Debug.println(e);
	    pw = new PrintWriter(new OutputStreamWriter(_os));
	}
	write(_properties, pw);
    }
    
    public static void write(Properties _properties, Writer _writer) {
	PrintWriter pw;
	if (_writer instanceof PrintWriter)
	    pw = (PrintWriter) _writer;
	else
	    pw = new PrintWriter(_writer);
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
    
    public static void write(Properties _properties, OutputStream _os,
			     Hashtable _nameToEncoding) {
	try {
	    PrintWriter utf8Writer
		= new PrintWriter(new OutputStreamWriter(_os, "UTF-8"));
	    PrintWriter iso8859Writer = null;
	    Enumeration propNames = _properties.keys();
	    while (propNames.hasMoreElements()) {
		String name = (String) propNames.nextElement();
		PrintWriter pw;
		if (_nameToEncoding == null)
		    pw = utf8Writer;
		else {
		    String encoding = (String) _nameToEncoding.get(name);
		    if (encoding == null)
			pw = utf8Writer;
		    else if ("ISO-8859-1".equals(encoding)) {
			if (iso8859Writer == null)
			    iso8859Writer
				= (new PrintWriter
				   (new OutputStreamWriter(_os, encoding)));
			pw = iso8859Writer;
		    } else if ("UTF-8".equals(encoding))
			pw = utf8Writer;
		    else
			throw new IllegalArgumentException
				  ("Unable to handle encoding type "
				   + encoding);
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
	} catch (UnsupportedEncodingException e) {
	    throw new ChainedRuntimeException("This should never happen.", e);
	}
    }
    
    public static boolean readProperty(Properties _props, String _line) {
	boolean done;
	if (_line == null || _line.length() == 0)
	    done = true;
	else {
	    char firstCh = _line.charAt(0);
	    if (firstCh != '#' && firstCh != ';') {
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
