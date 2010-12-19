/* StdXMLReader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PushbackInputStream;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

public class StdXMLReader implements IXMLReader
{
    private Stack readers;
    private StackedReader currentReader;
    /*synthetic*/ static Class class$0;
    
    private class StackedReader
    {
	PushbackReader pbReader;
	LineNumberReader lineReader;
	URL systemId;
	String publicId;
    }
    
    public static IXMLReader stringReader(String str) {
	return new StdXMLReader(new StringReader(str));
    }
    
    public static IXMLReader fileReader(String filename)
	throws FileNotFoundException, IOException {
	StdXMLReader r = new StdXMLReader(new FileInputStream(filename));
	r.setSystemID(filename);
	for (int i = 0; i < r.readers.size(); i++) {
	    StackedReader sr = (StackedReader) r.readers.elementAt(i);
	    sr.systemId = r.currentReader.systemId;
	}
	return r;
    }
    
    public StdXMLReader(String publicID, String systemID)
	throws MalformedURLException, FileNotFoundException, IOException {
	URL systemIDasURL = null;
	try {
	    systemIDasURL = new URL(systemID);
	} catch (MalformedURLException e) {
	    systemID = "file:" + systemID;
	    try {
		systemIDasURL = new URL(systemID);
	    } catch (MalformedURLException e2) {
		throw e;
	    }
	}
	currentReader = new StackedReader();
	readers = new Stack();
	Reader reader = openStream(publicID, systemIDasURL.toString());
	currentReader.lineReader = new LineNumberReader(reader);
	currentReader.pbReader
	    = new PushbackReader(currentReader.lineReader, 2);
    }
    
    public StdXMLReader(Reader reader) {
	currentReader = new StackedReader();
	readers = new Stack();
	currentReader.lineReader = new LineNumberReader(reader);
	currentReader.pbReader
	    = new PushbackReader(currentReader.lineReader, 2);
	currentReader.publicId = "";
	try {
	    currentReader.systemId = new URL("file:.");
	} catch (MalformedURLException malformedurlexception) {
	    /* empty */
	}
    }
    
    protected void finalize() throws Throwable {
	currentReader.lineReader = null;
	currentReader.pbReader = null;
	currentReader.systemId = null;
	currentReader.publicId = null;
	currentReader = null;
	readers.clear();
	super.finalize();
    }
    
    protected String getEncoding(String str) {
	if (!str.startsWith("<?xml"))
	    return null;
	int index2;
	for (int index = 5; index < str.length(); index = index2 + 1) {
	    StringBuffer key = new StringBuffer();
	    for (/**/; index < str.length(); index++) {
		if (str.charAt(index) > ' ')
		    break;
	    }
	    for (/**/; index < str.length() && str.charAt(index) >= 'a';
		 index++) {
		if (str.charAt(index) > 'z')
		    break;
		key.append(str.charAt(index));
	    }
	    for (/**/; index < str.length() && str.charAt(index) <= ' ';
		 index++) {
		/* empty */
	    }
	    if (index >= str.length() || str.charAt(index) != '=')
		break;
	    for (/**/; (index < str.length() && str.charAt(index) != '\''
			&& str.charAt(index) != '\"'); index++) {
		/* empty */
	    }
	    if (index >= str.length())
		break;
	    char delimiter = str.charAt(index);
	    index++;
	    index2 = str.indexOf(delimiter, index);
	    if (index2 < 0)
		break;
	    if (key.toString().equals("encoding"))
		return str.substring(index, index2);
	}
	return null;
    }
    
    protected Reader stream2reader
	(InputStream stream, StringBuffer charsRead) throws IOException {
	PushbackInputStream pbstream = new PushbackInputStream(stream);
	int b = pbstream.read();
	switch (b) {
	case 0:
	case 254:
	case 255:
	    pbstream.unread(b);
	    return new InputStreamReader(pbstream, "UTF-16");
	case 239:
	    for (int i = 0; i < 2; i++)
		pbstream.read();
	    return new InputStreamReader(pbstream, "UTF-8");
	case 60: {
	    b = pbstream.read();
	    charsRead.append('<');
	    for (/**/; b > 0 && b != 62; b = pbstream.read())
		charsRead.append((char) b);
	    if (b > 0)
		charsRead.append((char) b);
	    String encoding = getEncoding(charsRead.toString());
	    if (encoding == null)
		return new InputStreamReader(pbstream, "UTF-8");
	    charsRead.setLength(0);
	    try {
		return new InputStreamReader(pbstream, encoding);
	    } catch (UnsupportedEncodingException e) {
		return new InputStreamReader(pbstream, "UTF-8");
	    }
	}
	default:
	    charsRead.append((char) b);
	    return new InputStreamReader(pbstream, "UTF-8");
	}
    }
    
    public StdXMLReader(InputStream stream) throws IOException {
	PushbackInputStream pbstream = new PushbackInputStream(stream);
	StringBuffer charsRead = new StringBuffer();
	Reader reader = stream2reader(stream, charsRead);
	currentReader = new StackedReader();
	readers = new Stack();
	currentReader.lineReader = new LineNumberReader(reader);
	currentReader.pbReader
	    = new PushbackReader(currentReader.lineReader, 2);
	currentReader.publicId = "";
	try {
	    currentReader.systemId = new URL("file:.");
	} catch (MalformedURLException malformedurlexception) {
	    /* empty */
	}
	startNewStream(new StringReader(charsRead.toString()));
    }
    
    public char read() throws IOException {
	int ch;
	for (ch = currentReader.pbReader.read(); ch < 0;
	     ch = currentReader.pbReader.read()) {
	    if (readers.empty())
		throw new IOException("Unexpected EOF");
	    currentReader.pbReader.close();
	    currentReader = (StackedReader) readers.pop();
	}
	return (char) ch;
    }
    
    public boolean atEOFOfCurrentStream() throws IOException {
	int ch = currentReader.pbReader.read();
	if (ch < 0)
	    return true;
	currentReader.pbReader.unread(ch);
	return false;
    }
    
    public boolean atEOF() throws IOException {
	int ch;
	for (ch = currentReader.pbReader.read(); ch < 0;
	     ch = currentReader.pbReader.read()) {
	    if (readers.empty())
		return true;
	    currentReader.pbReader.close();
	    currentReader = (StackedReader) readers.pop();
	}
	currentReader.pbReader.unread(ch);
	return false;
    }
    
    public void unread(char ch) throws IOException {
	currentReader.pbReader.unread(ch);
    }
    
    public Reader openStream(String publicID, String systemID)
	throws MalformedURLException, FileNotFoundException, IOException {
	URL url = new URL(currentReader.systemId, systemID);
	if (url.getRef() != null) {
	    String ref = url.getRef();
	    if (url.getFile().length() > 0) {
		url = new URL(url.getProtocol(), url.getHost(), url.getPort(),
			      url.getFile());
		url = new URL("jar:" + url + '!' + ref);
	    } else {
		Class var_class = class$0;
		if (var_class == null) {
		    Class var_class_0_;
		    try {
			var_class_0_
			    = Class.forName("net.n3.nanoxml.StdXMLReader");
		    } catch (ClassNotFoundException classnotfoundexception) {
			NoClassDefFoundError noclassdeffounderror
			    = new NoClassDefFoundError;
			((UNCONSTRUCTED)noclassdeffounderror)
			    .NoClassDefFoundError
			    (classnotfoundexception.getMessage());
			throw noclassdeffounderror;
		    }
		    var_class = class$0 = var_class_0_;
		}
		url = var_class.getResource(ref);
	    }
	}
	currentReader.publicId = publicID;
	currentReader.systemId = url;
	StringBuffer charsRead = new StringBuffer();
	Reader reader = stream2reader(url.openStream(), charsRead);
	if (charsRead.length() == 0)
	    return reader;
	String charsReadStr = charsRead.toString();
	PushbackReader pbreader
	    = new PushbackReader(reader, charsReadStr.length());
	for (int i = charsReadStr.length() - 1; i >= 0; i--)
	    pbreader.unread(charsReadStr.charAt(i));
	return pbreader;
    }
    
    public void startNewStream(Reader reader) {
	startNewStream(reader, false);
    }
    
    public void startNewStream(Reader reader, boolean isInternalEntity) {
	StackedReader oldReader = currentReader;
	readers.push(currentReader);
	currentReader = new StackedReader();
	if (isInternalEntity) {
	    currentReader.lineReader = null;
	    currentReader.pbReader = new PushbackReader(reader, 2);
	} else {
	    currentReader.lineReader = new LineNumberReader(reader);
	    currentReader.pbReader
		= new PushbackReader(currentReader.lineReader, 2);
	}
	currentReader.systemId = oldReader.systemId;
	currentReader.publicId = oldReader.publicId;
    }
    
    public int getStreamLevel() {
	return readers.size();
    }
    
    public int getLineNr() {
	if (currentReader.lineReader == null) {
	    StackedReader sr = (StackedReader) readers.peek();
	    if (sr.lineReader == null)
		return 0;
	    return sr.lineReader.getLineNumber() + 1;
	}
	return currentReader.lineReader.getLineNumber() + 1;
    }
    
    public void setSystemID(String systemID) throws MalformedURLException {
	currentReader.systemId = new URL(currentReader.systemId, systemID);
    }
    
    public void setPublicID(String publicID) {
	currentReader.publicId = publicID;
    }
    
    public String getSystemID() {
	return currentReader.systemId.toString();
    }
    
    public String getPublicID() {
	return currentReader.publicId;
    }
}
