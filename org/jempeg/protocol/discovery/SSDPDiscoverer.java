/* SSDPDiscoverer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol.discovery;
import java.io.BufferedReader;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;

import com.inzyme.exception.ExceptionUtils;
import com.inzyme.util.Debug;

public class SSDPDiscoverer extends AbstractDiscoverer
{
    private Thread myDiscoveryThread;
    private DatagramSocket[] mySendSockets;
    private String[] myURIs;
    
    public SSDPDiscoverer(String[] _uris) {
	myURIs = _uris;
    }
    
    protected void stopDiscovery0() {
	if (mySendSockets != null) {
	    for (int i = 0; i < mySendSockets.length; i++) {
		if (mySendSockets[i] != null)
		    mySendSockets[i].close();
	    }
	}
    }
    
    protected void startDiscovery0() {
	setRunning(true);
	try {
	    int port = 1900;
	    final InetAddress group = InetAddress.getByName("239.255.255.250");
	    InetAddress[] localAddresses
		= InetAddress
		      .getAllByName(InetAddress.getLocalHost().getHostName());
	    InetAddress[] addresses
		= new InetAddress[localAddresses.length + 1];
	    System.arraycopy(localAddresses, 0, addresses, 1,
			     localAddresses.length);
	    mySendSockets = new DatagramSocket[addresses.length];
	    for (int i = 0; i < addresses.length; i++) {
		final DatagramSocket sendSocket
		    = new DatagramSocket(0, addresses[i]);
		Debug.println(4, ("SSDPDiscoverer.startDiscovery0: "
				  + addresses[i]));
		mySendSockets[i] = sendSocket;
		myDiscoveryThread = new Thread(new Runnable() {
		    public void run() {
			while (SSDPDiscoverer.this.isRunning()) {
			    try {
				byte[] responseBytes = new byte[8192];
				DatagramPacket receiveDp
				    = new DatagramPacket(responseBytes, 0,
							 responseBytes.length,
							 group, 1900);
				sendSocket.receive(receiveDp);
				InetAddress responseHost
				    = receiveDp.getAddress();
				String responseData
				    = new String(receiveDp.getData(), 0,
						 receiveDp.getLength());
				Properties responseProps = new Properties();
				BufferedReader br
				    = (new BufferedReader
				       (new StringReader(responseData)));
				String line;
				while ((line = br.readLine()) != null) {
				    int colonIndex = line.indexOf(':');
				    if (colonIndex != -1) {
					String key
					    = line.substring(0, colonIndex)
						  .trim();
					String value
					    = line.substring
						  (colonIndex + 1).trim();
					responseProps.put(key, value);
				    }
				}
				SSDPDevice device
				    = new SSDPDevice(responseHost,
						     responseProps);
				SSDPDiscoverer.this
				    .fireDeviceDiscovered(device);
			    } catch (Throwable t) {
				if (SSDPDiscoverer.this.isRunning())
				    Debug.println(t);
			    }
			}
		    }
		});
		myDiscoveryThread.start();
	    }
	    for (int i = 0; i < myURIs.length; i++) {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("M-SEARCH * HTTP/1.1\r\n");
		strBuf.append("Host: 239.255.255.250:1900\r\n");
		strBuf.append("Man: \"ssdp:discover\"\r\n");
		strBuf.append("ST: " + myURIs[i] + "\r\n");
		strBuf.append("MX: 3\r\n\r\n");
		byte[] requestBytes = strBuf.toString().getBytes();
		DatagramPacket sendDp
		    = new DatagramPacket(requestBytes, 0, requestBytes.length,
					 group, 1900);
		for (int socketNum = 0; socketNum < mySendSockets.length;
		     socketNum++)
		    mySendSockets[socketNum].send(sendDp);
	    }
	    myDiscoveryThread.join();
	} catch (Throwable t) {
	    ExceptionUtils.printChainedStackTrace(t);
	}
    }
}
