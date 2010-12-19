/**
 * This file is licensed under the GPL.
 *
 * See the LICENSE0 file included in this release, or
 * http://www.opensource.org/licenses/gpl-license.html
 * for the details of the license.
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

import org.jempeg.JEmplodeProperties;
import org.jempeg.empeg.protocol.packet.MountRequestPacket;
import org.jempeg.empeg.protocol.packet.StatFSResponsePacket;
import org.jempeg.empeg.protocol.packet.TCPFastHeader;
import org.jempeg.empeg.util.HijackUtils;
import org.jempeg.nodestore.FIDConstants;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.protocol.ConnectionException;
import org.jempeg.protocol.DeviceInfo;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.ProtocolVersion;
import org.jempeg.protocol.StorageInfo;

import com.inzyme.exception.ExceptionUtils;
import com.inzyme.exception.MethodNotImplementedException;
import com.inzyme.io.StreamUtils;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;
import com.inzyme.util.Debug;

/**
 * EmpegProtocolClient defines the communication protocol for the Empeg.
 *
 * @author Mike Schrag
 * @version $Revision: 1.26 $
 */
public class EmpegProtocolClient implements IProtocolClient, ISimpleProgressListener {
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

	public EmpegProtocolClient(IConnection _connection, ISimpleProgressListener _progressListener) {
		myConn = _connection;
		myProgressListener = _progressListener;

		myProtocolVersionMajor = -1;
		myProtocolVersionMinor = -1;

		myMaximumRetryCount = DEFAULT_MAX_RETRIES;
	}

	public void setPassword(String _password) {
		myPassword = _password;
		System.out.println("EmpegProtocolClient.setPassword: " + myPassword);
	}

	public void progressReported(long _current, long _maximum) {
		if (myProgressListener != null) {
			myProgressListener.progressReported(_current, _maximum);
		}
	}

	public void progressReported(String _description, long _current, long _maximum) {
		if (myProgressListener != null) {
			myProgressListener.progressReported(_description, _current, _maximum);
		}
	}

	public DeviceInfo getDeviceInfo() throws ProtocolException {
		throw new RuntimeException("getDeviceInfo is not implemented for the Empeg.");
	}

	public Properties getFileInfo(long _fid) throws ProtocolException {
		throw new RuntimeException("getFileInfo is not implemented for the Empeg.");
	}

	public void delete(long _fid, boolean _failIfMissing) throws ProtocolException {
		deleteFID(_fid, 3);
	}

	public IDeviceSettings getDeviceSettings() throws ProtocolException {
		String s;
		try {
			//Debug.println("ProtocolClient.getPlayerConfiguration()");
			byte[] data = readFIDToMemory(FIDConstants.FID_CONFIGFILE);
			try {
				s = new String(data, "ISO-8859-1");
			}
			catch (UnsupportedEncodingException e) {
				Debug.println(e);
				s = new String(data);
			}
			//Debug.println("ProtocolClient.getPlayerConfiguration() = 0");
		}
		catch (FileNotFoundException e) {
			Debug.println(Debug.WARNING, "The player configuration file config.ini didn't exist, nomatter.");
			s = "";
		}
		IDeviceSettings gp = new EmpegDeviceSettings(s);
		return gp;
	}

	public void changeDeviceSettings(IDeviceSettings _deviceSettings) throws ProtocolException {
		String configStr = _deviceSettings.toString();
		byte[] bytez;
		try {
			bytez = configStr.getBytes("ISO-8859-1");
		}
		catch (UnsupportedEncodingException e) {
			Debug.println(e);
			bytez = configStr.getBytes();
		}
		writeFIDFromMemory(FIDConstants.FID_CONFIGFILE, bytez);
	}

	public long getLength(long _fid) throws ProtocolException {
		boolean wasOpen = open0();
		EmpegRequest r = new EmpegRequest(myConn, this);
		progressReported(0, 1);

		long result = -1;

		boolean succeeded = false;
		for (int retryCount = 0; !succeeded; retryCount++) {
			try {
				result = r.statFID(new UINT32(_fid)).getValue();
				succeeded = true;
			}
			catch (FileNotFoundException e) {
				// don't bother retrying if the file doesn't exist
				throw new ProtocolException("There is no FID #" + _fid + ".", e);
			}
			catch (Throwable t) {
				errorAction(t, retryCount);
			}
		}

		progressReported(1, 1);
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
		}
		catch (Throwable t) {
			Debug.println(t);
			connected = false;
		}
		try {
			close0(wasOpen);
		}
		catch (Throwable t) {
			Debug.println(t);
		}

