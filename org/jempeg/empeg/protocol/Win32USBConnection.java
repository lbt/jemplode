package org.jempeg.empeg.protocol;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.jempeg.protocol.ConnectionException;
import org.jempeg.protocol.IConnection;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.util.Debug;

public class Win32USBConnection implements IConnection {
	public static boolean DLL_EXISTS = false;

	static {
		try {
			System.loadLibrary("jEmplode");
			DLL_EXISTS = true;
		}
		catch (UnsatisfiedLinkError e) {
			Debug.println(Debug.WARNING, "You do not have jEmplode.dll installed somewhere in your PATH.  USB support will not work until you do.");
		}
	}

  private String myDeviceName;
  private int myNativeConnection;
  private boolean myIsOpen;

  public Win32USBConnection(String _deviceName) {
    myDeviceName = _deviceName;
    myNativeConnection = createNativeConnection();
  }

  /**
  * Returns whether or not this connection is open.
  *
  * @returns whether or not this connection is open
  */
  public boolean isOpen() {
    return myIsOpen;
  }

  /**
  * Returns an input stream to read data from the Empeg
  *
  * @throws ConnectionException if the stream cannot be retrieved
  */
  public LittleEndianInputStream getInputStream() throws ConnectionException {
		InputStream is = new Win32USBInputStream(myNativeConnection);
    //InputStream is = new BufferedInputStream(new Win32USBInputStream(myNativeConnection));
    //EmpegInputStream eis = new EmpegInputStream(new BufferedInputStream(is));
    LittleEndianInputStream eis = new LittleEndianInputStream(is);
    return eis;
  }

  /**
  * Returns an output stream to write data to the Empeg
  *
  * @throws ConnectionException if the stream cannot be retrieved
  */
  public LittleEndianOutputStream getOutputStream() throws ConnectionException {
		OutputStream os = new BufferedOutputStream(new Win32USBOutputStream(myNativeConnection));
    //OutputStream os = new BufferedOutputStream(new Win32USBOutputStream(myNativeConnection));
    //EmpegOutputStream eos = new EmpegOutputStream(new BufferedOutputStream(os));
    LittleEndianOutputStream eos = new LittleEndianOutputStream(os);
    return eos;
  }

  public IConnection getFastConnection() throws ConnectionException {
    return null;
  }


  /**
  * Returns the maximum packet size of the connection.
  */
  public int getPacketSize() {
    return 16384;
  }

  /**
  * Opens the communication channel to the Empeg
  *
  * @throws ConnectionException if the connection cannot be opened
  */
  public void open() throws ConnectionException {
  	close();
  	
    open0();
    myIsOpen = true;
  }

  /**
  * Closes the communication channel to the Empeg
  *
  * @throws ConnectionException if the connection cannot be closed
  */
  public void close() throws ConnectionException {
    close0();
    myIsOpen = false;
  }

  /**
  * Pauses the communication channel to the Empeg.  You got me as to
  * why this exists -- this came from the original port.  All
  * implementations just proxy close.
  *
  * @throws ConnectionException if the connection cannot be paused
  */
  public void pause() throws ConnectionException {
    close();
  }

  /**
  * Unpauses the communication channel to the Empeg.  You got me as to
  * why this exists -- this came from the original port.  All
  * implementations just proxy open.
  *
  * @throws ConnectionException if the connection cannot be unpaused
  */
  public void unpause() throws ConnectionException {
    open();
  }

  /**
  * Flushes the receive buffer (for use after things like
  * error conditions when lingering bytes may be hanging at
  * the end of the stream.
  *
  * @throws ConnectionException if the buffer cannot be flushed
  */
  public void flushReceiveBuffer() throws ConnectionException {
    flushReceiveBuffer0();
  }

  /**
  * Sets the current timeout on the connection.
  *
  * @param _millis the number of milliseconds before an exception is thrown
  * @throws ConnectionException if the timeout cannot be set
  */
  public void setTimeout(long _millis) throws ConnectionException {
  }

  private native int createNativeConnection();

  private native void open0();

  private native void close0();

  private native void pause0();
  
  private native void unpause0();

  private native void flushReceiveBuffer0();
}
