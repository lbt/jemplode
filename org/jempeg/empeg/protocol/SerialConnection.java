/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol;

import java.io.IOException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

import org.jempeg.protocol.ConnectionException;
import org.jempeg.protocol.IConnection;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;

/**
* SerialConnection is an implementation of ConnectionIfc
* that uses the serial port.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class SerialConnection implements IConnection {
	private static final int SERIAL_MAXPAYLOAD = 4096;

	private SerialPort mySerialPort;
	private CommPortIdentifier myPortIdentifier;
	private int myBaudRate;
	private LittleEndianInputStream myInputStream;
	private LittleEndianOutputStream myOutputStream;

	public SerialConnection(CommPortIdentifier _portIdentifier, int _baudRate) {
		myPortIdentifier = _portIdentifier;
		myBaudRate = _baudRate;
	}

	public void setSerialPort(SerialPort _serialPort) throws ConnectionException {
		try {
			mySerialPort = _serialPort;
			//			myInputStream = new EmpegInputStream(new BufferedInputStream(mySerialPort.getInputStream()));
			//			myOutputStream = new EmpegOutputStream(new BufferedOutputStream(mySerialPort.getOutputStream()));
			myInputStream = new LittleEndianInputStream(mySerialPort.getInputStream());
			myOutputStream = new LittleEndianOutputStream(mySerialPort.getOutputStream());
		}
		catch (IOException e) {
			throw new ConnectionException("Unable to set the serial port for this connection.", e);
		}
	}

	public SerialPort getSerialPort() {
		return mySerialPort;
	}

	public int getPacketSize() {
		return SERIAL_MAXPAYLOAD;
	}

	public LittleEndianInputStream getInputStream() {
		return myInputStream;
	}

	public LittleEndianOutputStream getOutputStream() {
		return myOutputStream;
	}

	public IConnection getFastConnection() throws ConnectionException {
		return null;
	}

	public void open() throws ConnectionException {
		if (!isOpen()) {
			int maxRetries = 15;

			boolean succeeded = false;
			for (int retryCount = 0; !succeeded && retryCount < maxRetries; retryCount++) {
				try {
					SerialPort serialPort = (SerialPort) myPortIdentifier.open("JEmpeg", 2000);
					serialPort.setSerialPortParams(myBaudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
					setSerialPort(serialPort);
					succeeded = true;
					flushReceiveBuffer();
				}
				catch (PortInUseException e) {
					throw new ConnectionException("Serial port is in use.", e);
				}
				catch (UnsupportedCommOperationException e) {
					throw new ConnectionException("Unsupported operation.", e);
				}
				catch (Throwable t) {
					try {
						Thread.sleep(2000);
					}
					catch (Throwable ts) {
					}
				}
			}
			if (!succeeded) {
				throw new ConnectionException("Unable to open connection to the Empeg using " + myPortIdentifier);
			}
		}
	}

	public void close() throws ConnectionException {
		if (isOpen()) {
			if (mySerialPort != null) {
				mySerialPort.close();
			}
			mySerialPort = null;
		}
	}

	public boolean isOpen() {
		return (mySerialPort != null);
	}

	public void pause() throws ConnectionException {
		close();
	}

	public void unpause() throws ConnectionException {
		open();
	}

	public void flushReceiveBuffer() throws ConnectionException {
		try {
			while (myInputStream.available() > 0) {
				myInputStream.read();
			}
		}
		catch (IOException e) {
			throw new ConnectionException("Unable to flush the receive buffer.", e);
		}
	}

	public void setTimeout(long _millis) throws ConnectionException {
		try {
			mySerialPort.enableReceiveTimeout((int) _millis);
		}
		catch (UnsupportedCommOperationException e) {
			throw new ConnectionException("Unable to set timeout.", e);
		}
	}

	public String toString() {
		return "[SerialConnection: portIdentifier = " + myPortIdentifier.getName() + "; buadRate = " + myBaudRate + "]";
	}
}
