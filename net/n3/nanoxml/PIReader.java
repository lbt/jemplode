/* PIReader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.IOException;
import java.io.Reader;

class PIReader extends Reader
{
    private IXMLReader reader;
    private boolean atEndOfData;
    
    PIReader(IXMLReader reader) {
	this.reader = reader;
	atEndOfData = false;
    }
    
    protected void finalize() throws Throwable {
	reader = null;
	super.finalize();
    }
    
    public int read(char[] buffer, int offset, int size) throws IOException {
	if (atEndOfData)
	    return -1;
	int charsRead = 0;
	if (offset + size > buffer.length)
	    size = buffer.length - offset;
	for (/**/; charsRead < size; charsRead++) {
	    char ch = reader.read();
	    if (ch == '?') {
		char ch2 = reader.read();
		if (ch2 == '>') {
		    atEndOfData = true;
		    break;
		}
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
	    char ch = reader.read();
	    if (ch == '?') {
		char ch2 = reader.read();
		if (ch2 == '>')
		    atEndOfData = true;
	    }
	}
    }
}
