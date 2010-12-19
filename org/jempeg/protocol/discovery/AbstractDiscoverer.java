/* AbstractDiscoverer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol.discovery;
import java.util.Vector;

public abstract class AbstractDiscoverer implements IDiscoverer
{
    private boolean myRunning;
    private Vector myListeners = new Vector();
    
    protected AbstractDiscoverer() {
	/* empty */
    }
    
    public void addDiscoveryListener(IDiscoveryListener _listener) {
	myListeners.addElement(_listener);
    }
    
    public void removeDiscoveryListener(IDiscoveryListener _listener) {
	myListeners.removeElement(_listener);
    }
    
    public boolean isRunning() {
	return myRunning;
    }
    
    public void stopDiscovery() {
	myRunning = false;
	stopDiscovery0();
    }
    
    protected void fireDeviceDiscovered(IDevice _device) {
	for (int i = myListeners.size() - 1; i >= 0; i--) {
	    IDiscoveryListener listener
		= (IDiscoveryListener) myListeners.elementAt(i);
	    listener.deviceDiscovered(this, _device);
	}
    }
    
    public void startDiscovery() {
	myRunning = true;
	startDiscovery0();
    }
    
    protected void setRunning(boolean _running) {
	myRunning = _running;
    }
    
    protected abstract void stopDiscovery0();
    
    protected abstract void startDiscovery0();
}
