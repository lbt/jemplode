/* UnsynchronizedInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.io;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UnsynchronizedInputStream extends FilterInputStream
{
    private boolean skipIfNull = false;
    
    public UnsynchronizedInputStream(InputStream in) {
	super(in);
    }
    
    public int read() throws IOException {
	int b = in.read();
	if (skipIfNull && b == 0)
	    b = in.read();
	skipIfNull = b == 255;
	return b;
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
	int readBytes = in.read(b, off, len);
	if (readBytes < 1)
	    return readBytes;
	int o = off;
	for (int i = off; i < off + readBytes; i++) {
	    if (b[i] == 0 && skipIfNull)
		skipIfNull = false;
	    else {
		skipIfNull = b[i] == -1;
		b[o++] = b[i];
	    }
	}
	if (o == off + readBytes || readBytes < len)
	    return o - off;
	int reread = read(b, o, len - (o - off));
	if (reread > 0)
	    return o + reread - off;
	return o - off;
    }
    
    public long skip(long n) throws IOException {
	int MB = 1048576;
	if (n > 1048576L) {
	    long skippedBytes = 0L;
	    byte[] skipBuf = new byte[1048576];
	    for (/**/; skippedBytes + 1048576L < n;
		 skippedBytes += (long) this.read(skipBuf)) {
		/* empty */
	    }
	    int lastSkip = read(skipBuf, 0, (int) (n - skippedBytes));
	    if (lastSkip > 0)
		return skippedBytes + (long) lastSkip;
	    return skippedBytes;
	}
	byte[] skipBuf = new byte[(int) n];
	return (long) this.read(skipBuf);
    }
}
