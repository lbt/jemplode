package org.jempeg.protocol.discovery;

/**
 * For debugging, this prints out the devices as they are discovered.
 * 
 * @author Mike Schrag
 */
public class PrintDiscoveryListener implements IDiscoveryListener {
	public void deviceDiscovered(IDiscoverer _discoverer, IDevice _device) {
		System.out.println("PrintDiscoveryListener.deviceDiscovered: " + _device);
	}
}
