/* Pinger - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import com.inzyme.util.Debug;

public class Pinger
{
    private static final long TIMEOUT = 20000L;
    private EmpegProtocolClient myProtocolClient;
    private long myLastPing;
    
    public Pinger(EmpegProtocolClient _protocolClient) {
	myProtocolClient = _protocolClient;
	myLastPing = System.currentTimeMillis();
    }
    
    public void pingIfNecessary() {
	if (System.currentTimeMillis() - myLastPing > 20000L) {
	    Debug.println(4, ("Pinger.pingIfNecessary: Pinging "
			      + myProtocolClient.getConnection() + " ..."));
	    myProtocolClient.isDeviceConnected();
	    myLastPing = System.currentTimeMillis();
	}
    }
}
