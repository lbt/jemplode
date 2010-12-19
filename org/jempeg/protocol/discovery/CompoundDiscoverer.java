/* CompoundDiscoverer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol.discovery;
import java.util.Enumeration;
import java.util.Vector;

import com.inzyme.util.Debug;

public class CompoundDiscoverer extends AbstractDiscoverer
    implements IDiscoveryListener
{
    private Vector myDiscovererVec = new Vector();
    
    protected void stopDiscovery0() {
	for (int i = 0; i < myDiscovererVec.size(); i++) {
	    IDiscoverer discoverer
		= (IDiscoverer) myDiscovererVec.elementAt(i);
	    discoverer.stopDiscovery();
	}
    }
    
    public void addDiscoverer(IDiscoverer _discoverer) {
	myDiscovererVec.addElement(_discoverer);
	_discoverer.addDiscoveryListener(this);
    }
    
    protected void startDiscovery0() {
	Vector threadsVec = new Vector();
	Enumeration enum = myDiscovererVec.elements();
	while (isRunning() && enum.hasMoreElements()) {
	    try {
		final IDiscoverer discoverer
		    = (IDiscoverer) enum.nextElement();
		Thread t = new Thread(new Runnable() {
		    public void run() {
			discoverer.startDiscovery();
		    }
		});
		threadsVec.addElement(t);
		t.start();
	    } catch (Throwable e) {
		Debug.println(e);
	    }
	}
	for (int i = 0; i < threadsVec.size(); i++) {
	    Thread t = (Thread) threadsVec.elementAt(i);
	    try {
		t.join();
	    } catch (InterruptedException e) {
		Debug.println(e);
	    }
	}
    }
    
    public void deviceDiscovered(IDiscoverer _discoverer, IDevice _device) {
	fireDeviceDiscovered(_device);
    }
}
