/* AccountingInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.io;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AccountingInputStream extends FilterInputStream
    implements AccountingInput
{
    private long consumed = 0L;
    private long mark = 0L;
    private int readLimit = 0;
    
    public AccountingInputStream(InputStream in) {
	super(in);
    }
    
    public long consumed() {
	return consumed;
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
	int rb = in.read(b, off, len);
	consumed += (long) rb;
	return rb;
    }
    
    public int read() throws IOException {
	int b = in.read();
	consumed++;
	return b;
    }
    
    public long skip(long n) throws IOException {
	long s = in.skip(n);
	consumed += s;
	return s;
    }
    
    public void mark(int readLimit) {
	in.mark(readLimit);
	this.readLimit = readLimit;
	mark = consumed;
    }
    
    public void reset() throws IOException {
	if (consumed - mark > (long) readLimit)
	    throw new IOException
		      ("Attempt to reset a marked stream after having read more than readLimit bytes.");
	in.reset();
	consumed = mark;
    }
}
