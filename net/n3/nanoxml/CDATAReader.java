/* CDATAReader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.IOException;
import java.io.Reader;

class CDATAReader extends Reader
{
    private IXMLReader reader;
    private char savedChar;
    private boolean atEndOfData;
    
    CDATAReader(IXMLReader reader) {
	this.reader = reader;
	savedChar = '\0';
	atEndOfData = false;
    }
    
    protected void finalize() throws Throwable {
	reader = null;
	super.finalize();
    }
    
    public int read(char[] buffer, int offset, int size) throws IOException {
	int charsRead = 0;
	if (atEndOfData)
	    return -1;
	if (offset + size > buffer.length)
	    size = buffer.length - offset;
	for (/**/; charsRead < size; charsRead++) {
	    char ch = savedChar;
	    if (ch == 0)
		ch = reader.read();
	    else
		savedChar = '\0';
	    if (ch == ']') {
		char ch2 = reader.read();
		if (ch2 == ']') {
		    char ch3 = reader.read();
		    if (ch3 == '>') {
			atEndOfData = true;
			break;
		    }
		    savedChar = ch2;
		    reader.unread(ch3);
		} else
		    reader.unread(ch2);
	    }
	    buffer[charsRead] = ch;
	}
	if (charsRead == 0)
	    charsRead = -1;
	return charsRead;
    }
    
    public void close() throws IOException {
	while (!atEndOfData) {
	    char ch = savedChar;
	    if (ch == 0)
		ch = reader.read();
	    else
		savedChar = '\0';
	    if (ch == ']') {
		char ch2 = reader.read();
		if (ch2 == ']') {
		    char ch3 = reader.read();
		    if (ch3 == '>')
			break;
		    savedChar = ch2;
		    reader.unread(ch3);
		} else
		    reader.unread(ch2);
	    }
	}
	atEndOfData = true;
    }
}
