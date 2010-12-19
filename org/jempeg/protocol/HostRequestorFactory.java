/* HostRequestorFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;

public class HostRequestorFactory
{
    private static IHostRequestor myInstance;
    
    public static void setInstance(IHostRequestor _hostRequestor) {
	myInstance = _hostRequestor;
    }
    
    public static synchronized IHostRequestor getInstance() {
	if (myInstance == null)
	    setInstance(new DefaultHostRequestor());
	return myInstance;
    }
}
