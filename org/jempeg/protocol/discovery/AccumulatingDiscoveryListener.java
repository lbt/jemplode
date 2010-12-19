package org.jempeg.protocol.discovery;

import java.util.Vector;

/**
 * Keeps a list of all devices that have been found.
 * 
 * @author Mike Schrag
 */
public class AccumulatingDiscoveryListener implements IDiscoveryListener {
	private Vector myDevicesFound;
	
	public AccumulatingDiscoveryListener() {
		myDevicesFound = new Vector();
	}

	/**
	 * Returns the set of devices that have been discovered up to this point.
	 */
	public IDevice[] getDevices() {
		IDevice[] devices = new IDevice[myDevicesFound.size()];
		myDevicesFound.copyInto(devices);
		return devices;
	}

	public void deviceDiscovered(IDiscoverer _discoverer, IDevice _device) {
		if (!myDevicesFound.contains(_device)) {
			myDevicesFound.addElement(_device);
		}
	}
}
