/* UnsynchronizedOutputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.io;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UnsynchronizedOutputStream extends FilterOutputStream
{
    private boolean lastByteFull = false;
    private boolean hasUnsynchronized = false;
    
    public UnsynchronizedOutputStream(OutputStream out) {
	super(out);
    }
    
    public void write(int b) throws IOException {
	if (lastByteFull && ((b & 0xe0) == 224 || b == 0)) {
	    out.write(0);
	    hasUnsynchronized = true;
	}
	out.write(b);
	lastByteFull = (b & 0xff) == 255;
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
	int from = off;
	int to;
	for (to = off; to < off + len; to++) {
	    if (lastByteFull && ((b[to] & 0xe0) == 224 || b[to] == 0)) {
		out.write(b, from, to - from);
		out.write(0);
		hasUnsynchronized = true;
		from = to;
	    }
	    lastByteFull = (b[to] & 0xff) == 255;
	}
	if (from < off + len)
	    out.write(b, from, to - from);
    }
    
    public void flush() throws IOException {
	if (lastByteFull) {
	    lastByteFull = false;
	    out.write(0);
	    hasUnsynchronized = true;
	}
	out.flush();
    }
    
    public boolean hasUnsynchronized() {
	return hasUnsynchronized;
    }
}
