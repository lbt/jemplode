/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol.discovery;

import java.io.IOException;
import java.util.Vector;

import org.jempeg.empeg.protocol.Win32USBConnection;
import org.jempeg.empeg.protocol.Win32USBConnectionFactory;
import org.jempeg.protocol.discovery.AbstractDiscoverer;
import org.jempeg.protocol.discovery.BasicDevice;

import com.inzyme.util.Debug;

public class Win32USBEmpegDiscoverer extends AbstractDiscoverer {
	public Win32USBEmpegDiscoverer() {
	}

	protected void startDiscovery0() {
		try {
			if (Win32USBConnection.DLL_EXISTS) {
				synchronized (Win32USBConnection.class) {
					findDevices();
				}
			}
		}
		catch (Throwable t) {
			Debug.println(t);
		}
	}

	private Vector vec = new Vector();

	protected void deviceDiscovered(String _deviceName) throws IOException {
		Win32USBConnectionFactory connFactory = new Win32USBConnectionFactory(_deviceName);
		if (EmpegDiscoveryUtils.isEmpegConnected(connFactory)) {
			String type = EmpegDiscoveryUtils.getPlayerType(connFactory);
			String name = EmpegDiscoveryUtils.getEmpegName(connFactory, EmpegDiscoveryUtils.getDefaultName(type));
			fireDeviceDiscovered(new BasicDevice(name, connFactory));
		}
	}

	protected void stopDiscovery0() {
	}

	private native void findDevices();
}
