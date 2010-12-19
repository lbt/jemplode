/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.protocol;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.jempeg.nodestore.IDeviceSettings;

/**
* IProtocolClient defines the interface for the protocol communication layer at
* a higher level than Request.  ProtocolClient exposes all of the core processes
* that can be performed on a Rio device.
*
* @author Mike Schrag
* @version $Revision: 1.2 $
*/
public interface IProtocolClient {
	/**
	 * STORAGE_ZERO is the constant for the first storage (which is the only one
	 * that is currently to be used)
	 */
	public static final int STORAGE_ZERO = 0;
	
	/**
	 * Returns the Connection for this client.
	 * 
	 * @return ConnectionIfc the Connection for this client
	 */
	public IConnection getConnection();
	
	/**
	 * Opens a Connection to the device.
	 *  
	 * @param _password the password for this session
	 * @throws ProtocolException if the connection cannot be opened
	 */
	public void open() throws ProtocolException;

	/**
	 * Closes the Connection to the device.
	 *  
	 * @throws ProtocolException if the connection cannot be closed
	 */
	public void close() throws ProtocolException;
	
	/**
	 * Returns whether or not a device is connected.  Failure is equivalent to not
	 * having a connection to a device.
	 * 
	 * @return boolean whether or not a device is connected
	 */
	public boolean isDeviceConnected();
	
	/**
	 * Waits up to _retries tries for a device to be connected.
	 * 
	 * @param _retries the number of retries to attempt
	 * @throws ProtocolException if the device does not connect
	 */
	public void waitForDevice(int _retries) throws ProtocolException;
	
	/**
	 * Returns the version of the protocol that is in use by the connected device.
	 * 
	 * @return ProtocolVersion the version of the protocol that is in use by the
	 * connected device
	 * @throws ProtocolException if the version cannot be determined
	 */
	public ProtocolVersion getProtocolVersion() throws ProtocolException;
	
	/**
	 * Checks to make sure we can speak the same protocol as the connected
	 * device.
	 * 
	 * @throws ProtocolException if the version doesn't match
	 */
	public void checkProtocolVersion() throws ProtocolException;
	
	/**
	 * Returns the settings for this device.
	 * 
	 * @return IDeviceSettings the settings for this device
	 * @throws ProtocolException if the settings cannot be retrieved
	 */
	public IDeviceSettings getDeviceSettings() throws ProtocolException;
	
	/**
	 * Changes the settings for this device.
	 *  
	 * @param _deviceSettings the settings for this device.
	 * @throws ProtocolException if the settings cannot be retrieved
	 */
	public void changeDeviceSettings(IDeviceSettings _deviceSettings) throws ProtocolException;
	
	/**
	 * Returns the information about this device.
	 * 
	 * @return the information about this device
	 * @throws ProtocolException if the info cannot be retrieved
	 */
	public DeviceInfo getDeviceInfo() throws ProtocolException;
	
	/**
	 * Returns information about the specified storage.
	 * 
	 * @param _which the index of the storage to lookup info for
	 * @return StorageInfo information about the storage
	 * @throws ProtocolException if the storage info cannot be retrieved
	 */
	public StorageInfo getStorageInfo(int _which) throws ProtocolException;
	
	/**
	 * Requests a read lock on the device to make sure nothing else updates the
	 * device while we are reading.
	 *
	 * @throws ProtocolException if the lock cannot be obtained
	 */
	public void readLock() throws ProtocolException;
	
	/**
	 * Requests a write lock on the device to perform an update.
	 *
	 * @throws ProtocolException if the lock cannot be obtained
	 */
	public void writeLock() throws ProtocolException;
	
	/**
	 * Relinquishes a previous lock on the device after updating.
	 *
	 * @throws ProtocolException if the device cannot be unlocked
	 */
	public void unlock() throws ProtocolException;
	
	/**
	 * Returns the length of the specified FID.
	 * 
	 * @param _fid the FID to lookup
	 * @return long the length of the specified FID
	 * @throws ProtocolException if the length cannot be retrieved
	 */
	public long getLength(long _fid) throws ProtocolException;

	/**
	 * Returns the file info for the specified FID.
	 * 
	 * @param _fid the FID to lookup
	 * @return Properties the file info for the specified FID
	 * @throws ProtocolException if the file info cannot be retrieved
	 */
	public Properties getFileInfo(long _fid) throws ProtocolException;
	
	/**
	 * Prepares a particular FID for being written to.
	 *
	 * @param _fid the FID to write to
	 * @param _size the amount of space to reserve
	 * @param _storage the storage to write to
	 * @throws ProtocolException if the preparation fails
	 */
	public void prepare(long _fid, long _size, long _storage) throws ProtocolException;

	/**
	 * Reads an FID or part of an FID from the device.
	 * 
	 * @param _fid the FID to read from
	 * @param _offset the offset into the FID to read from
	 * @param _size the amount of data to read off the device
	 * @param _targetStream the stream to put the read data into  
	 * @param _totalSize the total size of the read operation
	 * @throws ProtocolException if the data cannot be read
	 */
	public void read(long _fid, long _offset, long _size, OutputStream _targetStream, long _totalSize) throws ProtocolException;

	/**
	 * Writes an FID or part of an FID to the device.
	 *
	 * @param _storage the storage to write to
	 * @param _fid the FID to write to
	 * @param _offset the offset into the data to write from
	 * @param _size the amount of data to write to the device
	 * @param _sourceStream the stream to read the data from
	 * @param _totalSize the total size of the write operation
	 * @throws ProtocolException if the data cannot be written
	 */
	public void write(long _storage, long _fid, long _offset, long _size, InputStream _sourceStream, long _totalSize) throws ProtocolException;

	/**
	 * Deletes an FID from the device.
	 * 
	 * @param _fid the FID to delete
	 * @param _failIfMissing should the ProtocolClient fail if the request FID is not there?
	 * @throws ProtocolException if the deletion fails
	 */
	public void delete(long _fid, boolean _failIfMissing) throws ProtocolException;
	
	/**
	 * Plays the specified FID by appending to the current playlist.
	 * 
	 * @param _fid the FID to play
	 * @throws ProtocolException if the play attempt fails
	 */
	public void playAppend(long _fid) throws ProtocolException;
	
	/**
	 * Plays the specified FID by inserting to the current playlist.
	 * 
	 * @param _fid the FID to play
	 * @throws ProtocolException if the play attempt fails
	 */
	public void playInsert(long _fid) throws ProtocolException;
	
	/**
	 * Plays the specified FID by replacing the current playlist.
	 * 
	 * @param _fid the FID to play
	 * @throws ProtocolException if the play attempt fails
	 */
	public void playReplace(long _fid) throws ProtocolException;
	
	/**
	 * Pauses the currently playing tune.
	 * 
	 * @throws ProtocolException if the pause attempt fails
	 */
	public void pause() throws ProtocolException;
	
	/**
	 * Skips to the next track in the playlist.
	 * 
	 * @throws ProtocolException if the next attempt fails
	 */
	public void nextTrack() throws ProtocolException;
	
	/**
	 * Skips to the previous track in the playlist.
	 * 
	 * @throws ProtocolException if the previous attempt fails
	 */
	public void prevTrack() throws ProtocolException;

	/**
	 * Returns a String that represents the type of player that
	 * is being connected to.
	 * 
	 * @throws ProtocolException if the type cannot be retrieved
	 */
	public String getPlayerType() throws ProtocolException;
}
