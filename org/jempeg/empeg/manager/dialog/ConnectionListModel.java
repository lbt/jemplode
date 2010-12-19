package org.jempeg.empeg.manager.dialog;

import java.util.Vector;

import javax.swing.AbstractListModel;

import org.jempeg.protocol.discovery.IDevice;
import org.jempeg.protocol.discovery.IDiscoverer;
import org.jempeg.protocol.discovery.IDiscoveryListener;

import com.inzyme.exception.ExceptionUtils;

/**
 * ConnectionListModel is an AbstractListModel implementation
 * that can display a set of discovered Empeg connections.
 * 
 * @author Mike Schrag
 */
public class ConnectionListModel extends AbstractListModel implements IDiscoveryListener {
	private Vector myListVector;
	
	public ConnectionListModel(IDevice[] _devices) {
		this();
		addDevices(_devices);
	}

	public ConnectionListModel() {
		myListVector = new Vector();
	}
	
	public IDevice getDeviceAt(int _index) {
		return (IDevice)myListVector.elementAt(_index);
	}
	
	public Object getElementAt(int _index) {
		IDevice device = getDeviceAt(_index);
		try {
			StringBuffer name = new StringBuffer();
			name.append(device.getName());
			String locationName = device.getConnectionFactory().getLocationName();
			if (locationName != null && locationName.length() > 0) {
				name.append(" at ");
				name.append(locationName);
			}
			return name.toString();
		}
		catch (Throwable t) {
			ExceptionUtils.printChainedStackTrace(t);
			return "Broken (" + device.getName() + ")";
		}
	}
	
	public int getSize() {
		return myListVector.size();
	}
	
	public void addDevices(IDevice[] _devices) {
		for (int i = 0; i < _devices.length; i ++) {
			addElement(_devices[i]);
		}
	}
	
	public void addElement(IDevice _device) {
		if (!myListVector.contains(_device)) {
			myListVector.addElement(_device);
			int index = getSize() - 1;
			fireIntervalAdded(this, index, index);
		}
	}

  public void removeAllElements() {
    myListVector.removeAllElements();
  }
  
	public void deviceDiscovered(IDiscoverer _discoverer, IDevice _device) {
		addElement(_device);
	}
}
