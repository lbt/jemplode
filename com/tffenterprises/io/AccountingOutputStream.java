/* AccountingOutputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.io;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AccountingOutputStream extends FilterOutputStream
{
    private long written = 0L;
    
    public AccountingOutputStream(OutputStream out) {
	super(out);
    }
    
    public long written() {
	return written;
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
	out.write(b, off, len);
	written += (long) len;
    }
    
    public void write(int b) throws IOException {
	out.write(b);
	written++;
    }
}
