package org.jempeg.protocol.discovery;

import java.util.Enumeration;
import java.util.Vector;

import com.inzyme.util.Debug;

/**
* CompoundEmpegDiscoverer provides an implementation of Discoverer
* that encapsulates discovery across a set of sub-discoverers.  This proxies 
* events of each of the sub discoverers.
*
* @author Mike Schrag
*/
public class CompoundDiscoverer extends AbstractDiscoverer implements IDiscoveryListener {
	private Vector myDiscovererVec;

	public CompoundDiscoverer() {
		myDiscovererVec = new Vector();
	}
	
	protected void stopDiscovery0() {
		for (int i = 0; i < myDiscovererVec.size(); i ++) {
			IDiscoverer discoverer = (IDiscoverer)myDiscovererVec.elementAt(i);
			discoverer.stopDiscovery();
		}
	}
	
	/**
	* Adds a discoverer to be proxied.
	*
	* @param _discoverer the discoverer to proxy
	*/
	public void addDiscoverer(IDiscoverer _discoverer) {
		myDiscovererVec.addElement(_discoverer);
		_discoverer.addDiscoveryListener(this);
	}
	
	protected void startDiscovery0() {
		Vector threadsVec = new Vector();
		Enumeration enum = myDiscovererVec.elements();
		while (isRunning() && enum.hasMoreElements()) {
			try {
				final IDiscoverer discoverer = (IDiscoverer) enum.nextElement();
				Thread t = new Thread(new Runnable() {
					public void run() {
						discoverer.startDiscovery();
					}
				});
				threadsVec.addElement(t);
				t.start();
			}
			catch (Throwable e) {
				Debug.println(e);
			}
		}
		
		for (int i = 0; i < threadsVec.size(); i ++) {
			Thread t = (Thread)threadsVec.elementAt(i);
			try {
				t.join();
			}
			catch (InterruptedException e) {
				Debug.println(e);
			}
		}
	}
	
	public void deviceDiscovered(IDiscoverer _discoverer, IDevice _device) {
		fireDeviceDiscovered(_device);
	}
}
