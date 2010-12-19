/* ExDataInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

final class ExDataInputStream extends DataInputStream
{
    public ExDataInputStream(InputStream in) {
	super(in);
    }
    
    public static long unsign(int x) {
	return 0xffffffffL & (long) x;
    }
    
    public long readUnsignedInt() throws IOException {
	return unsign(readInt());
    }
    
    public String readString() throws IOException {
	return readString(-1, "8859_1");
    }
    
    public String readString(String encoding) throws IOException {
	return readString(-1, encoding);
    }
    
    public String readString(int limit) throws IOException {
	return readString(limit, "8859_1");
    }
    
    public String readString(int limit, String encoding) throws IOException {
	ByteArrayOutputStream bytes
	    = new ByteArrayOutputStream(limit < 0 ? 80 : limit);
	for (int i = 0; i != limit; i++) {
	    int b = readByte();
	    if (b == 0)
		break;
	    bytes.write(b);
	}
	return bytes.toString(encoding);
    }
    
    public static double parseFloatingPoint(String token) {
	int st = 0;
	int e1 = Math.max(token.indexOf('e'), token.indexOf('E'));
	double d
	    = Double.valueOf
		  (token.substring(st, e1 < 0 ? token.length() : e1))
		  .doubleValue();
	if (e1 > 0)
	    d *= Math.pow(10.0, Double.valueOf(token.substring(e1 + 1))
				    .doubleValue());
	return d;
    }
    
    public double readFloatingPoint() throws IOException {
	return parseFloatingPoint(readString());
    }
    
    public int readBytes(byte[] b) throws IOException {
	return readBytes(b, 0, b.length);
    }
    
    public int readBytes(byte[] b, int off, int len) throws IOException {
	int result;
	int total;
	for (total = 0; total < len; total += result) {
	    result = in.read(b, off + total, len - total);
	    if (result == -1)
		throw new EOFException();
	}
	return total;
    }
}
