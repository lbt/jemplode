/* PasswordAuthentication - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import com.inzyme.util.ReflectionUtils;

public class PasswordAuthentication
{
    private String myPassword;
    private boolean mySavePasswordRequested;
    
    public PasswordAuthentication(String _password,
				  boolean _savePasswordRequested) {
	myPassword = _password;
	mySavePasswordRequested = _savePasswordRequested;
    }
    
    public String getPassword() {
	return myPassword;
    }
    
    public boolean isSavePasswordRequested() {
	return mySavePasswordRequested;
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
