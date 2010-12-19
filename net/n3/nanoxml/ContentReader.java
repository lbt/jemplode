/* ContentReader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.IOException;
import java.io.Reader;

class ContentReader extends Reader
{
    private IXMLReader reader;
    private String buffer;
    private int bufferIndex;
    private IXMLEntityResolver resolver;
    
    ContentReader(IXMLReader reader, IXMLEntityResolver resolver,
		  String buffer) {
	this.reader = reader;
	this.resolver = resolver;
	this.buffer = buffer;
	bufferIndex = 0;
    }
    
    protected void finalize() throws Throwable {
	reader = null;
	resolver = null;
	buffer = null;
	super.finalize();
    }
    
    public int read(char[] outputBuffer, int offset, int size)
	throws IOException {
	try {
	    int charsRead = 0;
	    int bufferLength = buffer.length();
	    if (offset + size > outputBuffer.length)
		size = outputBuffer.length - offset;
	    while (charsRead < size) {
		String str = "";
		char ch;
		if (bufferIndex >= bufferLength) {
		    str = XMLUtil.read(reader, '&');
		    ch = str.charAt(0);
		} else {
		    ch = buffer.charAt(bufferIndex);
		    bufferIndex++;
		    outputBuffer[charsRead] = ch;
		    charsRead++;
		    continue;
		}
		if (ch == '<') {
		    reader.unread(ch);
		    break;
		}
		if (ch == '&' && str.length() > 1) {
		    if (str.charAt(1) == '#')
			ch = XMLUtil.processCharLiteral(str);
		    else {
			XMLUtil.processEntity(str, reader, resolver);
			continue;
		    }
		}
		outputBuffer[charsRead] = ch;
		charsRead++;
	    }
	    if (charsRead == 0)
		charsRead = -1;
	    return charsRead;
	} catch (XMLParseException e) {
	    throw new IOException(e.getMessage());
	}
    }
    
    public void close() throws IOException {
	try {
	    int bufferLength = buffer.length();
	while_9_:
	    for (;;) {
		String str = "";
		do {
		    char ch;
		    if (bufferIndex >= bufferLength) {
			str = XMLUtil.read(reader, '&');
			ch = str.charAt(0);
		    } else {
			ch = buffer.charAt(bufferIndex);
			bufferIndex++;
			break;
		    }
		    if (ch == '<') {
			reader.unread(ch);
			break while_9_;
		    }
		    if (ch == '&' && str.length() > 1 && str.charAt(1) != '#')
			XMLUtil.processEntity(str, reader, resolver);
		} while (false);
	    }
	} catch (XMLParseException e) {
	    throw new IOException(e.getMessage());
	}
    }
}
