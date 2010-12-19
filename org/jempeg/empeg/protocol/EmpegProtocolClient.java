/* EmpegProtocolClient - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.StringTokenizer;

import com.inzyme.exception.ExceptionUtils;
import com.inzyme.exception.MethodNotImplementedException;
import com.inzyme.io.StreamUtils;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;
import com.inzyme.util.Debug;

import org.jempeg.empeg.protocol.packet.MountRequestPacket;
import org.jempeg.empeg.protocol.packet.StatFSResponsePacket;
import org.jempeg.empeg.protocol.packet.TCPFastHeader;
import org.jempeg.empeg.util.HijackUtils;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.protocol.ConnectionException;
import org.jempeg.protocol.DeviceInfo;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.ProtocolVersion;
import org.jempeg.protocol.StorageInfo;

public class EmpegProtocolClient
    implements IProtocolClient, ISimpleProgressListener
{
    public static final int PROTOCOL_TCP_PORT = 8300;
    public static final int RESTART_CONNECTION_RETRIES = 3;
    public static final int DEFAULT_MAX_RETRIES = 16;
    public static final int PROTOCOL_VERSION_MAJOR = 6;
    public static final int PROTOCOL_VERSION_MINOR = 0;
    private IConnection myConn;
    private IConnection myFastConnection;
    private int myProtocolVersionMajor;
    private int myProtocolVersionMinor;
    private int myMaximumRetryCount;
    private ISimpleProgressListener myProgressListener;
    private String myPassword;
    private boolean myUseHijack;
    private boolean myUseNewFidLayout;
    private static final boolean DEBUG = false;
    
    public EmpegProtocolClient(IConnection _connection,
			       ISimpleProgressListener _progressListener) {
	myConn = _connection;
	myProgressListener = _progressListener;
	myProtocolVersionMajor = -1;
	myProtocolVersionMinor = -1;
	myMaximumRetryCount = 16;
    }
    
    public void setPassword(String _password) {
	myPassword = _password;
	System.out.println("EmpegProtocolClient.setPassword: " + myPassword);
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
    
    public DeviceInfo getDeviceInfo() throws ProtocolException {
	throw new RuntimeException
		  ("getDeviceInfo is not implemented for the Empeg.");
    }
    
    public Properties getFileInfo(long _fid) throws ProtocolException {
	throw new RuntimeException
		  ("getFileInfo is not implemented for the Empeg.");
    }
    
    public void delete(long _fid, boolean _failIfMissing)
	throws ProtocolException {
	deleteFID(_fid, 3L);
    }
    
    public IDeviceSettings getDeviceSettings() throws ProtocolException {
	String s;
	try {
	    byte[] data = readFIDToMemory(6L);
	    try {
		s = new String(data, "ISO-8859-1");
	    } catch (UnsupportedEncodingException e) {
		Debug.println(e);
		s = new String(data);
	    }
	} catch (FileNotFoundException e) {
	    Debug.println
		(8,
		 "The player configuration file config.ini didn't exist, nomatter.");
	    s = "";
	}
	IDeviceSettings gp = new EmpegDeviceSettings(s);
	return gp;
    }
    
    public void changeDeviceSettings(IDeviceSettings _deviceSettings)
	throws ProtocolException {
	String configStr = _deviceSettings.toString();
	byte[] bytez;
	try {
	    bytez = configStr.getBytes("ISO-8859-1");
	} catch (UnsupportedEncodingException e) {
	    Debug.println(e);
	    bytez = configStr.getBytes();
	}
	writeFIDFromMemory(6L, bytez);
    }
    
    public long getLength(long _fid) throws ProtocolException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	progressReported(0L, 1L);
	long result = -1L;
	boolean succeeded = false;
	int retryCount = 0;
	while (!succeeded) {
	    try {
		result = r.statFID(new UINT32(_fid)).getValue();
		succeeded = true;
	    } catch (FileNotFoundException e) {
		throw new ProtocolException("There is no FID #" + _fid + ".",
					    e);
	    } catch (Throwable t) {
		errorAction(t, retryCount);
	    }
	    retryCount++;
	}
	progressReported(1L, 1L);
	close0(wasOpen);
	return result;
    }
    
    public boolean isDeviceConnected() {
	boolean connected = false;
	boolean wasOpen = false;
	try {
	    wasOpen = open0();
	    myConn.flushReceiveBuffer();
	    EmpegRequest req = new EmpegRequest(myConn, this);
	    req.ping();
	    connected = true;
	    getProtocolVersion0(req);
	} catch (Throwable t) {
	    Debug.println(t);
	    connected = false;
	}
	try {
	    close0(wasOpen);
	} catch (Throwable t) {
	    Debug.println(t);
	}
	return connected;
    }
    
    public void nextTrack() throws ProtocolException {
	throw new MethodNotImplementedException();
    }
    
    public void pause() throws ProtocolException {
	sendCommand(4L, -1L);
    }
    
    public void playAppend(long _fid) throws ProtocolException {
	playFID(_fid, 1);
    }
    
    public void playInsert(long _fid) throws ProtocolException {
	playFID(_fid, 0);
    }
    
    public void playReplace(long _fid) throws ProtocolException {
	playFID(_fid, 2);
    }
    
    public void prepare(long _fid, long _size, long _storage)
	throws ProtocolException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	boolean succeeded = false;
	int retryCount = 0;
	while (!succeeded) {
	    try {
		r.prepareFID(new UINT32(_fid), new UINT32(_size)).getValue();
		succeeded = true;
	    } catch (Throwable t) {
		errorAction(t, retryCount);
	    }
	    retryCount++;
	}
	close0(wasOpen);
    }
    
    public void prevTrack() throws ProtocolException {
	throw new MethodNotImplementedException();
    }
    
    public void read(long _fid, long _offset, long _size,
		     OutputStream _targetStream,
		     long _totalSize) throws ProtocolException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	r.newRequest();
	progressReported(_offset, _totalSize);
	if (_size > 0L) {
	    long pos = 0L;
	    do {
		byte[] chunk = null;
		boolean succeeded = false;
		int retryCount = 0;
		while (!succeeded) {
		    try {
			chunk
			    = r.readFID
				  (new UINT32(_fid), new UINT32(pos + _offset),
				   (new UINT32
				    (Math.min((long) myConn.getPacketSize(),
					      _size - pos))))
				  .getValue();
			_targetStream.write(chunk);
			r.newRequest();
			pos += (long) chunk.length;
			progressReported(pos + _offset, _totalSize);
			succeeded = true;
		    } catch (Throwable t) {
			errorAction(t, retryCount);
		    }
		    retryCount++;
		}
	    } while (pos < _size);
	}
	close0(wasOpen);
    }
    
    public void readLock() throws ProtocolException {
	/* empty */
    }
    
    public void unlock() throws ProtocolException {
	lockUI(false);
	enableWrite(false);
    }
    
    public void write(long _storage, long _fid, long _offset, long _size,
		      InputStream _sourceStream,
		      long _totalSize) throws ProtocolException {
	writeFID(_fid, _offset, _sourceStream, _size, true);
    }
    
    public void writeLock() throws ProtocolException {
	lockUI(true);
	enableWrite(true);
    }
    
    protected void playFID(long _fid, int _mode) throws ProtocolException {
	sendCommand(3L, _fid, (long) _mode);
    }
    
    protected void playFIDs(long[] _fids, int _mode) throws ProtocolException {
	for (int i = 0; i < _fids.length; i++)
	    playFID(_fids[i], _mode == 2 && i > 0 ? 1 : _mode);
    }
    
    public void startFastConnection() {
	try {
	    if (myFastConnection == null
		&& PropertiesManager.getInstance()
		       .getBooleanProperty("jempeg.useFastConnection")) {
		IConnection fastConnection = myConn.getFastConnection();
		if (fastConnection != null) {
		    fastConnection.open();
		    myFastConnection = fastConnection;
		}
	    }
	} catch (Exception e) {
	    Debug.println(8, e);
	}
    }
    
    public void stopFastConnection() throws ConnectionException {
	if (myFastConnection != null) {
	    IConnection fastConnection = myFastConnection;
	    myFastConnection = null;
	    fastConnection.close();
	}
    }
    
    protected IConnection getFastConnection() {
	return myFastConnection;
    }
    
    public IConnection getConnection() {
	return myConn;
    }
    
    public void open() throws ProtocolException {
	try {
	    myConn.open();
	    myUseHijack
		= myUseHijack || HijackUtils.shouldUseHijack(myConn) == null;
	    try {
		myUseNewFidLayout
		    = (myUseNewFidLayout
		       || myUseHijack && (HijackUtils.isNewFidLayout
					  (HijackUtils.getAddress(myConn))));
	    } catch (IOException e) {
		Debug.println
		    (8,
		     "Failed to determine if new FID layout should be used.");
		myUseNewFidLayout = false;
	    }
	} catch (Exception e) {
	    throw new ProtocolException("Unable to open connection to device.",
					e);
	}
    }
    
    public void close() throws ProtocolException {
	try {
	    myConn.close();
	} catch (Exception e) {
	    throw new ProtocolException
		      ("Unable to close connection to device.", e);
	}
    }
    
    public void setMaximumRetryCount(int _maximumRetryCount) {
	myMaximumRetryCount = _maximumRetryCount;
    }
    
    public synchronized byte[] readFIDPartial
	(long _fid, long _offset, long _requiredSize)
	throws ProtocolException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	_requiredSize = Math.min(_requiredSize, (long) myConn.getPacketSize());
	CharArray buffer = null;
	boolean succeeded = false;
	int retryCount = 0;
	while (!succeeded) {
	    try {
		buffer = r.readFID(new UINT32(_fid), new UINT32(_offset),
				   new UINT32(_requiredSize));
		succeeded = true;
	    } catch (Throwable t) {
		errorAction(t, retryCount);
	    }
	    retryCount++;
	}
	close0(wasOpen);
	return buffer.getValue();
    }
    
    public byte[] readFIDToMemory(long _fid)
	throws ProtocolException, FileNotFoundException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	readFID(_fid, baos);
	byte[] buffer = baos.toByteArray();
	return buffer;
    }
    
    private byte[] readDebug(long _fid)
	throws ProtocolException, FileNotFoundException {
	File root = new File("c:\\empeghis2");
	InputStream is;
	if (_fid == 6L)
	    is = new FileInputStream(new File(root, "config.ini"));
	else if (_fid == 3L)
	    is = new FileInputStream(new File(root, "database3"));
	else if (_fid == 2L)
	    is = new FileInputStream(new File(root, "tags"));
	else if (_fid == 5L)
	    is = new FileInputStream(new File(root, "playlists"));
	else if (_fid == 4L)
	    is = new ByteArrayInputStream(new byte[0]);
	else
	    throw new FileNotFoundException("No FID: " + _fid);
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	try {
	    try {
		StreamUtils.copy(is, baos);
		baos.flush();
		baos.close();
	    } catch (IOException e) {
		throw new ProtocolException("Failed: " + _fid, e);
	    }
	} catch (Object object) {
	    try {
		is.close();
	    } catch (Throwable t) {
		ExceptionUtils.printChainedStackTrace(t);
	    }
	    throw object;
	}
	try {
	    is.close();
	} catch (Throwable t) {
	    ExceptionUtils.printChainedStackTrace(t);
	}
	return baos.toByteArray();
    }
    
    public byte[] readFIDToMemory2(long _fid)
	throws ProtocolException, FileNotFoundException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	readFID2(_fid, baos);
	byte[] buffer = baos.toByteArray();
	return buffer;
    }
    
    protected synchronized void readFID(long _fid, OutputStream _os)
	throws ProtocolException, FileNotFoundException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	progressReported(0L, 1L);
	long size = -1L;
	boolean succeeded = false;
	int retryCount = 0;
	while (!succeeded) {
	    try {
		size = r.statFID(new UINT32(_fid)).getValue();
		size = (long) (int) size;
		succeeded = true;
	    } catch (FileNotFoundException e) {
		throw e;
	    } catch (Throwable t) {
		errorAction(t, retryCount);
	    }
	    retryCount++;
	}
	if (size < 0L)
	    throw new FileNotFoundException("There isn't a file with FID "
					    + _fid + " on this device.");
	r.newRequest();
	progressReported(1L, 1L);
	read(_fid, 0L, size, _os, size);
	close0(wasOpen);
    }
    
    protected synchronized void readFID2(long _fid, OutputStream _os)
	throws ProtocolException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	boolean succeeded = false;
	long reportSize = 8192L;
	long reportOffset = 0L;
	long requestedChunkSize = (long) myConn.getPacketSize();
	progressReported(0L, reportSize);
	long offset = 0L;
	byte[] chunk = null;
	do {
	    succeeded = false;
	    int retryCount = 0;
	    while (!succeeded) {
		try {
		    chunk
			= r.readFID(new UINT32(_fid), new UINT32(offset),
				    new UINT32(requestedChunkSize)).getValue();
		    _os.write(chunk);
		    r.newRequest();
		    offset += (long) chunk.length;
		    progressReported(reportOffset, reportSize);
		    reportOffset += (reportSize - reportOffset) / 32L;
		    succeeded = true;
		} catch (Throwable t) {
		    errorAction(t, retryCount);
		}
		retryCount++;
	    }
	} while ((long) chunk.length == requestedChunkSize);
	close0(wasOpen);
    }
    
    public void writeFIDPartial(long _fid, long _initialOffset, byte[] _bytes,
				int _offset,
				int _length) throws ProtocolException {
	ByteArrayInputStream bais
	    = new ByteArrayInputStream(_bytes, _offset, _length);
	writeFID(_fid, _initialOffset, bais, (long) _length, false);
    }
    
    protected void writeFIDPartial(long _fid, long _initialOffset,
				   byte[] _bytes) throws ProtocolException {
	ByteArrayInputStream bais = new ByteArrayInputStream(_bytes);
	writeFID(_fid, _initialOffset, bais, (long) _bytes.length, false);
    }
    
    public void writeFIDFromMemory(long _fid, byte[] _bytes)
	throws ProtocolException {
	ByteArrayInputStream bais = new ByteArrayInputStream(_bytes);
	writeFID(_fid, 0L, bais, (long) _bytes.length, false);
    }
    
    public void writeFID(long _fid, InputStream _is, long _size,
			 boolean _fastConnection) throws ProtocolException {
	writeFID(_fid, 0L, _is, _size, _fastConnection);
    }
    
    protected synchronized void writeFID
	(long _fid, long _initialOffset, InputStream _is, long _size,
	 boolean _fastConnection)
	throws ProtocolException {
	do {
	    if (myUseHijack && _fid >= 288L && isDeviceConnected()) {
		try {
		    PingProgressListener pingProgressListener
			= new PingProgressListener(this, myProgressListener);
		    HijackUtils.uploadFid(HijackUtils.getAddress(myConn), _fid,
					  _is, _initialOffset, _size,
					  pingProgressListener,
					  myUseNewFidLayout,
					  getMostEmptyDriveNumber());
		    break;
		} catch (IOException e) {
		    throw new ProtocolException
			      ("Failed to write FID using Hijack.", e);
		}
	    }
	    try {
		boolean wasOpen = open0();
		EmpegRequest r = new EmpegRequest(myConn, this);
		long maxProgress = _size + _size / 4L;
		progressReported(0L, maxProgress);
		boolean succeeded = false;
		int retryCount = 0;
		while (!succeeded) {
		    try {
			r.prepareFID(new UINT32(_fid), new UINT32(_size));
			succeeded = true;
		    } catch (Throwable t) {
			errorAction(t, retryCount);
		    }
		    retryCount++;
		}
		progressReported(_size / 4L, maxProgress);
		IConnection fastConn = null;
		if (_fastConnection)
		    fastConn = getFastConnection();
		if (fastConn != null) {
		    System.out.println
			("EmpegProtocolClient.writeFID: using fastwrite");
		    TCPFastHeader header = new TCPFastHeader();
		    header.setOperation(new UINT32(0L));
		    header.setFID(new UINT32(_fid));
		    header.setOffset(new UINT32(_initialOffset));
		    header.setSize(new UINT32(_size));
		    LittleEndianOutputStream eos = fastConn.getOutputStream();
		    header.write(eos);
		    byte[] buffer = new byte[fastConn.getPacketSize()];
		    long offset = 0L;
		    for (;;) {
			int bytesRead = _is.read(buffer, 0, buffer.length);
			if (bytesRead == -1)
			    break;
			eos.write(buffer, 0, bytesRead);
			offset += (long) bytesRead;
			progressReported(offset + _size / 4L, maxProgress);
		    }
		    eos.flush();
		} else {
		    long offset = 0L;
		    do {
			r.newRequest();
			CharArray chunk
			    = new CharArray(Math.min(myConn.getPacketSize(),
						     (int) (_size - offset)));
			for (long chunkOffset = 0L;
			     chunkOffset < (long) chunk.getLength();
			     chunkOffset
				 += (long) (_is.read
					    (chunk.getValue(),
					     (int) chunkOffset,
					     (int) ((long) chunk.getLength()
						    - chunkOffset)))) {
			    /* empty */
			}
			succeeded = false;
			int retryCount_0_ = 0;
			while (!succeeded) {
			    try {
				r.writeFID(new UINT32(_fid),
					   new UINT32(_initialOffset + offset),
					   chunk);
				succeeded = true;
			    } catch (Throwable t) {
				errorAction(t, retryCount_0_);
			    }
			    retryCount_0_++;
			}
			offset += (long) chunk.getLength();
			progressReported(offset + _size / 4L, maxProgress);
		    } while (offset < _size);
		}
		progressReported(maxProgress, maxProgress);
		close0(wasOpen);
	    } catch (Exception e) {
		throw new ProtocolException("Unable to write data to device.",
					    e);
	    }
	} while (false);
    }
    
    public void checkProtocolVersion() throws ProtocolException {
	if (myProtocolVersionMajor >= 0 && myProtocolVersionMajor != 6) {
	    if (myProtocolVersionMajor > 6)
		throw new ProtocolException
			  ("Empeg is using a protocol newer than the one I am using.");
	    Debug.println
		(8,
		 "Empeg is using a protocol that is older than the one I am using.");
	}
    }
    
    public void waitForDevice(int _retries) throws ProtocolException {
	int i;
	for (i = 0; i < _retries; i++) {
	    if (isDeviceConnected())
		break;
	    try {
		Thread.sleep(1000L);
	    } catch (Exception exception) {
		/* empty */
	    }
	}
	if (i == _retries)
	    throw new ProtocolException
		      ("There is no device currently connected.");
    }
    
    protected void waitForUnitConnected() throws ProtocolException {
	waitForUnitConnected(90000L);
    }
    
    protected synchronized void waitForUnitConnected(long _timeout)
	throws ProtocolException {
	try {
	    myConn.pause();
	} catch (ConnectionException e) {
	    throw new ProtocolException
		      ("Unable to pause connection to device.", e);
	}
	try {
	    myConn.unpause();
	} catch (Throwable throwable) {
	    /* empty */
	}
	boolean unitConnected = false;
	long startTime = System.currentTimeMillis();
	long curTime = 0L;
	EmpegRequest req = new EmpegRequest(myConn, this);
	do {
	    curTime = System.currentTimeMillis() - startTime;
	    progressReported(curTime, _timeout);
	    if (!myConn.isOpen()) {
		try {
		    myConn.unpause();
		} catch (Throwable t) {
		    Debug.println(t);
		}
	    }
	    try {
		req.ping();
		getProtocolVersion0(req);
		unitConnected = true;
	    } catch (Throwable t) {
		Debug.println(t);
	    }
	} while (!unitConnected && !myConn.isOpen() && curTime < _timeout);
	progressReported(_timeout, _timeout);
    }
    
    public synchronized void enableWrite(boolean _writeAccess)
	throws ProtocolException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	progressReported(0L, 1L);
	boolean succeeded = false;
	int retryCount = 0;
	while (!succeeded) {
	    try {
		r.mount(_writeAccess ? MountRequestPacket.MODE_READWRITE
			: MountRequestPacket.MODE_READONLY);
		succeeded = true;
	    } catch (Throwable t) {
		errorAction(t, retryCount);
	    }
	    retryCount++;
	}
	progressReported(1L, 1L);
	close0(wasOpen);
    }
    
    public synchronized void rebuildPlayerDatabase(long _options)
	throws ProtocolException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	boolean succeeded = false;
	int retryCount = 0;
	while (!succeeded) {
	    try {
		r.buildAndSaveDatabase(new UINT32(_options));
		succeeded = true;
	    } catch (Throwable t) {
		errorAction(t, retryCount);
	    }
	    retryCount++;
	}
	progressReported(1L, 1L);
	close0(wasOpen);
    }
    
    public synchronized void deleteFID(long _fid, long _idMask)
	throws ProtocolException {
	do {
	    if (myUseHijack && _fid >= 288L && isDeviceConnected()) {
		try {
		    progressReported(0L, 1L);
		    HijackUtils.deleteFid(HijackUtils.getAddress(myConn),
					  _fid);
		    progressReported(1L, 1L);
		    break;
		} catch (Exception e) {
		    throw new ProtocolException
			      ("Unable to delete fid from device.", e);
		}
	    }
	    boolean wasOpen = open0();
	    EmpegRequest r = new EmpegRequest(myConn, this);
	    progressReported(0L, 1L);
	    boolean succeeded = false;
	    int retryCount = 0;
	    while (!succeeded) {
		try {
		    r.deleteFID(new UINT32(_fid), new UINT32(_idMask));
		    succeeded = true;
		} catch (Throwable t) {
		    errorAction(t, retryCount);
		}
		retryCount++;
	    }
	    progressReported(1L, 1L);
	    close0(wasOpen);
	} while (false);
    }
    
    public synchronized int getMostEmptyDriveNumber()
	throws ProtocolException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	StatFSResponsePacket diskInfo = null;
	boolean succeeded = false;
	int retryCount = 0;
	while (!succeeded) {
	    try {
		diskInfo = r.freeSpace();
		succeeded = true;
	    } catch (Throwable t) {
		errorAction(t, retryCount);
	    }
	    retryCount++;
	}
	close0(wasOpen);
	long blockSize0 = diskInfo.getDrive0BlockSize().getValue();
	long blockSize1 = diskInfo.getDrive1BlockSize().getValue();
	long freeSpace0 = diskInfo.getDrive0Space().getValue() * blockSize0;
	long freeSpace1 = diskInfo.getDrive1Space().getValue() * blockSize1;
	return freeSpace0 > freeSpace1 ? 0 : 1;
    }
    
    public synchronized StorageInfo getStorageInfo(int _which)
	throws ProtocolException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	progressReported(0L, 1L);
	StatFSResponsePacket diskInfo = null;
	boolean succeeded = false;
	int retryCount = 0;
	while (!succeeded) {
	    try {
		diskInfo = r.freeSpace();
		succeeded = true;
	    } catch (Throwable t) {
		errorAction(t, retryCount);
	    }
	    retryCount++;
	}
	progressReported(1L, 1L);
	close0(wasOpen);
	long blockSize0 = diskInfo.getDrive0BlockSize().getValue();
	long blockSize1 = diskInfo.getDrive1BlockSize().getValue();
	long totalSize = (diskInfo.getDrive0Size().getValue() * blockSize0
			  + diskInfo.getDrive1Size().getValue() * blockSize1);
	long totalFree = (diskInfo.getDrive0Space().getValue() * blockSize0
			  + diskInfo.getDrive1Space().getValue() * blockSize1);
	StorageInfo storageInfo
	    = new StorageInfo(-1L, totalSize, totalFree, -1L);
	return storageInfo;
    }
    
    protected synchronized void fsck(String _drive, boolean _force)
	throws ProtocolException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	long result = -1L;
	boolean succeeded = false;
	int retryCount = 0;
	while (!succeeded) {
	    try {
		myConn.flushReceiveBuffer();
		result = r.fsck
			     (_drive, new UINT32((long) (_force ? 1 : 0)))
			     .getValue();
		succeeded = true;
	    } catch (Throwable t) {
		errorAction(t, retryCount);
	    }
	    retryCount++;
	}
	boolean bad = false;
	if ((result & 0x1L) != 0L)
	    System.err
		.println("FSCK corrected some errors on drive " + _drive);
	if ((result & 0x2L) != 0L) {
	    System.err.println
		("FSCK thinks the system should be rebooted on drive "
		 + _drive);
	    bad = true;
	}
	if ((result & 0x4L) != 0L) {
	    System.err
		.println("FSCK left stuff uncorrected on drive " + _drive);
	    bad = true;
	}
	if ((result & 0x8L) != 0L) {
	    System.err
		.println("FSCK reported operational error on drive " + _drive);
	    bad = true;
	}
	if ((result & 0x10L) != 0L) {
	    System.err.println("FSCK report usage or syntax error on drive "
			       + _drive);
	    bad = true;
	}
	if ((result & 0x80L) != 0L) {
	    System.err.println("FSCK reported shared library error on drive "
			       + _drive);
	    bad = true;
	}
	if ((result & 0x200L) != 0L)
	    System.err.println("FSCK reported disk is not present on drive "
			       + _drive);
	if (bad)
	    throw new ProtocolException
		      ("FSCK found an serious problem (check logs for explanation).");
	progressReported(1L, 1L);
	close0(wasOpen);
    }
    
    public synchronized void checkMedia(boolean _force)
	throws ProtocolException {
	boolean wasOpen = open0();
	fsck("/dev/hda4", _force);
	fsck("/dev/hdc4", _force);
	close0(wasOpen);
    }
    
    protected void sendCommand(long _command) throws ProtocolException {
	sendCommand(_command, 0L, 0L, "");
    }
    
    protected void sendCommand(long _command, long _parameter0)
	throws ProtocolException {
	sendCommand(_command, _parameter0, 0L, "");
    }
    
    protected void sendCommand(long _command, long _parameter0,
			       long _parameter1) throws ProtocolException {
	sendCommand(_command, _parameter0, _parameter1, "");
    }
    
    protected synchronized void sendCommand
	(long _command, long _parameter0, long _parameter1, String _parameter2)
	throws ProtocolException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	boolean succeeded = false;
	int retryCount = 0;
	while (!succeeded) {
	    try {
		myConn.flushReceiveBuffer();
		r.sendCommand(new UINT32(_command), new UINT32(_parameter0),
			      new UINT32(_parameter1), _parameter2);
		succeeded = true;
	    } catch (Throwable t) {
		errorAction(t, retryCount);
	    }
	    retryCount++;
	}
	close0(wasOpen);
    }
    
    public void restartPlayer(boolean _autoSlumber) throws ProtocolException {
	restartPlayer(_autoSlumber, true);
    }
    
    protected synchronized void restartPlayer
	(boolean _autoSlumber, boolean _waitForCompletion)
	throws ProtocolException {
	long param = 1L;
	if (_autoSlumber)
	    param |= 0xf00L;
	internalRestart(param, _waitForCompletion);
    }
    
    protected void restartUnit(boolean _autoSlumber) throws ProtocolException {
	restartUnit(_autoSlumber, true);
    }
    
    public synchronized void restartUnit
	(boolean _autoSlumber, boolean _waitForCompletion)
	throws ProtocolException {
	long param = 2L;
	if (_autoSlumber)
	    param |= 0xf00L;
	internalRestart(param, _waitForCompletion);
    }
    
    protected void internalRestart(long _param, boolean _waitForCompletion)
	throws ProtocolException {
	sendCommand(0L, _param);
	if (_waitForCompletion) {
	    try {
		Thread.sleep(2000L);
	    } catch (Exception exception) {
		/* empty */
	    }
	    waitForUnitConnected();
	}
    }
    
    public void lockUI(boolean _lock) throws ProtocolException {
	sendCommand(1L, (long) (_lock ? 1 : 0));
    }
    
    protected void setSlumber(boolean _slumber) throws ProtocolException {
	sendCommand(2L, (long) (_slumber ? 1 : 0));
    }
    
    public void setPlayState(long _playState) throws ProtocolException {
	sendCommand(4L, _playState);
    }
    
    public synchronized byte[] grabScreen() throws ProtocolException {
	boolean wasOpen = open0();
	EmpegRequest r = new EmpegRequest(myConn, this);
	byte[] screen = r.grabScreen(new UINT32(0L)).getValue();
	close0(wasOpen);
	return screen;
    }
    
    protected boolean open0() throws ProtocolException {
	boolean isOpen = myConn.isOpen();
	if (!isOpen)
	    open();
	return isOpen;
    }
    
    protected void close0(boolean _wasOpen) throws ProtocolException {
	if (!_wasOpen)
	    close();
    }
    
    public String getPlayerType() throws ProtocolException {
	String type;
	try {
	    byte[] buffer = readFIDToMemory(7L);
	    try {
		type = new String(buffer, 0, buffer.length - 1, "ISO-8859-1");
	    } catch (UnsupportedEncodingException e) {
		Debug.println(e);
		type = new String(buffer, 0, buffer.length - 1);
	    }
	} catch (FileNotFoundException e) {
	    type = "";
	}
	return type;
    }
    
    protected PlayerVersionInfo getVersionInfo() throws ProtocolException {
	try {
	    byte[] buffer = readFIDToMemory(0L);
	    String versionStr;
	    try {
		versionStr = new String(buffer, "ISO-8859-1");
	    } catch (UnsupportedEncodingException e) {
		Debug.println(e);
		versionStr = new String(buffer);
	    }
	    PlayerVersionInfo versionInfo
		= new PlayerVersionInfo(versionStr, "", 1);
	    return versionInfo;
	} catch (FileNotFoundException e) {
	    throw new ProtocolException
		      ("Unable to retrieve player version info.", e);
	}
    }
    
    protected synchronized void getProtocolVersion0(EmpegRequest _request) {
	myProtocolVersionMajor = _request.getProtocolVersionMajor().getValue();
	myProtocolVersionMinor = _request.getProtocolVersionMinor().getValue();
    }
    
    public synchronized ProtocolVersion getProtocolVersion()
	throws ProtocolException {
	if (myProtocolVersionMajor == -1 || myProtocolVersionMinor == -1) {
	    boolean wasOpen = open0();
	    EmpegRequest r = new EmpegRequest(myConn, this);
	    r.ping();
	    getProtocolVersion0(r);
	    close0(wasOpen);
	}
	ProtocolVersion protocolVersion
	    = new ProtocolVersion(myProtocolVersionMajor,
				  myProtocolVersionMinor);
	return protocolVersion;
    }
    
    public PlayerIdentity getPlayerIdentity()
	throws ProtocolException, FileNotFoundException {
	PlayerIdentity playerID = new PlayerIdentity();
	byte[] buffer = readFIDToMemory(1L);
	String idStr;
	try {
	    idStr = new String(buffer, "ISO-8859-1");
	} catch (UnsupportedEncodingException e) {
	    Debug.println(e);
	    idStr = new String(buffer);
	}
	StringTokenizer tokenizer = new StringTokenizer(idStr, "\n");
	while (tokenizer.hasMoreTokens()) {
	    String token = tokenizer.nextToken();
	    int colonIndex = token.indexOf(':');
	    String key = token.substring(0, colonIndex).trim();
	    String value = token.substring(colonIndex + 1).trim();
	    if (key.equals("hwrev"))
		playerID.setHWRev(value);
	    else if (key.equals("serial"))
		playerID.setSerial(value);
	    else if (key.equals("build"))
		playerID.setBuild(value);
	    else if (key.equals("id"))
		playerID.setID(value);
	    else if (key.equals("ram"))
		playerID.setRAM(value);
	    else if (key.equals("flash"))
		playerID.setFlash(value);
	    else
		Debug.println
		    (8, ("getPlayerIdentity: Don't know what to do with key = "
			 + key + "; value = " + value));
	}
	return playerID;
    }
    
    public synchronized void checkDatabaseAvailability()
	throws ProtocolException, FileNotFoundException {
	boolean wasOpen = open0();
	long size = getLength(2L);
	if (size < 16L)
	    throw new ProtocolException
		      ("Tag database is suspiciously small. Failing.");
	size = getLength(3L);
	if (size < 16L)
	    throw new ProtocolException
		      ("Static database is suspiciously small. Failing.");
	size = getLength(5L);
	close0(wasOpen);
    }
    
    public synchronized void deleteDatabases() throws ProtocolException {
	boolean wasOpen = open0();
	deleteFID(3L, 65535L);
	deleteFID(2L, 65535L);
	deleteFID(5L, 65535L);
	close0(wasOpen);
    }
    
    protected void errorAction(Throwable _t, int _retries)
	throws ProtocolException {
	Debug.println(8, ("Retry #" + _retries + " failed. (" + _t.getMessage()
			  + ")"));
	Debug.println(4, _t);
	try {
	    myConn.flushReceiveBuffer();
	} catch (ConnectionException e) {
	    throw new ProtocolException("Unable to flush receive buffer.", e);
	}
	if (_retries == 3) {
	    Debug.println
		(8,
		 "Desperately trying to restart the connection before we give up ...");
	    try {
		myConn.pause();
		myConn.unpause();
	    } catch (Throwable t) {
		Debug.println(t);
	    }
	}
	if (_retries >= myMaximumRetryCount)
	    throw new ProtocolException
		      ("Operation failure exceeded maximum retry count.");
	for (/**/; _retries > 3; _retries--) {
	    try {
		Thread.sleep(1000L);
	    } catch (InterruptedException interruptedexception) {
		/* empty */
	    }
	}
    }
}
