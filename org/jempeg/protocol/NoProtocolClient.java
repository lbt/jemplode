/* NoProtocolClient - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.jempeg.nodestore.BasicDeviceSettings;
import org.jempeg.nodestore.IDeviceSettings;

public class NoProtocolClient implements IProtocolClient
{
    private IDeviceSettings myDeviceSettings = new BasicDeviceSettings();
    
    public IConnection getConnection() {
	return null;
    }
    
    public void open() throws ProtocolException {
	/* empty */
    }
    
    public void close() throws ProtocolException {
	/* empty */
    }
    
    public boolean isDeviceConnected() {
	return true;
    }
    
    public void waitForDevice(int _retries) throws ProtocolException {
	/* empty */
    }
    
    public ProtocolVersion getProtocolVersion() throws ProtocolException {
	return new ProtocolVersion(-1, -1);
    }
    
    public void checkProtocolVersion() throws ProtocolException {
	/* empty */
    }
    
    public IDeviceSettings getDeviceSettings() {
	return myDeviceSettings;
    }
    
    public void changeDeviceSettings(IDeviceSettings _deviceSettings)
	throws ProtocolException {
	myDeviceSettings = _deviceSettings;
    }
    
    public DeviceInfo getDeviceInfo() {
	return new DeviceInfo();
    }
    
    public StorageInfo getStorageInfo(int _which) throws ProtocolException {
	return new StorageInfo();
    }
    
    public void readLock() throws ProtocolException {
	/* empty */
    }
    
    public void writeLock() throws ProtocolException {
	/* empty */
    }
    
    public void unlock() throws ProtocolException {
	/* empty */
    }
    
    public long getLength(long _fid) throws ProtocolException {
	return -1L;
    }
    
    public Properties getFileInfo(long _fid) throws ProtocolException {
	return null;
    }
    
    public void prepare(long _fid, long _size, long _storage)
	throws ProtocolException {
	/* empty */
    }
    
    public void read(long _fid, long _offset, long _size,
		     OutputStream _targetStream,
		     long _totalSize) throws ProtocolException {
	/* empty */
    }
    
    public void write(long _storage, long _fid, long _offset, long _size,
		      InputStream _sourceStream,
		      long _totalSize) throws ProtocolException {
	/* empty */
    }
    
    public void delete(long _fid, boolean _failIfMissing)
	throws ProtocolException {
	/* empty */
    }
    
    public void playAppend(long _fid) throws ProtocolException {
	/* empty */
    }
    
    public void playInsert(long _fid) throws ProtocolException {
	/* empty */
    }
    
    public void playReplace(long _fid) throws ProtocolException {
	/* empty */
    }
    
    public void pause() throws ProtocolException {
	/* empty */
    }
    
    public void nextTrack() throws ProtocolException {
	/* empty */
    }
    
    public void prevTrack() throws ProtocolException {
	/* empty */
    }
    
    public String getPlayerType() throws ProtocolException {
	return "";
    }
}
