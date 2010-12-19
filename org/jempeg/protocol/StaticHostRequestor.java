/* StaticHostRequestor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.io.IOException;
import java.net.InetAddress;

public class StaticHostRequestor implements IHostRequestor
{
    private InetAddress myHostAddress;
    
    public StaticHostRequestor(InetAddress _hostAddress) {
	myHostAddress = _hostAddress;
    }
    
    public InetAddress requestHost(InetAddress _lastKnownAddress)
	throws IOException {
	return myHostAddress == null ? _lastKnownAddress : myHostAddress;
    }
}
