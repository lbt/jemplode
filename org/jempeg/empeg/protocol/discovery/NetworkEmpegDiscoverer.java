/* NetworkEmpegDiscoverer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.discovery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.inzyme.util.Debug;

import org.jempeg.protocol.ConnectionException;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.SocketConnectionFactory;
import org.jempeg.protocol.discovery.AbstractDiscoverer;
import org.jempeg.protocol.discovery.BasicDevice;
import org.jempeg.protocol.discovery.PrintDiscoveryListener;

public class NetworkEmpegDiscoverer extends AbstractDiscoverer
{
    private static final int DISCOVERY_PORT = 8300;
    private static final String DISCOVERY_STRING = "?";
    private String myDoNotBroadcastTo;
    private Vector mySocketsVec = new Vector();
    private Vector myThreadsVec = new Vector();
    private int myTimeoutMillis;
    /*synthetic*/ static Class class$0;
    
    protected class BroadcastRunnable implements Runnable
    {
	private DatagramSocket mySocket;
	private Vector myBroadcastAddressesVec;
	private Vector myDiscoveredVec;
	private boolean myDiscovered;
	
	public BroadcastRunnable(DatagramSocket _socket,
				 Vector _broadcastAddressesVec,
				 Vector _discoveredVec) {
	    mySocket = _socket;
	    myBroadcastAddressesVec = _broadcastAddressesVec;
	    myDiscoveredVec = _discoveredVec;
	}
	
	public boolean isDiscovered() {
	    return myDiscovered;
	}
	
	public void run() {
	    Enumeration broadcastAddressesEnum
		= myBroadcastAddressesVec.elements();
	    try {
		while (NetworkEmpegDiscoverer.this.isRunning()
		       && broadcastAddressesEnum.hasMoreElements()) {
		    String broadcastAddressStr
			= (String) broadcastAddressesEnum.nextElement();
		    if (myDoNotBroadcastTo == null
			|| (myDoNotBroadcastTo.indexOf(broadcastAddressStr)
			    == -1)) {
			try {
			    InetAddress broadcastAddress
				= InetAddress.getByName(broadcastAddressStr);
			    Debug.println(4,
					  ("Broadcasting on "
					   + broadcastAddress.getHostAddress()
					   + "..."));
			    broadcastDiscoveryRequest(mySocket,
						      broadcastAddress);
			} catch (Throwable t) {
			    Debug.println(t);
			}
		    }
		}
		try {
		    myDiscovered
			= waitForDiscoveryResponses(mySocket, myTimeoutMillis,
						    myDiscoveredVec);
		} catch (InterruptedIOException interruptedioexception) {
		    /* empty */
		} catch (Throwable t) {
		    Debug.println(4, t);
		}
	    } catch (Object object) {
		try {
		    mySocket.close();
		} catch (Throwable t) {
		    Debug.println(4, t);
		}
		throw object;
	    }
	    try {
		mySocket.close();
	    } catch (Throwable t) {
		Debug.println(4, t);
	    }
	}
    }
    
    public NetworkEmpegDiscoverer(int _timeoutMillis) {
	myTimeoutMillis = _timeoutMillis;
    }
    
    public void setDoNotBroadcastTo(String _doNotBroadcastTo) {
	myDoNotBroadcastTo = _doNotBroadcastTo;
    }
    
    protected void stopDiscovery0() {
	Enumeration socketsEnum = mySocketsVec.elements();
	while (socketsEnum.hasMoreElements()) {
	    DatagramSocket s = (DatagramSocket) socketsEnum.nextElement();
	    try {
		s.close();
	    } catch (Throwable t) {
		Debug.println(4, t);
	    }
	}
    }
    
    protected void startDiscovery0() {
	myThreadsVec.clear();
	mySocketsVec.clear();
	try {
	    Class var_class = class$0;
	    if (var_class == null) {
		Class var_class_0_;
		try {
		    var_class_0_
			= Class
			      .forName("org.jempeg.protocol.SocketConnection");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class = class$0 = var_class_0_;
	    }
	    Class var_class_1_;
	    MONITORENTER (var_class_1_ = var_class);
	    MISSING MONITORENTER
	    synchronized (var_class_1_) {
		Hashtable interfaceToBroadcastAddress = new Hashtable();
		InetAddress[] addresses
		    = InetAddress.getAllByName(InetAddress.getLocalHost()
						   .getHostName());
		StringBuffer broadcastAddressStrBuf = new StringBuffer();
		for (int i = 0; i < addresses.length; i++) {
		    Vector broadcastAddressesVec = new Vector();
		    broadcastAddressesVec.addElement("255.255.255.255");
		    byte[] address = addresses[i].getAddress();
		    for (int j = 1; j < address.length; j++) {
			broadcastAddressStrBuf.setLength(0);
			for (int k = 0; k < j; k++) {
			    broadcastAddressStrBuf.append(address[k] < 0
							  ? (int) (256
								   + (address
								      [k]))
							  : address[k]);
			    broadcastAddressStrBuf.append('.');
			}
			for (int k = j; k < 4; k++)
			    broadcastAddressStrBuf.append("255.");
			broadcastAddressStrBuf
			    .setLength(broadcastAddressStrBuf.length() - 1);
			broadcastAddressesVec
			    .addElement(broadcastAddressStrBuf.toString());
		    }
		    interfaceToBroadcastAddress.put(addresses[i],
						    broadcastAddressesVec);
		}
		Vector discoveredVec = new Vector();
		Vector runnablesVec = new Vector();
		Enumeration interfaceAddressEnum
		    = interfaceToBroadcastAddress.keys();
		while (isRunning() && interfaceAddressEnum.hasMoreElements()) {
		    InetAddress interfaceAddress
			= (InetAddress) interfaceAddressEnum.nextElement();
		    try {
			DatagramSocket socket
			    = new DatagramSocket(8300, interfaceAddress);
			mySocketsVec.add(socket);
			Vector broadcastAddressesVec
			    = (Vector) interfaceToBroadcastAddress
					   .get(interfaceAddress);
			BroadcastRunnable br
			    = new BroadcastRunnable(socket,
						    broadcastAddressesVec,
						    discoveredVec);
			runnablesVec.addElement(br);
			Thread bt
			    = (new Thread
			       (br, ("jEmplode: NetworkEmpegDiscoverer for "
				     + interfaceAddress.getHostAddress())));
			myThreadsVec.addElement(bt);
			bt.start();
		    } catch (BindException bindexception) {
			/* empty */
		    }
		}
	    }
	    Enumeration threadsEnum = myThreadsVec.elements();
	    while (isRunning()) {
		if (!threadsEnum.hasMoreElements())
		    break;
		Thread t = (Thread) threadsEnum.nextElement();
		boolean done = false;
		while (isRunning() && !done) {
		    try {
			t.join();
			done = true;
		    } catch (InterruptedException interruptedexception) {
			/* empty */
		    }
		}
	    }
	} catch (Exception e) {
	    Debug.println(e);
	}
    }
    
    protected void broadcastDiscoveryRequest
	(DatagramSocket _socket, InetAddress _broadcastAddress)
	throws IOException {
	DatagramPacket packet
	    = new DatagramPacket("?".getBytes(), "?".length(),
				 _broadcastAddress, 8300);
	_socket.send(packet);
    }
    
    protected boolean waitForDiscoveryResponses
	(DatagramSocket _socket, int _timeoutMillis, Vector _discoveredVec)
	throws IOException {
	long startTimeMillis = System.currentTimeMillis();
	boolean discovered = false;
	boolean timeUp = false;
	while (isRunning() && !timeUp) {
	    long timeUsed = System.currentTimeMillis() - startTimeMillis;
	    if (timeUsed > (long) _timeoutMillis)
		timeUp = true;
	    if (!timeUp) {
		try {
		    DatagramPacket packet
			= new DatagramPacket(new byte[4096], 4096);
		    _socket.setSoTimeout((int) ((long) _timeoutMillis
						- timeUsed));
		    _socket.receive(packet);
		    InetAddress responseAddress = packet.getAddress();
		    String responseAddressStr
			= responseAddress.getHostAddress();
		    Vector vector;
		    MONITORENTER (vector = _discoveredVec);
		    MISSING MONITORENTER
		    synchronized (vector) {
			if (!_discoveredVec.contains(responseAddressStr)) {
			    _discoveredVec.addElement(responseAddressStr);
			    discovered = parseDiscoveryResponse(packet);
			}
		    }
		} catch (InterruptedIOException interruptedioexception) {
		    /* empty */
		} catch (Exception e) {
		    if (isRunning())
			Debug.println(e);
		}
	    }
	}
	if (timeUp && !discovered)
	    discovered = false;
	return discovered;
    }
    
    protected boolean parseDiscoveryResponse(DatagramPacket _packet)
	throws IOException, ConnectionException, ProtocolException {
	int packetLength = _packet.getLength();
	byte[] oldData = _packet.getData();
	byte[] newData = new byte[packetLength + 1];
	System.arraycopy(oldData, 0, newData, 0, packetLength);
	newData[packetLength] = (byte) 10;
	ByteArrayInputStream bais = new ByteArrayInputStream(newData);
	Properties props = new Properties();
	props.load(bais);
	String name = props.getProperty("name");
	String id = props.getProperty("id");
	boolean discovered;
	if (name != null && !name.equals("") && id != null && !id.equals("")) {
	    Debug.println(4, "Found Empeg = " + name);
	    InetAddress ipAddress = _packet.getAddress();
	    String string;
	    MONITORENTER (string = ipAddress.getHostAddress().intern());
	    MISSING MONITORENTER
	    synchronized (string) {
		fireDeviceDiscovered
		    (new BasicDevice(name,
				     new SocketConnectionFactory(ipAddress,
								 8300)));
		discovered = true;
	    }
	} else
	    discovered = false;
	return discovered;
    }
    
    public static void main(String[] _args) throws Throwable {
	NetworkEmpegDiscoverer ned = new NetworkEmpegDiscoverer(10000);
	ned.addDiscoveryListener(new PrintDiscoveryListener());
	ned.startDiscovery();
    }
}
