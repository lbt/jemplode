/**
 * This file is licensed under the GPL.
 *
 * See the LICENSE0 file included in this release, or
 * http://www.opensource.org/licenses/gpl-license.html
 * for the details of the license.
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

import org.jempeg.empeg.protocol.EmpegProtocolClient;
import org.jempeg.protocol.ConnectionException;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.SocketConnection;
import org.jempeg.protocol.SocketConnectionFactory;
import org.jempeg.protocol.discovery.AbstractDiscoverer;
import org.jempeg.protocol.discovery.BasicDevice;
import org.jempeg.protocol.discovery.PrintDiscoveryListener;

import com.inzyme.util.Debug;

/**
 * NetworkEmpegDiscoverer implements discovery over a Socket-based
 * TCP/IP network.  Discovery is based on broadcasting a datagram to
 * over port 8300 and listening for the Empeg to respond.
 *
 * @version 2.0a1 $Date: 2004/09/25 04:04:08 $
 * @author Mike Schrag
 * @author Daniel Zimmerman
 */
public class NetworkEmpegDiscoverer extends AbstractDiscoverer {
  private static final int DISCOVERY_PORT = 8300;
  private static final String DISCOVERY_STRING = "?";

  private String myDoNotBroadcastTo;
  private Vector mySocketsVec;
  private Vector myThreadsVec;
  private int myTimeoutMillis;

  /**
   * Constructs a new NetworkEmpegDiscoverer using 255.255.255.255
   * as the broadcast address.
   */
  public NetworkEmpegDiscoverer(int _timeoutMillis) {
    mySocketsVec = new Vector();
    myThreadsVec = new Vector();
    myTimeoutMillis = _timeoutMillis;
  }

  /**
   * Sets a comma-separated list of broadcast addresses
   * to not broadcast on.  This is an optimization if
   * someone has a NIC that they KNOW will not have an
   * Empeg on it.
   *
   * @param _doNotBroadcastTo the list of addresses to not broadcast to
   */
  public void setDoNotBroadcastTo(String _doNotBroadcastTo) {
    myDoNotBroadcastTo = _doNotBroadcastTo;
  }

  protected void stopDiscovery0() {
    Enumeration socketsEnum = mySocketsVec.elements();
    while (socketsEnum.hasMoreElements()) {
      DatagramSocket s = (DatagramSocket) socketsEnum.nextElement();
      try {
        s.close();
      }
      catch (Throwable t) {
        Debug.println(Debug.INFORMATIVE, t);
      }
    }
  }

  protected void startDiscovery0() {
    myThreadsVec.clear();
    mySocketsVec.clear();
    try {
      synchronized (SocketConnection.class) {
        Hashtable interfaceToBroadcastAddress = new Hashtable();

        InetAddress[] addresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
        StringBuffer broadcastAddressStrBuf = new StringBuffer();
        for (int i = 0; i < addresses.length; i ++ ) {
          Vector broadcastAddressesVec = new Vector();
          broadcastAddressesVec.addElement("255.255.255.255");

          byte[] address = addresses[i].getAddress();
          for (int j = 1; j < address.length; j ++ ) {
            broadcastAddressStrBuf.setLength(0);
            for (int k = 0; k < j; k ++ ) {
              broadcastAddressStrBuf.append((address[k] < 0) ? (256 + (int) address[k]) : address[k]);
              broadcastAddressStrBuf.append('.');
            }
            for (int k = j; k < 4; k ++ ) {
              broadcastAddressStrBuf.append("255.");
            }
            broadcastAddressStrBuf.setLength(broadcastAddressStrBuf.length() - 1);
            broadcastAddressesVec.addElement(broadcastAddressStrBuf.toString());
          }

          interfaceToBroadcastAddress.put(addresses[i], broadcastAddressesVec);
        }

        Vector discoveredVec = new Vector();
        Vector runnablesVec = new Vector();
        Enumeration interfaceAddressEnum = interfaceToBroadcastAddress.keys();
        while (isRunning() && interfaceAddressEnum.hasMoreElements()) {
          InetAddress interfaceAddress = (InetAddress) interfaceAddressEnum.nextElement();
          try {
            DatagramSocket socket = new DatagramSocket(NetworkEmpegDiscoverer.DISCOVERY_PORT, interfaceAddress);
            mySocketsVec.add(socket);
            Vector broadcastAddressesVec = (Vector) interfaceToBroadcastAddress.get(interfaceAddress);

            BroadcastRunnable br = new BroadcastRunnable(socket, broadcastAddressesVec, discoveredVec);
            runnablesVec.addElement(br);

            Thread bt = new Thread(br, "jEmplode: NetworkEmpegDiscoverer for " + interfaceAddress.getHostAddress());
            myThreadsVec.addElement(bt);

            bt.start();
          }
          catch (BindException t) {
            // Ignore ...
          }
        }
      }

      Enumeration threadsEnum = myThreadsVec.elements();
      while (isRunning() && threadsEnum.hasMoreElements()) {
        Thread t = (Thread) threadsEnum.nextElement();
        boolean done = false;
        while (isRunning() && !done) {
          try {
            t.join();
            done = true;
          }
          catch (InterruptedException e) {
          }
        }
      }
    }
    catch (Exception e) {
      Debug.println(e);
    }
  }

