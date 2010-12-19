/* PearlProtocolClient - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.util.Debug;
import com.rio.protocol2.packet.GetAllFileInfoReplyPacket;
import com.rio.protocol2.packet.InvalidPasswordException;
import com.rio.protocol2.packet.NotLoggedInException;
import com.rio.protocol2.packet.StatusFailedException;

import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.protocol.AuthenticatorFactory;
import org.jempeg.protocol.ConnectionException;
import org.jempeg.protocol.DeviceInfo;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.IPasswordManager;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.PasswordAuthentication;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.ProtocolVersion;
import org.jempeg.protocol.SimplePasswordManager;
import org.jempeg.protocol.StorageInfo;

public class PearlProtocolClient
    implements IProtocolClient, ISimpleProgressListener
{
    public static final int PROTOCOL_TCP_PORT = 8302;
    private IConnection myConnection;
    private PearlRequest myRequest;
    private int myConnectionRefCount;
    private int myMaxAttempts = 3;
    private ISimpleProgressListener myProgressListener;
    private IPasswordManager myPasswordManager;
    
    public PearlProtocolClient(IConnection _connection,
			       ISimpleProgressListener _progressListener) {
	this(_connection, _progressListener, new SimplePasswordManager());
    }
    
    public PearlProtocolClient(IConnection _connection,
			       ISimpleProgressListener _progressListener,
			       IPasswordManager _passwordManager) {
	myConnection = _connection;
	myRequest = new PearlRequest(_connection, this);
	myProgressListener = _progressListener;
	myPasswordManager = _passwordManager;
    }
    
    public void progressReported(long _current, long _maximum) {
	if (myProgressListener != null)
	    myProgressListener.progressReported(_current, _maximum);
    }
    
    public void progressReported(String _description, long _current,
				 long _maximum) {
	if (myProgressListener != null)
	    myProgressListener.progressReported(_description, _current,
						_maximum);
    }
    
    public IConnection getConnection() {
	return myConnection;
    }
    
    public void open() throws ProtocolException {
	try {
	    if (myConnectionRefCount == 0) {
		Throwable lastFailure = null;
		boolean opened = false;
		for (int i = 0; !opened && i < myMaxAttempts; i++) {
		    try {
			myConnection.open();
			myRequest.checkProtocolVersion();
			opened = true;
		    } catch (Throwable t) {
			lastFailure = t;
			Debug.println(8, "Attempt #" + i + "...");
			Debug.println(8, t);
			try {
			    myConnection.close();
			} catch (Throwable throwable) {
			    /* empty */
			}
		    }
		}
		if (!opened && lastFailure != null)
		    throw lastFailure;
	    }
	    myConnectionRefCount++;
	} catch (Throwable e) {
	    throw new ProtocolException
		      (new ResourceBundleKey("errors", "protocol.openFailed"),
		       e);
	}
    }
    
    public void close() throws ProtocolException {
	if (myConnectionRefCount == 0)
	    Debug.println
		(8,
		 "You attempted to close a connection that was already closed.");
	else {
	    myConnectionRefCount--;
	    if (myConnectionRefCount == 0) {
		try {
		    myConnection.close();
		} catch (ConnectionException e) {
		    Debug.println(e);
		}
	    }
	}
    }
    
    public ProtocolVersion getProtocolVersion() throws ProtocolException {
	return myRequest.getVersion();
    }
    
    public void checkProtocolVersion() throws ProtocolException {
	myRequest.checkProtocolVersion();
    }
    
    public boolean isDeviceConnected() {
	boolean deviceConnected;
	try {
	    myRequest.getVersion();
	    deviceConnected = true;
	} catch (ProtocolException e) {
	    deviceConnected = false;
	}
	return deviceConnected;
    }
    
    public void waitForDevice(int _retries) throws ProtocolException {
	boolean deviceConnected = false;
	for (int i = 0; !deviceConnected && i < 10; i++) {
	    try {
		myRequest.getVersion();
		deviceConnected = true;
		if (!deviceConnected)
		    Thread.sleep(1000L);
	    } catch (Exception exception) {
		/* empty */
	    }
	}
	if (!deviceConnected)
	    throw new ProtocolException
		      (new ResourceBundleKey("errors",
					     "protocol.deviceNotConnected"));
    }
    
    public IDeviceSettings getDeviceSettings() throws ProtocolException {
	open();
	PearlDeviceSettings pearldevicesettings;
	try {
	    Properties deviceSettings = myRequest.getDeviceSettings();
	    PearlDeviceSettings pearldevicesettings_0_
		= new PearlDeviceSettings();
	    pearldevicesettings_0_.fromProperties("", deviceSettings);
	    pearldevicesettings_0_.setDirty(false);
	    pearldevicesettings = pearldevicesettings_0_;
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
	return pearldevicesettings;
    }
    
    public void changeDeviceSettings(IDeviceSettings _deviceSettings)
	throws ProtocolException {
	open();
	try {
	    Properties deviceSettings = _deviceSettings.toProperties("");
	    myRequest.changeDeviceSettings(deviceSettings);
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
    }
    
    public DeviceInfo getDeviceInfo() throws ProtocolException {
	open();
	DeviceInfo deviceinfo;
	try {
	    DeviceInfo deviceInfo = myRequest.getDeviceInfo();
	    deviceinfo = deviceInfo;
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
	return deviceinfo;
    }
    
    public StorageInfo getStorageInfo(int _which) throws ProtocolException {
	open();
	StorageInfo storageinfo;
	try {
	    StorageInfo storageInfo = myRequest.getStorageInfo(_which);
	    storageinfo = storageInfo;
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
	return storageinfo;
    }
    
    public void changeFileInfo(long _fid, Properties _fileInfo)
	throws ProtocolException {
	open();
	try {
	    myRequest.changeFileInfo(_fid, _fileInfo);
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
    }
    
    protected void login(ResourceBundleKey _prompt) throws ProtocolException {
	String password = myPasswordManager.getPassword();
	if (password == null)
	    password = "";
	boolean succeeded = isLoginSuccessful(password);
	for (int attempt = 0; !succeeded && attempt < myMaxAttempts;
	     attempt++) {
	    PasswordAuthentication authentication
		= AuthenticatorFactory.getInstance()
		      .requestPassword(_prompt.getString());
	    password = authentication.getPassword();
	    succeeded = isLoginSuccessful(password);
	    if (succeeded)
		myPasswordManager.setPassword(authentication.getPassword(),
					      authentication
						  .isSavePasswordRequested());
	}
	if (!succeeded)
	    throw new ProtocolException
		      (new ResourceBundleKey("errors",
					     "protocol.maxPasswordAttempts"));
    }
    
    protected boolean isLoginSuccessful(String _password)
	throws ProtocolException {
	boolean succeeded;
	try {
	    myRequest.login(_password);
	    succeeded = true;
	} catch (InvalidPasswordException e) {
	    succeeded = false;
	}
	return succeeded;
    }
    
    public void readLock() throws ProtocolException {
	open();
	try {
	    boolean succeeded = false;
	    int attempt = 0;
	    while (!succeeded) {
		if (attempt > myMaxAttempts)
		    break;
		try {
		    myRequest.readLock();
		    succeeded = true;
		} catch (StatusFailedException e) {
		    if (attempt != myMaxAttempts
			&& e instanceof NotLoggedInException)
			login
			    (new ResourceBundleKey("ui",
						   "protocol.readLockPrompt"));
		    else
			throw e;
		}
		attempt++;
	    }
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
    }
    
    public void writeLock() throws ProtocolException {
	open();
	try {
	    boolean succeeded = false;
	    int attempt = 0;
	    while (!succeeded) {
		if (attempt > myMaxAttempts)
		    break;
		try {
		    myRequest.writeLock();
		    succeeded = true;
		} catch (StatusFailedException e) {
		    if (attempt != myMaxAttempts
			&& e instanceof NotLoggedInException)
			login(new ResourceBundleKey
			      ("ui", "protocol.writeLockPrompt"));
		    else
			throw e;
		}
		attempt++;
	    }
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
    }
    
    public void unlock() throws ProtocolException {
	open();
	try {
	    myRequest.unlock();
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
    }
    
    public long getLength(long _fid) throws ProtocolException {
	open();
	long l;
	try {
	    Properties fileInfoProps = myRequest.getFileInfo(_fid);
	    NodeTags nodeTags = new NodeTags(fileInfoProps);
	    long length = nodeTags.getLongValue("length", 0L);
	    l = length;
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
	return l;
    }
    
    public Properties getFileInfo(long _fid) throws ProtocolException {
	open();
	Properties properties;
	try {
	    Properties fileInfoProps = myRequest.getFileInfo(_fid);
	    properties = fileInfoProps;
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
	return properties;
    }
    
    public void prepare(long _fid, long _size, long _storage)
	throws ProtocolException {
	open();
	try {
	    myRequest.prepare(_fid, _size, _storage);
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
    }
    
    public void format(int _storage) throws ProtocolException {
	open();
	try {
	    myRequest.format(_storage);
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
    }
    
    public void read(long _fid, long _offset, long _size,
		     OutputStream _targetStream,
		     long _totalSize) throws ProtocolException {
	open();
	try {
	    myRequest.read(_fid, _offset, _size, _targetStream, _totalSize,
			   myProgressListener);
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
    }
    
    public void write(long _storage, long _fid, long _offset, long _size,
		      InputStream _sourceStream,
		      long _totalSize) throws ProtocolException {
	open();
	try {
	    myRequest.write(_storage, _fid, _offset, _size, _sourceStream,
			    _totalSize, myProgressListener);
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
    }
    
    public void delete(long _fid, boolean _failIfMissing)
	throws ProtocolException {
	open();
	try {
	    myRequest.delete(_fid, _failIfMissing);
	} catch (Object object) {
	    close();
	    throw object;
	}
	close();
    }
    
    public void playAppend(long _fid) throws ProtocolException {
	open();
	close();
    }
    
    public void playInsert(long _fid) throws ProtocolException {
	open();
	close();
    }
    
    public void playReplace(long _fid) throws ProtocolException {
	open();
	close();
    }
    
    public void pause() throws ProtocolException {
	open();
	close();
    }
    
    public void nextTrack() throws ProtocolException {
	open();
	close();
    }
    
    public void prevTrack() throws ProtocolException {
	open();
	close();
    }
    
    public String getPlayerType() throws ProtocolException {
	open();
	close();
	return null;
    }
    
    public FileInfoEnumeration getAllFileInfo() throws ProtocolException {
	GetAllFileInfoReplyPacket getAllFileInfoReply
	    = myRequest.getAllFileInfo();
	return getAllFileInfoReply.getFileInfoEnumeration();
    }
}
