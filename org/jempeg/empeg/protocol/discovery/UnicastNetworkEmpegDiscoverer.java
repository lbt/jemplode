package org.jempeg.empeg.protocol.discovery;

import java.net.InetAddress;

import org.jempeg.empeg.protocol.EmpegProtocolClient;
import org.jempeg.empeg.protocol.EmpegSynchronizeClient;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.SocketConnection;
import org.jempeg.protocol.SocketConnectionFactory;
import org.jempeg.protocol.discovery.AbstractDiscoverer;
import org.jempeg.protocol.discovery.BasicDevice;

import com.inzyme.progress.SilentProgressListener;
import com.inzyme.util.Debug;

/**
* UnicastNetworkEmpegDiscoverer implements discovery over a Socket-based
* TCP/IP network.  Discovery is based on communicating
* over port 8300 and listening for the Empeg to respond.
*
* @author Mike Schrag
* @version $Revision: 1.10 $
*/
public class UnicastNetworkEmpegDiscoverer extends AbstractDiscoverer {
	private InetAddress myInetAddress;
	private int myTimeoutMillis;
	
	/**
	* Creates a UnicastNetworkEmpegDiscoverer to the given InetAddress.
	*
	* @param _inetAddress the InetAddress of the Empeg
	*/
	public UnicastNetworkEmpegDiscoverer(InetAddress _inetAddress, int _timeoutMillis) {
		myInetAddress = _inetAddress;
		myTimeoutMillis = _timeoutMillis;
	}

	protected void startDiscovery0() {
		try {
			synchronized (SocketConnection.class) {
				SocketConnectionFactory socketConnFactory = new SocketConnectionFactory(myInetAddress, EmpegProtocolClient.PROTOCOL_TCP_PORT);
				IProtocolClient client = null;
				try {
					EmpegSynchronizeClient syncClient = new EmpegSynchronizeClient(socketConnFactory);
					client = syncClient.getProtocolClient(new SilentProgressListener());
					client.getConnection().setTimeout(myTimeoutMillis);
					if (client.isDeviceConnected()) {
						String type = client.getPlayerType();
						IDeviceSettings dcf = client.getDeviceSettings();
						String name = dcf.getName();
						if (name == null || name.length() == 0) {
							name = EmpegDiscoveryUtils.getDefaultName(type);
						}
						fireDeviceDiscovered(new BasicDevice(name, socketConnFactory));
					}
				}
				finally {
					if (client != null) {
						client.close();
					}
				}
			}
		}
		catch (Exception e) {
			Debug.println(e);
		}
	}
	
	protected void stopDiscovery0() {
	}
}
