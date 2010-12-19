/* KeyValueChunk - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

abstract class KeyValueChunk extends Chunk
{
    protected String key;
    protected String value;
    
    KeyValueChunk(int type) {
	super(type);
    }
    
    protected abstract boolean isCompressed();
    
    protected String getEncoding() {
	return "8859_1";
    }
    
    protected void readData() throws IOException {
	key = readKey();
	value = readValue();
    }
    
    protected String readKey() throws IOException {
	String raw_key = in_data.readString();
	if (raw_key.length() > 79)
	    throw new PngExceptionSoft(typeToString(type)
				       + " string too long");
	return repairKey(raw_key);
    }
    
    protected String readValue() throws IOException {
	int L = bytesRemaining();
	byte[] buf = new byte[L];
	in_data.readBytes(buf);
	if (isCompressed()) {
	    byte method = buf[0];
	    if (method != 0)
		throw new PngExceptionSoft("Unrecognized " + typeToString(type)
					   + " compression method: " + method);
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream(L * 3);
	    byte[] tbuf = new byte[512];
	    Inflater inf = new Inflater();
	    inf.reset();
	    inf.setInput(buf, 1, L - 1);
	    try {
		while (!inf.needsInput())
		    bytes.write(tbuf, 0, inf.inflate(tbuf));
	    } catch (DataFormatException e) {
		throw new PngExceptionSoft("Error inflating "
					   + typeToString(type) + " chunk: "
					   + e);
	    }
	    return bytes.toString(getEncoding());
	}
	return new String(buf, 0, L, getEncoding());
    }
    
    static String repairKey(String k) {
	char[] chs = k.toCharArray();
	int i = 0;
	int p = 0;
	int L = chs.length;
    while_17_:
	while (p < L) {
	    char ch = chs[p++];
	    if (Character.isWhitespace(ch)) {
		if (i > 0)
		    chs[i++] = ' ';
		while (Character.isWhitespace(ch = chs[p++])) {
		    if (p == L)
			break while_17_;
		}
	    }
	    chs[i++] = ch;
	}
	if (Character.isWhitespace(chs[i - 1]))
	    i--;
	return new String(chs, 0, i);
    }
}
