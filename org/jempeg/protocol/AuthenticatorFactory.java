/* AuthenticatorFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;

public class AuthenticatorFactory
{
    private static IAuthenticator myInstance;
    
    public static void setInstance(IAuthenticator _authenticator) {
	myInstance = _authenticator;
    }
    
    public static synchronized IAuthenticator getInstance() {
	if (myInstance == null)
	    setInstance(new DefaultAuthenticator());
	return myInstance;
    }
}
