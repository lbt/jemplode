/* StaticAuthenticator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;

public class StaticAuthenticator implements IAuthenticator
{
    private String myPassword;
    
    public StaticAuthenticator(String _password) {
	myPassword = _password;
    }
    
    public PasswordAuthentication requestPassword(String _prompt) {
	return new PasswordAuthentication(myPassword, true);
    }
}
