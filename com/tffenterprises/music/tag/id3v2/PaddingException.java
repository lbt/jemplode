/* PaddingException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2;

public class PaddingException extends Exception
{
    long nullBytesRead = 0L;
    
    private PaddingException() {
	init(0L);
    }
    
    public PaddingException(long readBytes, String message) {
	super(message);
	init(readBytes);
    }
    
    public PaddingException(long readBytes) {
	init(readBytes);
    }
    
    private void init(long readBytes) {
	nullBytesRead = readBytes;
    }
    
    public long readBytes() {
	return nullBytesRead;
    }
}