  protected void broadcastDiscoveryRequest(DatagramSocket _socket, InetAddress _broadcastAddress) throws IOException {
    DatagramPacket packet = new DatagramPacket(DISCOVERY_STRING.getBytes(), DISCOVERY_STRING.length(), _broadcastAddress, NetworkEmpegDiscoverer.DISCOVERY_PORT);
    _socket.send(packet);
  }

  protected boolean waitForDiscoveryResponses(DatagramSocket _socket, int _timeoutMillis, Vector _discoveredVec) throws IOException {
    long startTimeMillis = System.currentTimeMillis();
    boolean discovered = false;
    boolean timeUp = false;
    while (isRunning() && !timeUp) {
      //    while (!discovered) {
      long timeUsed = (System.currentTimeMillis() - startTimeMillis);
      if (timeUsed > _timeoutMillis) {
        timeUp = true;
      }

      if (!timeUp) {
        try {
          DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
          _socket.setSoTimeout((int) (_timeoutMillis - timeUsed));
          _socket.receive(packet);
          InetAddress responseAddress = packet.getAddress();
          String responseAddressStr = responseAddress.getHostAddress();
          synchronized (_discoveredVec) {
            if (!_discoveredVec.contains(responseAddressStr)) {
              _discoveredVec.addElement(responseAddressStr);
              discovered = parseDiscoveryResponse(packet);
            }
          }
        }
        catch (InterruptedIOException e) {
        }
        catch (Exception e) {
          if (isRunning()) {
            Debug.println(e);
          }
        }
      }
    }
    if (timeUp && !discovered) {
      discovered = false;
      //throw new IOException("Timed out before recieving a response.");
    }
    return discovered;
  }

  protected boolean parseDiscoveryResponse(DatagramPacket _packet) throws IOException, ConnectionException, ProtocolException {
    int packetLength = _packet.getLength();
    byte[] oldData = _packet.getData();
    byte[] newData = new byte[packetLength + 1];
    System.arraycopy(oldData, 0, newData, 0, packetLength);
    newData[packetLength] = (byte) '\n';

    ByteArrayInputStream bais = new ByteArrayInputStream(newData);
    Properties props = new Properties();
    props.load(bais);

    String name = props.getProperty("name");
    String id = props.getProperty("id");

    boolean discovered;
    if (name != null && !name.equals("") && id != null && !id.equals("")) {
      Debug.println(Debug.INFORMATIVE, "Found Empeg = " + name);
      InetAddress ipAddress = _packet.getAddress();
      synchronized (ipAddress.getHostAddress().intern()) {
        fireDeviceDiscovered(new BasicDevice(name, new SocketConnectionFactory(ipAddress, EmpegProtocolClient.PROTOCOL_TCP_PORT)));
        discovered = true;
      }
    }
    else {
      discovered = false;
    }
    return discovered;
  }

  protected class BroadcastRunnable implements Runnable {
    private DatagramSocket mySocket;
    private Vector myBroadcastAddressesVec;
    private Vector myDiscoveredVec;
    private boolean myDiscovered;

    public BroadcastRunnable(DatagramSocket _socket, Vector _broadcastAddressesVec, Vector _discoveredVec) {
      mySocket = _socket;
      myBroadcastAddressesVec = _broadcastAddressesVec;
      myDiscoveredVec = _discoveredVec;
    }

    public boolean isDiscovered() {
      return myDiscovered;
    }

    public void run() {
      Enumeration broadcastAddressesEnum = myBroadcastAddressesVec.elements();
      try {
        while (isRunning() && broadcastAddressesEnum.hasMoreElements()) {
          String broadcastAddressStr = (String) broadcastAddressesEnum.nextElement();
          if (myDoNotBroadcastTo == null || myDoNotBroadcastTo.indexOf(broadcastAddressStr) == -1) {
            try {
              InetAddress broadcastAddress = InetAddress.getByName(broadcastAddressStr);

              // Amazing ... toString on InetAddress takes FOREVER!
              Debug.println(Debug.INFORMATIVE, "Broadcasting on " + broadcastAddress.getHostAddress() + "...");
              broadcastDiscoveryRequest(mySocket, broadcastAddress);
            }
            catch (Throwable t) {
              Debug.println(t);
            }
          }
        }
        try {
          myDiscovered = waitForDiscoveryResponses(mySocket, myTimeoutMillis, myDiscoveredVec);
        }
        catch (InterruptedIOException e) {
        }
        catch (Throwable t) {
          Debug.println(Debug.INFORMATIVE, t);
        }
      }
      finally {
        try {
          mySocket.close();
        }
        catch (Throwable t) {
          Debug.println(Debug.INFORMATIVE, t);
        }
      }
    }
  }

  public static void main(String[] _args) throws Throwable {
    NetworkEmpegDiscoverer ned = new NetworkEmpegDiscoverer(10000);
    ned.addDiscoveryListener(new PrintDiscoveryListener());
    ned.startDiscovery();
  }
}