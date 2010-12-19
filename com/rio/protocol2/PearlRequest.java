/* PearlRequest - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;
import com.inzyme.typeconv.UINT64;
import com.inzyme.util.Debug;
import com.rio.protocol2.packet.AbstractReplyPacket;
import com.rio.protocol2.packet.AbstractRequestPacket;
import com.rio.protocol2.packet.ByeReplyPacket;
import com.rio.protocol2.packet.ByeRequestPacket;
import com.rio.protocol2.packet.ChangeDeviceSettingsReplyPacket;
import com.rio.protocol2.packet.ChangeDeviceSettingsRequestPacket;
import com.rio.protocol2.packet.ChangeFileInfoReplyPacket;
import com.rio.protocol2.packet.ChangeFileInfoRequestPacket;
import com.rio.protocol2.packet.DeleteReplyPacket;
import com.rio.protocol2.packet.DeleteRequestPacket;
import com.rio.protocol2.packet.DeviceOperationReplyPacket;
import com.rio.protocol2.packet.DeviceOperationRequestPacket;
import com.rio.protocol2.packet.FIDNotFoundException;
import com.rio.protocol2.packet.FormatReplyPacket;
import com.rio.protocol2.packet.FormatRequestPacket;
import com.rio.protocol2.packet.GetAllFileInfoReplyPacket;
import com.rio.protocol2.packet.GetAllFileInfoRequestPacket;
import com.rio.protocol2.packet.GetDeviceInfoReplyPacket;
import com.rio.protocol2.packet.GetDeviceInfoRequestPacket;
import com.rio.protocol2.packet.GetDeviceSettingsReplyPacket;
import com.rio.protocol2.packet.GetDeviceSettingsRequestPacket;
import com.rio.protocol2.packet.GetFileInfoReplyPacket;
import com.rio.protocol2.packet.GetFileInfoRequestPacket;
import com.rio.protocol2.packet.GetStorageInfoReplyPacket;
import com.rio.protocol2.packet.GetStorageInfoRequestPacket;
import com.rio.protocol2.packet.GetVersionReplyPacket;
import com.rio.protocol2.packet.GetVersionRequestPacket;
import com.rio.protocol2.packet.LockReplyPacket;
import com.rio.protocol2.packet.LockRequestPacket;
import com.rio.protocol2.packet.Login1ReplyPacket;
import com.rio.protocol2.packet.Login1RequestPacket;
import com.rio.protocol2.packet.Login2ReplyPacket;
import com.rio.protocol2.packet.Login2RequestPacket;
import com.rio.protocol2.packet.NakReplyPacket;
import com.rio.protocol2.packet.PacketFactory;
import com.rio.protocol2.packet.PacketHeader;
import com.rio.protocol2.packet.PrepareReplyPacket;
import com.rio.protocol2.packet.PrepareRequestPacket;
import com.rio.protocol2.packet.ProgressReplyPacket;
import com.rio.protocol2.packet.ReadReplyPacket;
import com.rio.protocol2.packet.ReadRequestPacket;
import com.rio.protocol2.packet.UnlockReplyPacket;
import com.rio.protocol2.packet.UnlockRequestPacket;
import com.rio.protocol2.packet.WriteReplyPacket;
import com.rio.protocol2.packet.WriteRequestPacket;

import org.jempeg.protocol.DeviceInfo;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.ProtocolVersion;
import org.jempeg.protocol.StorageInfo;

public class PearlRequest
{
    private IConnection myConnection;
    private ISimpleProgressListener myProtocolProgressListener;
    
    public PearlRequest(IConnection _connection,
			ISimpleProgressListener _protocolProgressListener) {
	myConnection = _connection;
	myProtocolProgressListener = _protocolProgressListener;
    }
    
    protected void writePacket(AbstractRequestPacket _requestPacket)
	throws ProtocolException {
	try {
	    Debug.println(1, ("PearlRequest.writePacket: writing "
			      + _requestPacket));
	    LittleEndianOutputStream eos = myConnection.getOutputStream();
	    _requestPacket.write(eos);
	    eos.flush();
	} catch (Exception e) {
	    throw new ProtocolException
		      (new ResourceBundleKey("errors",
					     "protocol.writePacketFailed"),
		       e);
	}
    }
    
    protected AbstractReplyPacket readReply() throws ProtocolException {
	long timeoutMillis = 20000L;
	AbstractReplyPacket replyPacket = null;
	boolean done = false;
	while (!done) {
	    replyPacket = readPacket(timeoutMillis);
	    if (replyPacket instanceof ProgressReplyPacket) {
		ProgressReplyPacket progressReplyPacket
		    = (ProgressReplyPacket) replyPacket;
		progressUpdated(progressReplyPacket);
	    } else {
		if (replyPacket instanceof NakReplyPacket)
		    throw new ProtocolException
			      (new ResourceBundleKey("errors",
						     "protocol.nak"));
		done = true;
	    }
	}
	return replyPacket;
    }
    
    protected AbstractReplyPacket readPacket(long _timeoutMillis)
	throws ProtocolException {
	try {
	    myConnection.setTimeout(_timeoutMillis);
	    LittleEndianInputStream eis = myConnection.getInputStream();
	    PacketHeader packetHeader = new PacketHeader();
	    packetHeader.read(eis);
	    AbstractReplyPacket replyPacket
		= PacketFactory.createReplyPacket(packetHeader);
	    replyPacket.read(eis);
	    return replyPacket;
	} catch (ProtocolException e) {
	    throw e;
	} catch (Throwable e) {
	    throw new ProtocolException
		      (new ResourceBundleKey("errors",
					     "protocol.readPacketFailed"),
		       e);
	}
    }
    
    protected AbstractReplyPacket writePacketAndReadReply
	(AbstractRequestPacket _requestPacket) throws ProtocolException {
	writePacket(_requestPacket);
	AbstractReplyPacket reply = readReply();
	return reply;
    }
    
    protected void progressUpdated(ProgressReplyPacket _progressReplyPacket) {
	if (myProtocolProgressListener != null)
	    myProtocolProgressListener.progressReported
		(_progressReplyPacket.getNum().getValue(),
		 _progressReplyPacket.getDenom().getValue());
    }
    
    public ProtocolVersion getVersion() throws ProtocolException {
	ProtocolVersion currentVersion = ProtocolVersion.CURRENT_VERSION;
	writePacket(new GetVersionRequestPacket(currentVersion));
	GetVersionReplyPacket getVersionReply
	    = (GetVersionReplyPacket) readReply();
	ProtocolVersion deviceVersion = getVersionReply.getVersion();
	return deviceVersion;
    }
    
    public void checkProtocolVersion() throws ProtocolException {
	ProtocolVersion currentVersion = ProtocolVersion.CURRENT_VERSION;
	ProtocolVersion deviceVersion = getVersion();
	if (!currentVersion.isCompatibleWith(deviceVersion))
	    throw new ProtocolException
		      (new ResourceBundleKey("errors",
					     "protocol.incompatibleVersion",
					     new Object[] { deviceVersion,
							    currentVersion }));
    }
    
    public void login(String _password) throws ProtocolException {
	try {
	    Login1ReplyPacket login1Reply
		= ((Login1ReplyPacket)
		   writePacketAndReadReply(new Login1RequestPacket()));
	    Login2ReplyPacket login2Reply
		= ((Login2ReplyPacket)
		   (writePacketAndReadReply
		    (new Login2RequestPacket(login1Reply.getSalt(),
					     _password))));
	    login2Reply.getStatus();
	} catch (NoSuchAlgorithmException e) {
	    throw new ProtocolException
		      (new ResourceBundleKey("errors",
					     "protocol.passwordEncryptFailed"),
		       e);
	}
    }
    
    public void bye() throws ProtocolException {
	ByeReplyPacket byeReply
	    = (ByeReplyPacket) writePacketAndReadReply(new ByeRequestPacket());
	byeReply.getStatus();
    }
    
    public void changeDeviceSettings(Properties _deviceSettings)
	throws ProtocolException {
	ChangeDeviceSettingsReplyPacket changeDeviceSettingsReply
	    = ((ChangeDeviceSettingsReplyPacket)
	       (writePacketAndReadReply
		(new ChangeDeviceSettingsRequestPacket(_deviceSettings))));
	changeDeviceSettingsReply.getStatus();
    }
    
    public void changeFileInfo(long _fid, Properties _fileInfo)
	throws ProtocolException {
	ChangeFileInfoReplyPacket changeFileInfoReply
	    = ((ChangeFileInfoReplyPacket)
	       writePacketAndReadReply(new ChangeFileInfoRequestPacket
				       (new UINT32(_fid), _fileInfo)));
	changeFileInfoReply.getStatus();
    }
    
    public void delete(long _fid, boolean _failIfMissing)
	throws ProtocolException {
	try {
	    DeleteReplyPacket deleteReply
		= ((DeleteReplyPacket)
		   (writePacketAndReadReply
		    (new DeleteRequestPacket(new UINT32(_fid)))));
	    deleteReply.getStatus();
	} catch (FIDNotFoundException e) {
	    if (_failIfMissing)
		throw e;
	}
    }
    
    public DeviceOperationReplyPacket executeDeviceOperation
	(byte[] _data, IProgressListener _progressListener)
	throws ProtocolException {
	DeviceOperationReplyPacket deviceOperationReply
	    = ((DeviceOperationReplyPacket)
	       (writePacketAndReadReply
		(new DeviceOperationRequestPacket(_data, _progressListener))));
	return deviceOperationReply;
    }
    
    public void format(int _whichStorage) throws ProtocolException {
	FormatReplyPacket formatReply
	    = ((FormatReplyPacket)
	       (writePacketAndReadReply
		(new FormatRequestPacket(new UINT32((long) _whichStorage)))));
	formatReply.getStatus();
    }
    
    public GetAllFileInfoReplyPacket getAllFileInfo()
	throws ProtocolException {
	GetAllFileInfoReplyPacket getAllFileInfoReply
	    = ((GetAllFileInfoReplyPacket)
	       writePacketAndReadReply(new GetAllFileInfoRequestPacket()));
	return getAllFileInfoReply;
    }
    
    public DeviceInfo getDeviceInfo() throws ProtocolException {
	GetDeviceInfoReplyPacket deviceInfoReply
	    = ((GetDeviceInfoReplyPacket)
	       writePacketAndReadReply(new GetDeviceInfoRequestPacket()));
	DeviceInfo deviceInfo = deviceInfoReply.getDeviceInfo();
	return deviceInfo;
    }
    
    public Properties getDeviceSettings() throws ProtocolException {
	GetDeviceSettingsReplyPacket deviceSettingsReply
	    = ((GetDeviceSettingsReplyPacket)
	       writePacketAndReadReply(new GetDeviceSettingsRequestPacket()));
	Properties deviceSettings = deviceSettingsReply.getSettings();
	return deviceSettings;
    }
    
    public Properties getFileInfo(long _fid) throws ProtocolException {
	GetFileInfoReplyPacket getFileInfoReply
	    = ((GetFileInfoReplyPacket)
	       (writePacketAndReadReply
		(new GetFileInfoRequestPacket(new UINT32(_fid)))));
	Properties fileInfo = getFileInfoReply.getFileInfo();
	return fileInfo;
    }
    
    public StorageInfo getStorageInfo(int _whichStorage)
	throws ProtocolException {
	GetStorageInfoReplyPacket getStorageInfoReply
	    = ((GetStorageInfoReplyPacket)
	       writePacketAndReadReply(new GetStorageInfoRequestPacket
				       (new UINT32((long) _whichStorage))));
	StorageInfo storageInfo = getStorageInfoReply.getStorageInfo();
	return storageInfo;
    }
    
    public void readLock() throws ProtocolException {
	LockReplyPacket lockReply
	    = ((LockReplyPacket)
	       writePacketAndReadReply(new LockRequestPacket(LockRequestPacket
							     .READ)));
	lockReply.getStatus();
    }
    
    public void writeLock() throws ProtocolException {
	LockReplyPacket lockReply
	    = ((LockReplyPacket)
	       writePacketAndReadReply(new LockRequestPacket(LockRequestPacket
							     .WRITE)));
	lockReply.getStatus();
    }
    
    public void ping() throws ProtocolException {
	getVersion();
    }
    
    public void prepare(long _fid, long _size, long _storage)
	throws ProtocolException {
	PrepareReplyPacket prepareReply
	    = ((PrepareReplyPacket)
	       (writePacketAndReadReply
		(new PrepareRequestPacket(new UINT64(_size), new UINT32(_fid),
					  new UINT32(_storage)))));
	prepareReply.getStatus();
    }
    
    public long prepare(long _size, long _storage) throws ProtocolException {
	PrepareReplyPacket prepareReply
	    = ((PrepareReplyPacket)
	       (writePacketAndReadReply
		(new PrepareRequestPacket(new UINT64(_size), new UINT32(0L),
					  new UINT32(_storage)))));
	long fid = prepareReply.getFID().getValue();
	return fid;
    }
    
    public void read
	(long _fid, long _offset, long _size, OutputStream _targetStream,
	 long _totalSize, ISimpleProgressListener _progressListener)
	throws ProtocolException {
	try {
	    if (_size > 0L) {
		long maxPacketSize = 2147483647L;
		long bytesRead = 0L;
		do {
		    long packetSize
			= Math.min(maxPacketSize, _size - bytesRead);
		    ReadReplyPacket readReply
			= ((ReadReplyPacket)
			   (writePacketAndReadReply
			    (new ReadRequestPacket(new UINT64(_offset
							      + bytesRead),
						   new UINT64(packetSize),
						   new UINT32(_fid)))));
		    readReply.readInto(_targetStream, _offset, _totalSize,
				       _progressListener);
		    bytesRead += readReply.getSize().getValue();
		    readReply.checkStatus2();
		} while (bytesRead < _size);
	    }
	} catch (IOException e) {
	    throw new ProtocolException
		      (new ResourceBundleKey("errors",
					     "protocol.readPacketFailed"),
		       e);
	}
    }
    
    public void unlock() throws ProtocolException {
	UnlockReplyPacket unlockReply
	    = ((UnlockReplyPacket)
	       writePacketAndReadReply(new UnlockRequestPacket()));
	unlockReply.getStatus();
    }
    
    public void write
	(long _storage, long _fid, long _offset, long _size,
	 InputStream _sourceStream, long _totalSize,
	 ISimpleProgressListener _progressListener)
	throws ProtocolException {
	long maxPacketSize = 2147483647L;
	long bytesWritten = 0L;
	do {
	    long packetSize = Math.min(maxPacketSize, _size - bytesWritten);
	    WriteReplyPacket writeReply
		= ((WriteReplyPacket)
		   (writePacketAndReadReply
		    (new WriteRequestPacket(new UINT64(_offset + bytesWritten),
					    new UINT64(packetSize),
					    new UINT32(_fid),
					    new UINT32(_storage),
					    _sourceStream, _totalSize,
					    _progressListener))));
	    writeReply.getStatus();
	    bytesWritten += packetSize;
	} while (bytesWritten < _size);
    }
}
