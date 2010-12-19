/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
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

/**
* SocketConnection is an implementation of
* ConnectionIfc that can communicate with 
* a device over an ethernet connection.
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public class SocketConnection implements IConnection {
	private static final boolean DEBUG_INPUT = Debug.isDebugLevel(Debug.COMPLETELY_RIDICULOUS);
	private static final boolean DEBUG_OUTPUT = Debug.isDebugLevel(Debug.COMPLETELY_RIDICULOUS);
	
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

	public synchronized void setSocket(Socket _socket) throws ConnectionException {
		try {
			mySocket = _socket;
			if (myTimeout >= 0) {
				mySocket.setSoTimeout(myTimeout);
			}
			InputStream is = mySocket.getInputStream();
			OutputStream os = mySocket.getOutputStream();
			if (DEBUG_OUTPUT) {
				os = new DebugOutputStream("SocketConnection", os);
			}
			if (DEBUG_INPUT) {
				is = new DebugInputStream("SocketConnection", is);
			}
			myInputStream = new LittleEndianInputStream(new BufferedInputStream(is, SocketConnection.TCP_MAXPAYLOAD));
			myOutputStream = new LittleEndianOutputStream(new BufferedOutputStream(os, SocketConnection.TCP_MAXPAYLOAD));
		}
		catch (IOException e) {
			throw new ConnectionException("Unable to set the socket for this connection.", e);
		}
	}
	
	/**
	 * Returns the InetAddress that this connection will connect to.
	 * 
	 * @return the InetAddress that this connection will connect to
	 */
	public InetAddress getAddress() {
		return myAddress;
	}
	
	/**
	 * Returns the port that this connection will connect to.
	 * 
	 * @return the port that this connection will connect to
	 */
	public int getPort() {
		return myPort;
	}

	/**
	 * Returns the Socket for this connection.
	 * 
	 * @return the Socket for this connection
	 */
	public Socket getSocket() {
		return mySocket;
	}

	public int getPacketSize() {
		return TCP_MAXPAYLOAD;
	}

	public LittleEndianInputStream getInputStream() {
		return myInputStream;
	}

	public LittleEndianOutputStream getOutputStream() {
		return myOutputStream;
	}

	public synchronized IConnection getFastConnection() throws ConnectionException {
		IConnection fastConnection = new SocketConnection(myAddress, myPort + 1);
		return fastConnection;
	}

	public synchronized void open() throws ConnectionException {
		close();

		if (!isOpen()) {
			Debug.println(Debug.VERBOSE, "SocketConnection (" + hashCode() + "): actually open");
			
			// I loop here because when the socket is restarted, it is
			// possible that it will fail to come back up
			boolean succeeded = false;
			for (int retryCount = 0; !succeeded && retryCount < SocketConnection.MAX_RETRIES; retryCount++) {
				try {
					Socket sock = new Socket(myAddress, myPort);
					sock.setTcpNoDelay(true);
					setSocket(sock);
					flushReceiveBuffer();
					succeeded = true;
				}
				catch (Throwable t) {
					Debug.println(Debug.INFORMATIVE, "Device has probably not restarted fully... Retry #" + retryCount);
          Debug.println(Debug.INFORMATIVE, t);
					try {
						Thread.sleep(1000);
					}
					catch (Throwable ex) {
					}
				}
			}

			if (!succeeded) {
				throw new ConnectionException("Failed to connect to the specified device.  Please verify the device's IP address.");
			}
		}
	}

	public synchronized void close() throws ConnectionException {
		try {
			if (isOpen()) {
				Debug.println(Debug.VERBOSE, "SocketConnection (" + hashCode() + "): actually close");
				
				if (mySocket != null) {
					myOutputStream.flush();
					myOutputStream.close();
					myInputStream.close();
					mySocket.close();
				}
				mySocket = null;
			}
		}
		catch (IOException e) {
			throw new ConnectionException("Unable to close the socket for this connection.", e);
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
			while (myInputStream.available() > 0) {
				myInputStream.read();
			}
		}
		catch (IOException e) {
			throw new ConnectionException("Unable to flush the receive buffer for this connection.", e);
		}
	}

	public synchronized void setTimeout(long _millis) throws ConnectionException {
		try {
			myTimeout = (int) _millis;
			if (mySocket != null) {
				mySocket.setSoTimeout(myTimeout);
			}
		}
		catch (IOException e) {
			throw new ConnectionException("Unable to set the timeout for this connection.", e);
		}
	}

	public synchronized boolean isOpen() {
		return (mySocket != null);
	}

	public String toString() {
		return "[SocketConnection: address = " + myAddress.toString() + "; port = " + myPort + "]";
	}
}
