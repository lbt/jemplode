/* CRCInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

final class CRCInputStream extends FilterInputStream
{
    private CRC32 crc = new CRC32();
    private int byteCount = 0;
    private byte[] byteArray = new byte[0];
    
    public CRCInputStream(InputStream in) {
	super(in);
    }
    
    public long getValue() {
	return crc.getValue();
    }
    
    public void reset() {
	byteCount = 0;
	crc.reset();
    }
    
    public int count() {
	return byteCount;
    }
    
    public int read() throws IOException {
	int x = in.read();
	crc.update(x);
	byteCount++;
	return x;
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
	int x = in.read(b, off, len);
	crc.update(b, off, x);
	byteCount += x;
	return x;
    }
    
    public long skip(long n) throws IOException {
	if ((long) byteArray.length < n)
	    byteArray = new byte[(int) n];
	return (long) read(byteArray, 0, (int) n);
    }
}
