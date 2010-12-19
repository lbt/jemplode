/* EmpegProtocolException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import java.io.IOException;

public class EmpegProtocolException extends IOException
{
    private boolean myErrorNumSet;
    private int myErrorNum;
    
    public EmpegProtocolException(String _message, int _errorNum) {
	super(_message);
	myErrorNum = _errorNum;
	myErrorNumSet = true;
    }
    
    public EmpegProtocolException(String _message) {
	super(_message);
    }
    
    public boolean isErrorNumSet() {
	return myErrorNumSet;
    }
    
    public int getErrorNum() {
	return myErrorNum;
    }
}
