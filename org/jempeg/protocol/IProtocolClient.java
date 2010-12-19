/* IProtocolClient - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.jempeg.nodestore.IDeviceSettings;

public interface IProtocolClient
{
    public static final int STORAGE_ZERO = 0;
    
    public IConnection getConnection();
    
    public void open() throws ProtocolException;
    
    public void close() throws ProtocolException;
    
    public boolean isDeviceConnected();
    
    public void waitForDevice(int i) throws ProtocolException;
    
    public ProtocolVersion getProtocolVersion() throws ProtocolException;
    
    public void checkProtocolVersion() throws ProtocolException;
    
    public IDeviceSettings getDeviceSettings() throws ProtocolException;
    
    public void changeDeviceSettings(IDeviceSettings idevicesettings)
	throws ProtocolException;
    
    public DeviceInfo getDeviceInfo() throws ProtocolException;
    
    public StorageInfo getStorageInfo(int i) throws ProtocolException;
    
    public void readLock() throws ProtocolException;
    
    public void writeLock() throws ProtocolException;
    
    public void unlock() throws ProtocolException;
    
    public long getLength(long l) throws ProtocolException;
    
    public Properties getFileInfo(long l) throws ProtocolException;
    
    public void prepare(long l, long l_0_, long l_1_) throws ProtocolException;
    
    public void read(long l, long l_2_, long l_3_, OutputStream outputstream,
		     long l_4_) throws ProtocolException;
    
    public void write(long l, long l_5_, long l_6_, long l_7_,
		      InputStream inputstream,
		      long l_8_) throws ProtocolException;
    
    public void delete(long l, boolean bool) throws ProtocolException;
    
    public void playAppend(long l) throws ProtocolException;
    
    public void playInsert(long l) throws ProtocolException;
    
    public void playReplace(long l) throws ProtocolException;
    
    public void pause() throws ProtocolException;
    
    public void nextTrack() throws ProtocolException;
    
    public void prevTrack() throws ProtocolException;
    
    public String getPlayerType() throws ProtocolException;
}
