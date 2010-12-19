/* CounterInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CounterInputStream extends FilterInputStream
{
    private long myCounter;
    
    public CounterInputStream(InputStream _is) {
	super(_is);
    }
    
    public InputStream getProxiedInputStream() {
	return in;
    }
    
    public long getCounter() {
	return myCounter;
    }
    
    public int read() throws IOException {
	int read = in.read();
	if (read != -1)
	    myCounter++;
	return read;
    }
    
    public int read(byte[] b) throws IOException {
	int numRead = in.read(b);
	if (numRead > 0)
	    myCounter += (long) numRead;
	return numRead;
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
	int numRead = in.read(b, off, len);
	if (numRead > 0)
	    myCounter += (long) numRead;
	return numRead;
    }
    
    public synchronized void reset() throws IOException {
	super.reset();
	myCounter = 0L;
    }
    
    public long skip(long n) throws IOException {
	long skipped = super.skip(n);
	if (skipped > 0L)
	    myCounter += skipped;
	return skipped;
    }
}
