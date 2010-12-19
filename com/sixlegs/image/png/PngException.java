/* PngException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;

class PngException extends IOException
{
    String msg = null;
    
    PngException() {
	/* empty */
    }
    
    PngException(String s) {
	msg = s;
    }
    
    public String toString() {
	return msg;
    }
}
