package org.jempeg.protocol;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.jempeg.nodestore.BasicDeviceSettings;
import org.jempeg.nodestore.IDeviceSettings;

/**
 * NoProtocolClient is an implementation of ProtocolClient that basically just 
 * NoOps. This is a stub for player databases that aren't downloaded.
 *  
 * @author Mike Schrag
 */
public class NoProtocolClient implements IProtocolClient {
	private IDeviceSettings myDeviceSettings;
	
	public NoProtocolClient() {
		myDeviceSettings = new BasicDeviceSettings();
	}
	
	public IConnection getConnection() {
		return null;
	}

	public void open() throws ProtocolException {
	}

	public void close() throws ProtocolException {
	}

	public boolean isDeviceConnected() {
		return true;
	}

	public void waitForDevice(int _retries) throws ProtocolException {
	}

	public ProtocolVersion getProtocolVersion() throws ProtocolException {
		return new ProtocolVersion(-1, -1);
	}

	public void checkProtocolVersion() throws ProtocolException {
	}

	public IDeviceSettings getDeviceSettings() {
		return myDeviceSettings;
	}

	public void changeDeviceSettings(IDeviceSettings _deviceSettings) throws ProtocolException {
		myDeviceSettings = _deviceSettings;
	}
	
	public DeviceInfo getDeviceInfo() {
		return new DeviceInfo();
	}

	public StorageInfo getStorageInfo(int _which) throws ProtocolException {
		return new StorageInfo();
	}

	public void readLock() throws ProtocolException {
	}

	public void writeLock() throws ProtocolException {
	}

	public void unlock() throws ProtocolException {
	}

	public long getLength(long _fid) throws ProtocolException {
		return -1;
	}

	public Properties getFileInfo(long _fid) throws ProtocolException {
		return null;
	}

	public void prepare(long _fid, long _size, long _storage) throws ProtocolException {
	}

	public void read(long _fid, long _offset, long _size, OutputStream _targetStream, long _totalSize) throws ProtocolException {
	}

	public void write(long _storage, long _fid, long _offset, long _size, InputStream _sourceStream, long _totalSize) throws ProtocolException {
	}

	public void delete(long _fid, boolean _failIfMissing) throws ProtocolException {
	}

	public void playAppend(long _fid) throws ProtocolException {
	}

	public void playInsert(long _fid) throws ProtocolException {
	}

	public void playReplace(long _fid) throws ProtocolException {
	}

	public void pause() throws ProtocolException {
	}

	public void nextTrack() throws ProtocolException {
	}

	public void prevTrack() throws ProtocolException {
	}

	public String getPlayerType() throws ProtocolException {
		return "";
	}
}
