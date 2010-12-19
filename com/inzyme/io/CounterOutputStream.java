/* CounterOutputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CounterOutputStream extends FilterOutputStream
{
    private long myCounter;
    
    public CounterOutputStream(OutputStream _out) {
	super(_out);
    }
    
    public long getCounter() {
	return myCounter;
    }
    
    public void write(byte[] b) throws IOException {
	out.write(b);
	myCounter += (long) b.length;
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
	out.write(b, off, len);
	myCounter += (long) len;
    }
    
    public void write(int b) throws IOException {
	out.write(b);
	myCounter++;
    }
}
