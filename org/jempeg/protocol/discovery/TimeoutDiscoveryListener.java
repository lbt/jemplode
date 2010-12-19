/* TimeoutDiscoveryListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol.discovery;
import com.inzyme.util.Timer;

public class TimeoutDiscoveryListener implements IDiscoveryListener
{
    private IDiscoverer myDiscoverer;
    private Timer myTimer;
    
    public TimeoutDiscoveryListener(IDiscoverer _discoverer, int _timeout) {
	myDiscoverer = _discoverer;
	myTimer = new Timer(_timeout, this, "wakeup");
	myTimer.mark();
    }
    
    public void wakeup() {
	myDiscoverer.stopDiscovery();
    }
    
    public void deviceDiscovered(IDiscoverer _discoverer, IDevice _device) {
	myTimer.mark();
    }
}
