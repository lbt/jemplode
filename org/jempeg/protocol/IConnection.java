/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.protocol;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;

/**
* Provides the interface that must be implemented to provide
* basic communication capabilities to a Rio device.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public interface IConnection {
  /**
  * Returns the maximum packet size of the connection.
  */
  public int getPacketSize();

  /**
  * Returns an input stream to read data from the device
  *
  * @throws ConnectionException if the stream cannot be retrieved
  */
  public LittleEndianInputStream getInputStream() throws ConnectionException;

  /**
  * Returns an output stream to write data to the device
  *
  * @throws ConnectionException if the stream cannot be retrieved
  */
  public LittleEndianOutputStream getOutputStream() throws ConnectionException;

  /**
  * Returns a fast connection for this connection (or null
  * if one is not available).
  *
  * @returns a fast connection for this connection
  * @throws ConnectionException if the connection cannot be created
  */
  public IConnection getFastConnection() throws ConnectionException;

  /**
  * Opens the communication channel to the device
  *
  * @throws ConnectionException if the connection cannot be opened
  */
  public void open() throws ConnectionException;

  /**
  * Closes the communication channel to the device
  *
  * @throws ConnectionException if the connection cannot be closed
  */
  public void close() throws ConnectionException;

  /**
  * Pauses the communication channel to the device.  You got me as to
  * why this exists -- this came from the original port.  All
  * implementations just proxy close.
  *
  * @throws ConnectionException if the connection cannot be paused
  */
  public void pause() throws ConnectionException;

  /**
  * Unpauses the communication channel to the device.  You got me as to
  * why this exists -- this came from the original port.  All
  * implementations just proxy open.
  *
  * @throws ConnectionException if the connection cannot be unpaused
  */
  public void unpause() throws ConnectionException;

  /**
  * Flushes the receive buffer (for use after things like
  * error conditions when lingering bytes may be hanging at 
  * the end of the stream.
  *
  * @throws ConnectionException if the buffer cannot be flushed
  */
  public void flushReceiveBuffer() throws ConnectionException;

  /**
  * Sets the current timeout on the connection.
  *
  * @param _millis the number of milliseconds before an exception is thrown
  * @throws ConnectionException if the timeout cannot be set
  */
  public void setTimeout(long _millis) throws ConnectionException;

  /**
  * Returns whether or not this connection is open.
  *
  * @returns whether or not this connection is open
  */
  public boolean isOpen();
}
