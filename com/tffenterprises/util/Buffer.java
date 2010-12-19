/* Buffer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.util;

public class Buffer
{
    byte[] buf = null;
    int datalength = -1;
    
    public Buffer(int max) {
	buf = new byte[max];
    }
    
    public int getMax() {
	return buf.length;
    }
    
    public byte[] getData() {
	return buf;
    }
    
    public void setData(byte[] data) {
	setData(data, data.length);
    }
    
    public void setData(byte[] data, int length) {
	System.arraycopy(data, 0, buf, 0, length);
	setDataLength(length);
    }
    
    public int getDataLength() {
	return datalength;
    }
    
    public void setDataLength(int length) {
	datalength = length;
    }
}