		return connected;
	}

	public void nextTrack() throws ProtocolException {
		throw new MethodNotImplementedException();
	}

	public void pause() throws ProtocolException {
		sendCommand(EmpegRequest.COM_PLAYSTATE, EmpegRequest.PLAYSTATE_TOGGLE);
	}

	public void playAppend(long _fid) throws ProtocolException {
		playFID(_fid, EmpegRequest.PLAYMODE_APPEND);
	}

	public void playInsert(long _fid) throws ProtocolException {
		playFID(_fid, EmpegRequest.PLAYMODE_INSERT);
	}

	public void playReplace(long _fid) throws ProtocolException {
		playFID(_fid, EmpegRequest.PLAYMODE_REPLACE);
	}

	public void prepare(long _fid, long _size, long _storage) throws ProtocolException {
		boolean wasOpen = open0();
		EmpegRequest r = new EmpegRequest(myConn, this);

		boolean succeeded = false;
		for (int retryCount = 0; !succeeded; retryCount++) {
			try {
				r.prepareFID(new UINT32(_fid), new UINT32(_size)).getValue();
				succeeded = true;
			}
			catch (Throwable t) {
				errorAction(t, retryCount);
			}
		}
		close0(wasOpen);
	}

	public void prevTrack() throws ProtocolException {
		throw new MethodNotImplementedException();
	}

	public void read(long _fid, long _offset, long _size, OutputStream _targetStream, long _totalSize) throws ProtocolException {
		boolean wasOpen = open0();

		//Debug.println("ProtocolClient.readFID: " + _fid);
		EmpegRequest r = new EmpegRequest(myConn, this);

		r.newRequest();

		progressReported(_offset, _totalSize);

		boolean succeeded;
		if (_size > 0) {
			long pos = 0;
			do {
				byte[] chunk = null;
				succeeded = false;
				for (int retryCount = 0; !succeeded; retryCount++) {
					try {
						chunk = r.readFID(new UINT32(_fid), new UINT32(pos + _offset), new UINT32(Math.min(myConn.getPacketSize(), _size - pos))).getValue();

						_targetStream.write(chunk);

						r.newRequest();
						pos += chunk.length;

						progressReported(pos + _offset, _totalSize);

						succeeded = true;
					}
					catch (Throwable t) {
						errorAction(t, retryCount);
					}
				}
			}
			while (pos < _size);
		}

		close0(wasOpen);
		//Debug.println("ProtocolClient.readFID: " + _fid + " Done.");
	}

	public void readLock() throws ProtocolException {
	}

	public void unlock() throws ProtocolException {
		lockUI(false);
		enableWrite(false);
	}

	public void write(long _storage, long _fid, long _offset, long _size, InputStream _sourceStream, long _totalSize) throws ProtocolException {
		writeFID(_fid, _offset, _sourceStream, _size, true);
	}

	public void writeLock() throws ProtocolException {
		lockUI(true);
		enableWrite(true);
	}

	protected void playFID(long _fid, int _mode) throws ProtocolException {
		sendCommand(EmpegRequest.COM_PLAYFID, _fid, _mode);
	}

	protected void playFIDs(long[] _fids, int _mode) throws ProtocolException {
		for (int i = 0; i < _fids.length; i++) {
			playFID(_fids[i], (_mode == EmpegRequest.PLAYMODE_REPLACE && i > 0) ? EmpegRequest.PLAYMODE_APPEND : _mode);
		}
	}

	/**
	 * Start a fast connection for this client.  This connection will be
	 * left open until you call stopFastConnection.
	 * 
	 * If a fast connection cannot be obtained, a warning is logged but
	 * the connection will not stop.  Since fast connections are optional,
	 * it's not worth a failure condition.
	 */
	public void startFastConnection() {
		try {
			if (myFastConnection == null && PropertiesManager.getInstance().getBooleanProperty(JEmplodeProperties.USE_FAST_CONNECTIONS_PROPERTY)) {
				IConnection fastConnection = myConn.getFastConnection();
				if (fastConnection != null) {
					fastConnection.open();
					myFastConnection = fastConnection;
				}
			}
		}
		catch (Exception e) {
			Debug.println(Debug.WARNING, e);
		}
	}

	/**
	 * Stops the fast connection for this client.
	 * 
	 * @throws IOException if the connection cannot be stopped
	 */
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

			myUseHijack = myUseHijack || (HijackUtils.shouldUseHijack(myConn) == null);
			try {
				myUseNewFidLayout = myUseNewFidLayout || (myUseHijack && HijackUtils.isNewFidLayout(HijackUtils.getAddress(myConn)));
			}
			catch (IOException e) {
				Debug.println(Debug.WARNING, "Failed to determine if new FID layout should be used.");
				myUseNewFidLayout = false;
			}
		}
		catch (Exception e) {
			throw new ProtocolException("Unable to open connection to device.", e);
		}
	}

	public void close() throws ProtocolException {
		try {
			myConn.close();
		}
		catch (Exception e) {
			throw new ProtocolException("Unable to close connection to device.", e);
		}
	}

	public void setMaximumRetryCount(int _maximumRetryCount) {
		myMaximumRetryCount = _maximumRetryCount;
	}

	public synchronized byte[] readFIDPartial(long _fid, long _offset, long _requiredSize) throws ProtocolException {
		boolean wasOpen = open0();
		EmpegRequest r = new EmpegRequest(myConn, this);
		_requiredSize = Math.min(_requiredSize, myConn.getPacketSize());

		CharArray buffer = null;

		boolean succeeded = false;
		for (int retryCount = 0; !succeeded; retryCount++) {
			try {
				buffer = r.readFID(new UINT32(_fid), new UINT32(_offset), new UINT32(_requiredSize));
				succeeded = true;
			}
			catch (Throwable t) {
				errorAction(t, retryCount);
			}
		}

		close0(wasOpen);

		//Debug.println("readFidPartial: You asked for " + _requiredSize + " bytes, and I gave you " + buffer.length + " bytes.");
		return buffer.getValue();
	}

	private static final boolean DEBUG = false;

	public byte[] readFIDToMemory(long _fid) throws ProtocolException, FileNotFoundException {
		if (DEBUG) {
			return readDebug(_fid);
		}
		else {
			//Debug.println("ProtocolClient.readFIDToMemory()");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			readFID(_fid, baos);
			byte[] buffer = baos.toByteArray();
			//Debug.println("ProtocolClient.readFIDToMemory() = done");
			return buffer;
		}
	}

	private byte[] readDebug(long _fid) throws ProtocolException, FileNotFoundException {
		InputStream is;

		File root = new File("c:\\empeghis2");
		if (_fid == FIDConstants.FID_CONFIGFILE) {
			is = new FileInputStream(new File(root, "config.ini"));
		}
		else if (_fid == FIDConstants.FID_STATICDATABASE) {
			is = new FileInputStream(new File(root, "database3"));
		}
		else if (_fid == FIDConstants.FID_TAGINDEX) {
			is = new FileInputStream(new File(root, "tags"));
		}
		else if (_fid == FIDConstants.FID_PLAYLISTDATABASE) {
			is = new FileInputStream(new File(root, "playlists"));
		}
		else if (_fid == FIDConstants.FID_DYNAMICDATABASE) {
			is = new ByteArrayInputStream(new byte[0]);
		}
		else {
			throw new FileNotFoundException("No FID: " + _fid);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			StreamUtils.copy(is, baos);
			baos.flush();
			baos.close();
		}
		catch (IOException e) {
			throw new ProtocolException("Failed: " + _fid, e);
		}
		finally {
			try {
				is.close();
			}
			catch (Throwable t) {
				ExceptionUtils.printChainedStackTrace(t);
			}
		}

		return baos.toByteArray();
	}

	public byte[] readFIDToMemory2(long _fid) throws ProtocolException, FileNotFoundException {
		if (DEBUG) {
			return readDebug(_fid);
		}
		else {
			//Debug.println("ProtocolClient.readFIDToMemory()");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			readFID2(_fid, baos);
			byte[] buffer = baos.toByteArray();
			//Debug.println("ProtocolClient.readFIDToMemory() = done");
			return buffer;
		}
	}

	/**
	 * Reads the bytes from the empeg for the given _fid and writes them out to the
	 * given OutputStream. Use ByteArrayOutputStream to read into memory.
	 */
	protected synchronized void readFID(long _fid, OutputStream _os) throws ProtocolException, FileNotFoundException {
		boolean wasOpen = open0();

		//Debug.println("ProtocolClient.readFID: " + _fid);
		EmpegRequest r = new EmpegRequest(myConn, this);

		progressReported(0, 1);
		long size = -1;
		boolean succeeded = false;
		for (int retryCount = 0; !succeeded; retryCount++) {
			try {
				size = r.statFID(new UINT32(_fid)).getValue();
				size = (int) size;
				succeeded = true;
			}
			catch (FileNotFoundException e) {
				// don't bother retrying if the file doesn't exist
				throw e;
			}
			catch (Throwable t) {
				errorAction(t, retryCount);
			}
		}

		if (size < 0) {
			throw new FileNotFoundException("There isn't a file with FID " + _fid + " on this device.");
		}

		r.newRequest();

		progressReported(1, 1);

		read(_fid, 0, size, _os, size);

		close0(wasOpen);
		//Debug.println("ProtocolClient.readFID: " + _fid + " Done.");
	}

	/**
	 * Reads the bytes from the empeg for the given _fid and writes them out to the
	 * given OutputStream. Use ByteArrayOutputStream to read into memory.  This is
	 * different from readFID in that it does not stat the FID prior to downloading,
	 * which is necessary because the HSX-109 does not actually have the database built
	 * on the player, so to determine its size, it is a lengthy process.
	 */
	protected synchronized void readFID2(long _fid, OutputStream _os) throws ProtocolException {
		boolean wasOpen = open0();

		//Debug.println("ProtocolClient.readFID: " + _fid);
		EmpegRequest r = new EmpegRequest(myConn, this);

		boolean succeeded = false;
		long reportSize = 8 * 1024;
		long reportOffset = 0;
		long requestedChunkSize = myConn.getPacketSize();
		progressReported(0, reportSize);

		long offset = 0;
		byte[] chunk = null;
		do {
			succeeded = false;
			for (int retryCount = 0; !succeeded; retryCount++) {
				try {
					chunk = r.readFID(new UINT32(_fid), new UINT32(offset), new UINT32(requestedChunkSize)).getValue();

					_os.write(chunk);

					r.newRequest();
					offset += chunk.length;

					progressReported(reportOffset, reportSize);
					reportOffset += (reportSize - reportOffset) / 32;

					succeeded = true;
				}
				catch (Throwable t) {
					errorAction(t, retryCount);
				}
			}
		}
		while (chunk.length == requestedChunkSize);

		close0(wasOpen);
		//Debug.println("ProtocolClient.readFID: " + _fid + " Done.");
	}

	public void writeFIDPartial(long _fid, long _initialOffset, byte[] _bytes, int _offset, int _length) throws ProtocolException {
		ByteArrayInputStream bais = new ByteArrayInputStream(_bytes, _offset, _length);
		writeFID(_fid, _initialOffset, bais, _length, false);
	}

	protected void writeFIDPartial(long _fid, long _initialOffset, byte[] _bytes) throws ProtocolException {
		ByteArrayInputStream bais = new ByteArrayInputStream(_bytes);
		writeFID(_fid, _initialOffset, bais, _bytes.length, false);
	}

	public void writeFIDFromMemory(long _fid, byte[] _bytes) throws ProtocolException {
		ByteArrayInputStream bais = new ByteArrayInputStream(_bytes);
		writeFID(_fid, 0, bais, _bytes.length, false);
	}

	public void writeFID(long _fid, InputStream _is, long _size, boolean _fastConnection) throws ProtocolException {
		writeFID(_fid, 0, _is, _size, _fastConnection);
	}

	/**
	 * Reads all the bytes from the given InputStream and writes them to the given
	 * _fid on the empeg (starting at the _initialOffset on the empeg).  Use ByteArrayInputStream for in-memory arrays.
	 */
	protected synchronized void writeFID(long _fid, long _initialOffset, InputStream _is, long _size, boolean _fastConnection) throws ProtocolException {
		if (myUseHijack && _fid >= FIDConstants.FID_FIRSTNORMAL && isDeviceConnected()) {
			try {
				PingProgressListener pingProgressListener = new PingProgressListener(this, myProgressListener);
				HijackUtils.uploadFid(HijackUtils.getAddress(myConn), _fid, _is, _initialOffset, _size, pingProgressListener, myUseNewFidLayout, getMostEmptyDriveNumber());
			}
			catch (IOException e) {
				throw new ProtocolException("Failed to write FID using Hijack.", e);
			}
		}
		else {
			try {
				boolean wasOpen = open0();

				EmpegRequest r = new EmpegRequest(myConn, this);

				long maxProgress = _size + _size / 4;
				progressReported(0, maxProgress);

				boolean succeeded = false;
				for (int retryCount = 0; !succeeded; retryCount++) {
					try {
						r.prepareFID(new UINT32(_fid), new UINT32(_size));
						succeeded = true;
					}
					catch (Throwable t) {
						errorAction(t, retryCount);
					}
				}

				progressReported(_size / 4, maxProgress);

				IConnection fastConn = null;
				if (_fastConnection) {
					fastConn = getFastConnection();
				}
				if (fastConn != null) {
					System.out.println("EmpegProtocolClient.writeFID: using fastwrite");
					//    	Debug.println(Debug.VERBOSE, "Attempting fastwrite: " + _size);
					TCPFastHeader header = new TCPFastHeader();
					header.setOperation(new UINT32(0));
					header.setFID(new UINT32(_fid));
					header.setOffset(new UINT32(_initialOffset));
					header.setSize(new UINT32(_size));

					LittleEndianOutputStream eos = fastConn.getOutputStream();
					header.write(eos);

					byte[] buffer = new byte[fastConn.getPacketSize()];
					long offset = 0;
					while (true) {
						int bytesRead = _is.read(buffer, 0, buffer.length);
						if (bytesRead == -1)
							break;
						eos.write(buffer, 0, bytesRead);
						offset += bytesRead;
						progressReported(offset + _size / 4, maxProgress);
					}
					eos.flush();
				}
				else {
					long offset = 0;
					do {
						r.newRequest();
						CharArray chunk = new CharArray(Math.min(myConn.getPacketSize(), (int) (_size - offset)));
						long chunkOffset = 0;
						while (chunkOffset < chunk.getLength()) {
							chunkOffset += _is.read(chunk.getValue(), (int) chunkOffset, (int) (chunk.getLength() - chunkOffset));
						}

						succeeded = false;
						for (int retryCount = 0; !succeeded; retryCount++) {
							try {
								r.writeFID(new UINT32(_fid), new UINT32(_initialOffset + offset), chunk);
								succeeded = true;
							}
							catch (Throwable t) {
								errorAction(t, retryCount);
							}
						}

						offset += chunk.getLength();
						progressReported(offset + _size / 4, maxProgress);
					}
					while (offset < _size);
				}

				progressReported(maxProgress, maxProgress);
				close0(wasOpen);
			}
			catch (Exception e) {
				throw new ProtocolException("Unable to write data to device.", e);
			}
		}
	}

	public void checkProtocolVersion() throws ProtocolException {
		if (myProtocolVersionMajor < 0 // assume OK until proven guilty
		|| myProtocolVersionMajor == PROTOCOL_VERSION_MAJOR) {
			// return;
		}
		else if (myProtocolVersionMajor > PROTOCOL_VERSION_MAJOR) {
			throw new ProtocolException("Empeg is using a protocol newer than the one I am using.");
		}
		else {
			Debug.println(Debug.WARNING, "Empeg is using a protocol that is older than the one I am using.");
		}
	}

	public void waitForDevice(int _retries) throws ProtocolException {
		int i;
		for (i = 0; i < _retries; i++) {
			if (isDeviceConnected()) {
				break;
			}
			try {
				Thread.sleep(1000);
			}
			catch (Exception e) {
			}
		}
		if (i == _retries) {
			throw new ProtocolException("There is no device currently connected.");
		}
	}

	protected void waitForUnitConnected() throws ProtocolException {
		waitForUnitConnected(90000);
	}

	protected synchronized void waitForUnitConnected(long _timeout) throws ProtocolException {
		try {
			myConn.pause();
		}
		catch (ConnectionException e) {
			throw new ProtocolException("Unable to pause connection to device.", e);
		}

		try {
			myConn.unpause();
		}
		catch (Throwable t) {
			// This is just too common...
			// Debug.println(t);
		}

		//    myConn.setTimeout(5000);
		boolean unitConnected = false;
		long startTime = System.currentTimeMillis();
		long curTime = 0;
		EmpegRequest req = new EmpegRequest(myConn, this);
		do {
			curTime = (System.currentTimeMillis() - startTime);
			progressReported(curTime, _timeout);

			if (!myConn.isOpen()) {
				try {
					myConn.unpause();
				}
				catch (Throwable t) {
					Debug.println(t);
				}
			}

			try {
				req.ping();
				getProtocolVersion0(req);
				unitConnected = true;
			}
			catch (Throwable t) {
				Debug.println(t);
			}
		}
		while (!unitConnected && !myConn.isOpen() && curTime < _timeout);

		progressReported(_timeout, _timeout);
	}

	public synchronized void enableWrite(boolean _writeAccess) throws ProtocolException {
		boolean wasOpen = open0();

		EmpegRequest r = new EmpegRequest(myConn, this);
		progressReported(0, 1);

		boolean succeeded = false;
		for (int retryCount = 0; !succeeded; retryCount++) {
			try {
				r.mount((_writeAccess) ? MountRequestPacket.MODE_READWRITE : MountRequestPacket.MODE_READONLY);
				succeeded = true;
			}
			catch (Throwable t) {
				errorAction(t, retryCount);
			}
		}

		progressReported(1, 1);
		close0(wasOpen);
	}

	public synchronized void rebuildPlayerDatabase(long _options) throws ProtocolException {
		boolean wasOpen = open0();

		EmpegRequest r = new EmpegRequest(myConn, this);

		boolean succeeded = false;
		for (int retryCount = 0; !succeeded; retryCount++) {
			try {
				r.buildAndSaveDatabase(new UINT32(_options));
				succeeded = true;
			}
			catch (Throwable t) {
				errorAction(t, retryCount);
			}
		}

		progressReported(1, 1);

		close0(wasOpen);
	}

	public synchronized void deleteFID(long _fid, long _idMask) throws ProtocolException {
		if (myUseHijack && _fid >= FIDConstants.FID_FIRSTNORMAL && isDeviceConnected()) {
			try {
				progressReported(0, 1);
				HijackUtils.deleteFid(HijackUtils.getAddress(myConn), _fid);
				progressReported(1, 1);
			}
			catch (Exception e) {
				throw new ProtocolException("Unable to delete fid from device.", e);
			}
		}
		else {
			boolean wasOpen = open0();

			EmpegRequest r = new EmpegRequest(myConn, this);
			progressReported(0, 1);

			boolean succeeded = false;
			for (int retryCount = 0; !succeeded; retryCount++) {
				try {
					r.deleteFID(new UINT32(_fid), new UINT32(_idMask));
					succeeded = true;
				}
				catch (Throwable t) {
					errorAction(t, retryCount);
				}
			}

			progressReported(1, 1);
			close0(wasOpen);
		}
	}

	/**
	 * Returns the number of the drive with the most available space.
	 */
	public synchronized int getMostEmptyDriveNumber() throws ProtocolException {
		boolean wasOpen = open0();

		EmpegRequest r = new EmpegRequest(myConn, this);
		StatFSResponsePacket diskInfo = null;

		boolean succeeded = false;
		for (int retryCount = 0; !succeeded; retryCount++) {
			try {
				diskInfo = r.freeSpace();
				succeeded = true;
			}
			catch (Throwable t) {
				errorAction(t, retryCount);
			}
		}

		close0(wasOpen);

		long blockSize0 = diskInfo.getDrive0BlockSize().getValue();
		long blockSize1 = diskInfo.getDrive1BlockSize().getValue();
		long freeSpace0 = diskInfo.getDrive0Space().getValue() * blockSize0;
		long freeSpace1 = diskInfo.getDrive1Space().getValue() * blockSize1;
		return (freeSpace0 > freeSpace1) ? 0 : 1;
	}

	public synchronized StorageInfo getStorageInfo(int _which) throws ProtocolException {
		boolean wasOpen = open0();

		EmpegRequest r = new EmpegRequest(myConn, this);
		progressReported(0, 1);
		StatFSResponsePacket diskInfo = null;

		boolean succeeded = false;
		for (int retryCount = 0; !succeeded; retryCount++) {
			try {
				diskInfo = r.freeSpace();
				succeeded = true;
			}
			catch (Throwable t) {
				errorAction(t, retryCount);
			}
		}

		progressReported(1, 1);
		close0(wasOpen);

		long blockSize0 = diskInfo.getDrive0BlockSize().getValue();
		long blockSize1 = diskInfo.getDrive1BlockSize().getValue();
		long totalSize = (diskInfo.getDrive0Size().getValue() * blockSize0 + diskInfo.getDrive1Size().getValue() * blockSize1);
		long totalFree = (diskInfo.getDrive0Space().getValue() * blockSize0 + diskInfo.getDrive1Space().getValue() * blockSize1);
		StorageInfo storageInfo = new StorageInfo(-1, totalSize, totalFree, -1);
		return storageInfo;
	}

	protected synchronized void fsck(String _drive, boolean _force) throws ProtocolException {
		boolean wasOpen = open0();

		EmpegRequest r = new EmpegRequest(myConn, this);

		long result = -1;

		boolean succeeded = false;
		for (int retryCount = 0; !succeeded; retryCount++) {
			try {
				myConn.flushReceiveBuffer();
				result = r.fsck(_drive, new UINT32((_force) ? 1 : 0)).getValue();
				succeeded = true;
			}
			catch (Throwable t) {
				errorAction(t, retryCount);
			}
		}

		boolean bad = false;

		if ((result & 1) != 0) {
			System.err.println("FSCK corrected some errors on drive " + _drive);
		}
		if ((result & 2) != 0) {
			System.err.println("FSCK thinks the system should be rebooted on drive " + _drive);
			bad = true;
		}
		if ((result & 4) != 0) {
			System.err.println("FSCK left stuff uncorrected on drive " + _drive);
			bad = true;
		}
		if ((result & 8) != 0) {
			System.err.println("FSCK reported operational error on drive " + _drive);
			bad = true;
		}
		if ((result & 16) != 0) {
			System.err.println("FSCK report usage or syntax error on drive " + _drive);
			bad = true;
		}
		if ((result & 128) != 0) {
			System.err.println("FSCK reported shared library error on drive " + _drive);
			bad = true;
		}
		if ((result & 512) != 0) {
			System.err.println("FSCK reported disk is not present on drive " + _drive);
		}

		if (bad) {
			throw new ProtocolException("FSCK found an serious problem (check logs for explanation).");
		}

		progressReported(1, 1);
		close0(wasOpen);
	}

	public synchronized void checkMedia(boolean _force) throws ProtocolException {
		boolean wasOpen = open0();

		fsck("/dev/hda4", _force);
		fsck("/dev/hdc4", _force);

		close0(wasOpen);
	}

	protected void sendCommand(long _command) throws ProtocolException {
		sendCommand(_command, 0, 0, "");
	}

	protected void sendCommand(long _command, long _parameter0) throws ProtocolException {
		sendCommand(_command, _parameter0, 0, "");
	}

	protected void sendCommand(long _command, long _parameter0, long _parameter1) throws ProtocolException {
		sendCommand(_command, _parameter0, _parameter1, "");
	}

	protected synchronized void sendCommand(long _command, long _parameter0, long _parameter1, String _parameter2) throws ProtocolException {
		boolean wasOpen = open0();

		EmpegRequest r = new EmpegRequest(myConn, this);

		boolean succeeded = false;
		for (int retryCount = 0; !succeeded; retryCount++) {
			try {
				myConn.flushReceiveBuffer();
				r.sendCommand(new UINT32(_command), new UINT32(_parameter0), new UINT32(_parameter1), _parameter2);
				succeeded = true;
			}
			catch (Throwable t) {
				errorAction(t, retryCount);
			}
		}

		close0(wasOpen);
	}

	public void restartPlayer(boolean _autoSlumber) throws ProtocolException {
		restartPlayer(_autoSlumber, true);
	}

	protected synchronized void restartPlayer(boolean _autoSlumber, boolean _waitForCompletion) throws ProtocolException {
		long param = EmpegRequest.RESTART_PLAYER;
		if (_autoSlumber) {
			param |= EmpegRequest.RESTART_IN_SLUMBER;
		}
		internalRestart(param, _waitForCompletion);

	}

	protected void restartUnit(boolean _autoSlumber) throws ProtocolException {
		restartUnit(_autoSlumber, true);
	}

	public synchronized void restartUnit(boolean _autoSlumber, boolean _waitForCompletion) throws ProtocolException {
		long param = EmpegRequest.RESTART_UNIT;
		if (_autoSlumber) {
			param |= EmpegRequest.RESTART_IN_SLUMBER;
		}
		internalRestart(param, _waitForCompletion);
	}

	protected void internalRestart(long _param, boolean _waitForCompletion) throws ProtocolException {
		sendCommand(EmpegRequest.COM_RESTART, _param);

		if (_waitForCompletion) {
			try {
				Thread.sleep(2000);
			}
			catch (Exception e) {
			}

			waitForUnitConnected();
		}
	}

	public void lockUI(boolean _lock) throws ProtocolException {
		sendCommand(EmpegRequest.COM_LOCKUI, _lock ? 1 : 0);
	}

	protected void setSlumber(boolean _slumber) throws ProtocolException {
		sendCommand(EmpegRequest.COM_SLUMBER, _slumber ? 1 : 0);
	}

	public void setPlayState(long _playState) throws ProtocolException {
		sendCommand(EmpegRequest.COM_PLAYSTATE, _playState);
	}

	public synchronized byte[] grabScreen() throws ProtocolException {
		boolean wasOpen = open0();
		EmpegRequest r = new EmpegRequest(myConn, this);
		byte[] screen = r.grabScreen(new UINT32(0)).getValue();
		close0(wasOpen);
		return screen;
	}

	protected boolean open0() throws ProtocolException {
		boolean isOpen = myConn.isOpen();
		if (!isOpen) {
			open();
		}
		return isOpen;
	}

	protected void close0(boolean _wasOpen) throws ProtocolException {
		if (!_wasOpen) {
			close();
		}
	}

	public String getPlayerType() throws ProtocolException {
		String type;
		try {
			byte[] buffer = readFIDToMemory(FIDConstants.FID_PLAYERTYPE);
			try {
				type = new String(buffer, 0, buffer.length - 1, "ISO-8859-1");
			}
			catch (UnsupportedEncodingException e) {
				Debug.println(e);
				type = new String(buffer, 0, buffer.length - 1);
			}
		}
		catch (FileNotFoundException e) {
			// ... That's ok ...
			type = "";
		}
		return type;
	}

	protected PlayerVersionInfo getVersionInfo() throws ProtocolException {
		try {
			//Debug.println("ProtocolClient.getVersionInfo()");

			byte[] buffer = readFIDToMemory(FIDConstants.FID_VERSION);
			String versionStr;
			try {
				versionStr = new String(buffer, "ISO-8859-1");
			}
			catch (UnsupportedEncodingException e) {
				Debug.println(e);
				versionStr = new String(buffer);
			}
			PlayerVersionInfo versionInfo = new PlayerVersionInfo(versionStr, "", 1);

			//Debug.println("ProtocolClient.getVersionInfo() = done");

			return versionInfo;
		}
		catch (FileNotFoundException e) {
			throw new ProtocolException("Unable to retrieve player version info.", e);
		}
	}

	protected synchronized void getProtocolVersion0(EmpegRequest _request) {
		myProtocolVersionMajor = _request.getProtocolVersionMajor().getValue();
		myProtocolVersionMinor = _request.getProtocolVersionMinor().getValue();
	}

	public synchronized ProtocolVersion getProtocolVersion() throws ProtocolException {
		if (myProtocolVersionMajor == -1 || myProtocolVersionMinor == -1) {
			boolean wasOpen = open0();
			EmpegRequest r = new EmpegRequest(myConn, this);
			r.ping();
			getProtocolVersion0(r);
			close0(wasOpen);
		}

		ProtocolVersion protocolVersion = new ProtocolVersion(myProtocolVersionMajor, myProtocolVersionMinor);
		return protocolVersion;
	}

	public PlayerIdentity getPlayerIdentity() throws ProtocolException, FileNotFoundException {
		PlayerIdentity playerID = new PlayerIdentity();

		byte[] buffer = readFIDToMemory(FIDConstants.FID_ID);
		String idStr;
		try {
			idStr = new String(buffer, "ISO-8859-1");
		}
		catch (UnsupportedEncodingException e) {
			Debug.println(e);
			idStr = new String(buffer);
		}
		StringTokenizer tokenizer = new StringTokenizer(idStr, "\n");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			int colonIndex = token.indexOf(':');
			String key = token.substring(0, colonIndex).trim();
			String value = token.substring(colonIndex + 1).trim();

			if (key.equals("hwrev")) {
				playerID.setHWRev(value);
			}
			else if (key.equals("serial")) {
				playerID.setSerial(value);
			}
			else if (key.equals("build")) {
				playerID.setBuild(value);
			}
			else if (key.equals("id")) {
				playerID.setID(value);
			}
			else if (key.equals("ram")) {
				playerID.setRAM(value);
			}
			else if (key.equals("flash")) {
				playerID.setFlash(value);
			}
			else {
				Debug.println(Debug.WARNING, "getPlayerIdentity: Don't know what to do with key = " + key + "; value = " + value);
			}
		}

		return playerID;
	}

	public synchronized void checkDatabaseAvailability() throws ProtocolException, FileNotFoundException {
		//Debug.println("ProtocolClient.checkDatabaseAvailability()");
		boolean wasOpen = open0();

		long size;

		size = getLength(FIDConstants.FID_TAGINDEX);
		if (size < 16) {
			throw new ProtocolException("Tag database is suspiciously small. Failing.");
		}
		//System.out.println("Tag database is " + size + " bytes.");

		size = getLength(FIDConstants.FID_STATICDATABASE);
		if (size < 16) {
			throw new ProtocolException("Static database is suspiciously small. Failing.");
		}
		//System.out.println("Static database is " + size + " bytes.");

		size = getLength(FIDConstants.FID_PLAYLISTDATABASE);
		//System.out.println("Playlist database is " + size + " bytes.");

		//Debug.println("ProtocolClient.checkDatabaseAvailability() = " + size);
		close0(wasOpen);
	}

	public synchronized void deleteDatabases() throws ProtocolException {
		boolean wasOpen = open0();
		deleteFID(FIDConstants.FID_STATICDATABASE, 0xFFFF);
		deleteFID(FIDConstants.FID_TAGINDEX, 0xFFFF);
		deleteFID(FIDConstants.FID_PLAYLISTDATABASE, 0xFFFF);
		close0(wasOpen);
	}

	protected void errorAction(Throwable _t, int _retries) throws ProtocolException {
		Debug.println(Debug.WARNING, "Retry #" + _retries + " failed. (" + _t.getMessage() + ")");
		Debug.println(Debug.INFORMATIVE, _t);
		//_t.printStackTrace();

		//org.jempeg.util.Debug.println("Flushing because of failure...");
		try {
			myConn.flushReceiveBuffer();
		}
		catch (ConnectionException e) {
			throw new ProtocolException("Unable to flush receive buffer.", e);
		}

		if (_retries == RESTART_CONNECTION_RETRIES) {
			Debug.println(Debug.WARNING, "Desperately trying to restart the connection before we give up ...");
			try {
				myConn.pause();
				myConn.unpause();
			}
			catch (Throwable t) {
				Debug.println(t);
			}
		}
		if (_retries >= myMaximumRetryCount) {
			throw new ProtocolException("Operation failure exceeded maximum retry count.");
		}

		while (_retries > 3) {
			try {
				//org.jempeg.util.Debug.println("Sleeping due to excessive retries...");
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
			}
			_retries--;
		}
	}
}
