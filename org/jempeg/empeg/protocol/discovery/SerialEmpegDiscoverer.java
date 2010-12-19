/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol.discovery;

import java.io.IOException;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;

import org.jempeg.empeg.protocol.SerialConnection;
import org.jempeg.empeg.protocol.SerialConnectionFactory;
import org.jempeg.protocol.discovery.AbstractDiscoverer;
import org.jempeg.protocol.discovery.BasicDevice;

import com.inzyme.util.Debug;

/**
* SerialEmpegDiscoverer is an implementation of 
* AbstractEmpegDiscoverer that can do discovery of
* an Empeg across a serial connection.
*
* @author Mike Schrag
* @version $Revision: 1.7 $
*/
public class SerialEmpegDiscoverer extends AbstractDiscoverer {
	public static final int EMPEG_BAUD_RATE = 115200;

	private CommPortIdentifier myPortIdentifier;
	//private int myTimeoutMillis;

	public SerialEmpegDiscoverer(int _timeoutMillis) {
		//myTimeoutMillis = _timeoutMillis;
	}

	public SerialEmpegDiscoverer(String _portName) throws NoSuchPortException {
		myPortIdentifier = CommPortIdentifier.getPortIdentifier(_portName);
	}

	protected void stopDiscovery0() {
	}

	protected void startDiscovery0() {
		try {
			synchronized (SerialConnection.class) {
				if (myPortIdentifier == null) {
					Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
					while (isRunning() && portIdentifiers.hasMoreElements()) {
						final CommPortIdentifier portIdentifier = (CommPortIdentifier) portIdentifiers.nextElement();
						if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL && !portIdentifier.isCurrentlyOwned()) {
							Thread t = new Thread(new Runnable() {
								public void run() {
									try {
										discover0(portIdentifier);
									}
									catch (IOException e) {
										Debug.println(e);
									}
								}
							}, "jEmplode Serial Discovery: " + portIdentifier);
							t.start();
						}
					}
				}
				else {
					discover0(myPortIdentifier);
				}
			}
		}
		catch (Exception e) {
			Debug.println(e);
		}
	}

	protected boolean discover0(CommPortIdentifier _portIdentifier) throws IOException {
		boolean discovered = false;
		for (int i = 0; isRunning() && !discovered && i < 3; i++) {
			try {
				SerialConnectionFactory connectionFactory = new SerialConnectionFactory(_portIdentifier, EMPEG_BAUD_RATE);
				if (EmpegDiscoveryUtils.isEmpegConnected(connectionFactory)) {
					String type = EmpegDiscoveryUtils.getPlayerType(connectionFactory);
					String name = EmpegDiscoveryUtils.getEmpegName(connectionFactory, EmpegDiscoveryUtils.getDefaultName(type));
					fireDeviceDiscovered(new BasicDevice(name, connectionFactory));
					discovered = true;
				}
				else {
					discovered = false;
				}
			}
			catch (Throwable t) {
				Debug.println(t);
			}
		}
		return discovered;
	}
}
