/* SocketConnectionFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.net.InetAddress;

import com.inzyme.util.ReflectionUtils;

public class SocketConnectionFactory implements IConnectionFactory
{
    private InetAddress myAddress;
    private int myPort;
    
    public SocketConnectionFactory(InetAddress _address, int _port) {
	myAddress = _address;
	myPort = _port;
    }
    
    public InetAddress getAddress() {
	return myAddress;
    }
    
    public int getPort() {
	return myPort;
    }
    
    public IConnection createConnection() {
	return new SocketConnection(myAddress, myPort);
    }
    
    public String getLocationName() {
	return myAddress.getHostAddress();
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
