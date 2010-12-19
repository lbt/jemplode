/* FTPException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.enterprisedt.net.ftp;
import java.io.IOException;

public class FTPException extends IOException
{
    private int replyCode = -1;
    
    public FTPException(String msg) {
	super(msg);
    }
    
    public FTPException(String msg, String replyCode) {
	super(msg + " (" + replyCode + ")");
	try {
	    this.replyCode = Integer.parseInt(replyCode);
	} catch (NumberFormatException ex) {
	    this.replyCode = -1;
	}
    }
    
    public int getReplyCode() {
	return replyCode;
    }
}
