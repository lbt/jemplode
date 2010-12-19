/* AccumulatingDiscoveryListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol.discovery;
import java.util.Vector;

public class AccumulatingDiscoveryListener implements IDiscoveryListener
{
    private Vector myDevicesFound = new Vector();
    
    public IDevice[] getDevices() {
	IDevice[] devices = new IDevice[myDevicesFound.size()];
	myDevicesFound.copyInto(devices);
	return devices;
    }
    
    public void deviceDiscovered(IDiscoverer _discoverer, IDevice _device) {
	if (!myDevicesFound.contains(_device))
	    myDevicesFound.addElement(_device);
    }
}
