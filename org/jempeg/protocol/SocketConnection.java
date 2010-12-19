/* SocketConnection - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.inzyme.io.DebugInputStream;
import com.inzyme.io.DebugOutputStream;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.util.Debug;

public class SocketConnection implements IConnection
{
    private static final boolean DEBUG_INPUT = Debug.isDebugLevel(64);
    private static final boolean DEBUG_OUTPUT = Debug.isDebugLevel(64);
    public static final int TCP_MAXPAYLOAD = 16384;
    private static final int MAX_RETRIES = 10;
    private InetAddress myAddress;
    private int myPort;
    private int myTimeout;
    private Socket mySocket;
    private LittleEndianInputStream myInputStream;
    private LittleEndianOutputStream myOutputStream;
    
    public SocketConnection(InetAddress _address, int _port) {
	myAddress = _address;
	myPort = _port;
	myTimeout = -1;
    }
    
    public synchronized void setSocket(Socket _socket)
	throws ConnectionException {
	try {
	    mySocket = _socket;
	    if (myTimeout >= 0)
		mySocket.setSoTimeout(myTimeout);
	    InputStream is = mySocket.getInputStream();
	    OutputStream os = mySocket.getOutputStream();
	    if (DEBUG_OUTPUT)
		os = new DebugOutputStream("SocketConnection", os);
	    if (DEBUG_INPUT)
		is = new DebugInputStream("SocketConnection", is);
	    myInputStream
		= new LittleEndianInputStream(new BufferedInputStream(is,
								      16384));
	    myOutputStream = (new LittleEndianOutputStream
			      (new BufferedOutputStream(os, 16384)));
	} catch (IOException e) {
	    throw new ConnectionException
		      ("Unable to set the socket for this connection.", e);
	}
    }
    
    public InetAddress getAddress() {
	return myAddress;
    }
    
    public int getPort() {
	return myPort;
    }
    
    public Socket getSocket() {
	return mySocket;
    }
    
    public int getPacketSize() {
	return 16384;
    }
    
    public LittleEndianInputStream getInputStream() {
	return myInputStream;
    }
    
    public LittleEndianOutputStream getOutputStream() {
	return myOutputStream;
    }
    
    public synchronized IConnection getFastConnection()
	throws ConnectionException {
	IConnection fastConnection
	    = new SocketConnection(myAddress, myPort + 1);
	return fastConnection;
    }
    
    public synchronized void open() throws ConnectionException {
	close();
	if (!isOpen()) {
	    Debug.println(2, ("SocketConnection (" + this.hashCode()
			      + "): actually open"));
	    boolean succeeded = false;
	    for (int retryCount = 0; !succeeded && retryCount < 10;
		 retryCount++) {
		try {
		    Socket sock = new Socket(myAddress, myPort);
		    sock.setTcpNoDelay(true);
		    setSocket(sock);
		    flushReceiveBuffer();
		    succeeded = true;
		} catch (Throwable t) {
		    Debug.println
			(4,
			 ("Device has probably not restarted fully... Retry #"
			  + retryCount));
		    Debug.println(4, t);
		    try {
			Thread.sleep(1000L);
		    } catch (Throwable throwable) {
			/* empty */
		    }
		}
	    }
	    if (!succeeded)
		throw new ConnectionException
			  ("Failed to connect to the specified device.  Please verify the device's IP address.");
	}
    }
    
    public synchronized void close() throws ConnectionException {
	try {
	    if (isOpen()) {
		Debug.println(2, ("SocketConnection (" + this.hashCode()
				  + "): actually close"));
		if (mySocket != null) {
		    myOutputStream.flush();
		    myOutputStream.close();
		    myInputStream.close();
		    mySocket.close();
		}
		mySocket = null;
	    }
	} catch (IOException e) {
	    throw new ConnectionException
		      ("Unable to close the socket for this connection.", e);
	}
    }
    
    public void pause() throws ConnectionException {
	close();
    }
    
    public void unpause() throws ConnectionException {
	open();
    }
    
    public synchronized void flushReceiveBuffer() throws ConnectionException {
	try {
	    while (myInputStream.available() > 0)
		myInputStream.read();
	} catch (IOException e) {
	    throw new ConnectionException
		      ("Unable to flush the receive buffer for this connection.",
		       e);
	}
    }
    
    public synchronized void setTimeout(long _millis)
	throws ConnectionException {
	try {
	    myTimeout = (int) _millis;
	    if (mySocket != null)
		mySocket.setSoTimeout(myTimeout);
	} catch (IOException e) {
	    throw new ConnectionException
		      ("Unable to set the timeout for this connection.", e);
	}
    }
    
    public synchronized boolean isOpen() {
	if (mySocket != null)
	    return true;
	return false;
    }
    
    public String toString() {
	return ("[SocketConnection: address = " + myAddress.toString()
		+ "; port = " + myPort + "]");
    }
}
