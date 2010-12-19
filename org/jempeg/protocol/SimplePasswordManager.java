/* SimplePasswordManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;

public class SimplePasswordManager implements IPasswordManager
{
    private String myPassword;
    
    public SimplePasswordManager(String _password) {
	myPassword = _password;
    }
    
    public SimplePasswordManager() {
	/* empty */
    }
    
    public void setPassword(String _password, boolean _persistent) {
	myPassword = _password;
    }
    
    public String getPassword() {
	return myPassword;
    }
}
