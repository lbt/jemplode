/* PrintDiscoveryListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol.discovery;

public class PrintDiscoveryListener implements IDiscoveryListener
{
    public void deviceDiscovered(IDiscoverer _discoverer, IDevice _device) {
	System.out
	    .println("PrintDiscoveryListener.deviceDiscovered: " + _device);
    }
}
