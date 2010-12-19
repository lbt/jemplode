/**
 * Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
 * other contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jempeg.empeg.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jempeg.JEmplodeProperties;
import org.jempeg.empeg.versiontracker.HijackVersionTracker;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.SocketConnection;
import org.jempeg.protocol.SocketConnectionFactory;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.io.StreamUtils;
import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

/**
 * Convenience methods for using Hijack.
 *
 * @author Mike Schrag
 * @version $Revision: 1.14 $
 */
public class HijackUtils {
	/**
	 * Returns whether or not Hijack should be used to communicate
	 * with the given connection.  This will check the user's
	 * preferences also.  If Hijack can be used this will return null, 
	 * otherwise it will return a Reason why you can't use it.
	 *
	 * @param _conn the connection to check
	 * @returns the reason Hijack cannot be used
	 */
	public static Reason shouldUseHijack(IConnection _conn) {
		Reason reason = null;
		if (!(_conn instanceof SocketConnection)) {
			reason = new Reason("jEmplode must be connected to your Empeg with Ethernet to use Hijack.");
		}
		else if (!PropertiesManager.getInstance().getBooleanProperty(JEmplodeProperties.USE_HIJACK_KEY)) {
			reason = new Reason("You must have 'Use Hijack with Possible' enabled under Tools=>Options... to use Hijack.");
		}
		else {
			SocketConnection conn = (SocketConnection) _conn;
			HijackVersionTracker versionTracker = new HijackVersionTracker(new SocketConnectionFactory(conn.getAddress(), conn.getPort()));
			try {
				if (!versionTracker.isInstalled()) {
					reason = versionTracker.getFailureReason("use Hijack");
				}
			}
			catch (Throwable t) {
				Debug.println(Debug.WARNING, "You wanted to use Hijack, but I can't find it on this connection.");
				reason = versionTracker.getFailureReason("use Hijack");
			}
		}
		return reason;
	}

	/**
	 * Returns the InetAddress from this Connection.
	 *
	 * @param _conn the connection to retrieve an address for
	 * @returns an InetAddress from this connection
	 */
	public static InetAddress getAddress(IConnection _conn) {
		SocketConnection socketConn = (SocketConnection) _conn;
		InetAddress inetAddress = socketConn.getAddress();
		return inetAddress;
	}

	/**
	 * Upload a stream of data to Hijack
	 *
	 * @param _address the address to upload to
	 * @param _remoteFilename the name of the file on the Empeg
	 * @param _uploadStream the stream to upload
	 * @param _offset the offset to begin writing
	 * @param _listener the listener to send status to
	 * @throws IOException if the upload fails
	 */
	public static void upload(InetAddress _address, String _remoteFilename, SeekableInputStream _uploadStream, int _offset, IProgressListener _listener) throws IOException {
		upload(_address, _remoteFilename, _uploadStream, _offset, (int) _uploadStream.length(), _listener);
	}

	/**
	 * Upload a stream of data to Hijack
	 *
	 * @param _address the address to upload to
	 * @param _remoteFilename the name of the file on the Empeg
	 * @param _uploadStream the stream to upload
	 * @param _offset the offset to begin writing
	 * @param _length the length of this upload
	 * @param _listener the listener to send status to
	 * @throws IOException if the upload fails
	 */
	public static void upload(InetAddress _address, String _remoteFilename, InputStream _uploadStream, int _offset, int _length, IProgressListener _listener) throws IOException {
		FTPClient ftp = new FTPClient(_address);
		try {
			login(ftp);
			OutputStream os = ftp.putBinary(_remoteFilename, _offset);
			StreamUtils.copy(_uploadStream, os, SocketConnection.TCP_MAXPAYLOAD, _length, _listener);
			os.flush();
			os.close();
		}
		finally {
			ftp.quit();
		}
	}

	/**
	 * Upload a stream of data to Hijack
	 *
	 * @param _address the address to upload to
	 * @param _remoteFilename the name of the file on the Empeg
	 * @param _uploadStream the stream to upload
	 * @param _offset the offset to begin writing
	 * @param _length the length of this upload
	 * @param _listener the listener to send status to
	 * @throws IOException if the upload fails
	 */
	public static void upload(FTPClient _ftpClient, String _remoteFilename, InputStream _uploadStream, int _offset, int _length, IProgressListener _listener) throws IOException {
		OutputStream os = _ftpClient.putBinary(_remoteFilename, _offset);
		StreamUtils.copy(_uploadStream, os, SocketConnection.TCP_MAXPAYLOAD, _length, _listener);
		os.flush();
		os.close();
	}

	/**
	 * Download a stream of data from Hijack.
	 *
	 * @param _address the address to download from
	 * @param _remoteFilename the name of the file on the Empeg
	 * @param _outputStream the stream to download into
	 * @param _listener the listener to send status to
	 * @throws IOException if the download fails
	 */
	public static void download(InetAddress _address, String _remoteFilename, OutputStream _outputStream, IProgressListener _listener) throws IOException {
		FTPClient ftp = new FTPClient(_address);
		try {
			login(ftp);
			InputStream is = ftp.getBinary(_remoteFilename);
			try {
				int size;
				if (_remoteFilename.startsWith("/proc")) {
					size = Integer.MAX_VALUE;
				}
				else {
					size = getSize(ftp, _remoteFilename);
				}
				StreamUtils.copy(is, _outputStream, SocketConnection.TCP_MAXPAYLOAD, size, _listener);
			}
			finally {
				is.close();
			}
      ftp.readGetResponse();
		}
		finally {
			ftp.quit();
		}
	}

	public static void download(FTPClient _ftpClient, String _remoteFilename, OutputStream _outputStream, int _size, IProgressListener _listener) throws IOException {
		InputStream is = _ftpClient.getBinary(_remoteFilename);
		try {
			StreamUtils.copy(is, _outputStream, SocketConnection.TCP_MAXPAYLOAD, _size, _listener);
		}
		finally {
			is.close();
		}
    _ftpClient.readGetResponse();
	}

	/**
	 * Delete a fid using Hijack.
	 *
	 * @param _address the address to download from
	 * @param _fid the fid to delete
	 * @throws IOException on ftp errors
	 */
	public static void deleteFid(InetAddress _address, long _fid) throws IOException {

		FTPClient ftp = new FTPClient(_address);
		try {
			login(ftp);
			RemoteFid fid = findFID(ftp, _fid);
			if (fid != null) {
				ftp.quote("DELE " + fid.getFullPath(), null);
			}
		}
		finally {
			ftp.quit();
		}
	}

	/**
	 * Upload a fid using Hijack
	 *
	 * @param _address the address to upload to
	 * @param _fid the fid to upload
	 * @param _is the stream to upload
	 * @param _initialOffset the offset to begin writing
	 * @param _size the length of this upload
	 * @param _listener the listener to send status to
	 * @param _newFidLayout if we should use the new fid layout
	 * @param _drive drive number to upload to
	 * @throws IOException if the upload fails
	 */
	public static void uploadFid(InetAddress _address, long _fid, InputStream _is, long _initialOffset, long _size, ISimpleProgressListener _listener, boolean _newFidLayout, int _drive) throws IOException {
		FTPClient ftp = new FTPClient(_address);
		try {
			login(ftp);

			RemoteFid remoteFid = HijackUtils.findFID(ftp, _fid);
			if (remoteFid == null) {
				remoteFid = new RemoteFid(_drive, _fid, _newFidLayout);
			}
			
			OutputStream os = null;
			try {
				os = ftp.putBinary(remoteFid.getFullPath(), (int) _initialOffset);
			}
			catch (FTPException e) {
				// If it failed we probably need to create the directory first
				ftp.mkdir(remoteFid.getPath());
				os = ftp.putBinary(remoteFid.getFullPath(), (int) _initialOffset);
			}
			StreamUtils.copy(_is, os, SocketConnection.TCP_MAXPAYLOAD, _size, 0, _size, _listener);
			os.flush();
			os.close();
		}
		finally {
			ftp.quit();
		}
	}

	private static boolean isExistingFile(FTPClient _ftp, RemoteFid _remoteFid) {
		try {
			String[] validCode = { "553" };
			_ftp.quote("NLST " + _remoteFid.getFullPath(), validCode);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Try to find the path of an existing FID on the empeg
	 *
	 * @param _ftp the FTPClient to use
	 * @param _fid the FID to lookup
	 */
	private static RemoteFid findFID(FTPClient _ftp, long _fid) {
		RemoteFid foundFid = null;
		for (int driveNum = 0; foundFid == null && driveNum <= 1; driveNum ++) {
			RemoteFid fid = new RemoteFid(driveNum, _fid, false);
			if (isExistingFile(_ftp, fid)) {
				foundFid = fid;
			}
			else {
				fid = new RemoteFid(driveNum, _fid, true);
				if (isExistingFile(_ftp, fid)) {
					foundFid = fid;
				}
			}
		}
		return foundFid;
	}

	/**
	 * Download a file from Hijack as a String (like /proc/version)
	 *
	 * @param _address the address to connect to
	 * @param _remoteFilename the filename to download
	 * @throws IOException if the download fails
	 */
	public static String getString(InetAddress _address, String _remoteFilename) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		download(_address, _remoteFilename, baos, null);
		String str = baos.toString();
		return str;
	}

	public static String getString(FTPClient _ftpClient, String _remoteFilename) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		download(_ftpClient, _remoteFilename, baos, Integer.MAX_VALUE, null);
		String str = baos.toString();
		return str;
	}

	/**
	 * Find out if the player is using the new FID layout scheme
	 *
	 * @param _address the address to connect to
	 * @throws IOException if ftp fails
	 */
	public static boolean isNewFidLayout(InetAddress _address) throws IOException {
		FTPClient ftp = new FTPClient(_address);
		try {
			login(ftp);
			ftp.chdir(RemoteFid.getFidPath(0) + "_00000");
			return true;
		}
		catch (FTPException e) {
			return false;
		}
		finally {
			ftp.quit();
		}
	}

	public static int getMaxFid(FTPClient _ftp) throws IOException {
		int maxFid = 0;
		for (int driveNum = 0; driveNum <= 1; driveNum++) {
			try {
				maxFid = HijackUtils.getMaxFid(_ftp, "/empeg/fids" + driveNum, maxFid);
			}
			catch (FTPException e) {
				// probably the drive doesn't exist
			}
		}
		return maxFid;
	}
	
	private static int getMaxFid(FTPClient _ftp, String _path, int _currentMax) throws IOException {
		int maxFid = _currentMax;
		Enumeration filesEnum = HijackUtils.getFiles(_ftp, _path).elements();
		while (filesEnum.hasMoreElements()) {
			FtpFile ftpFile = (FtpFile) filesEnum.nextElement();
			if (ftpFile.getName().indexOf('_') == -1) {
				RemoteFid remoteFid = new RemoteFid(ftpFile.getFullPath());
				maxFid = (int) Math.max(maxFid, remoteFid.getFid());
			}
			else {
				maxFid = HijackUtils.getMaxFid(_ftp, ftpFile.getFullPath(), maxFid);
			}
		}
		return maxFid;
	}

	/**
	 * Returns the size of the given filename
	 *
	 * @param _ftp the client to use
	 * @param _filename the filename to lookup
	 * @returns the size of the filename
	 * @throws IOException if the lookup fails
	 */
	protected static int getSize(FTPClient _ftp, String _filename) throws IOException {
		int size = -1;
		int slashIndex = _filename.lastIndexOf('/');
		String path = _filename.substring(0, slashIndex);
		String name = _filename.substring(slashIndex + 1);
		Enumeration filesEnum = getFiles(_ftp, path).elements();
		while (size == -1 && filesEnum.hasMoreElements()) {
			FtpFile file = (FtpFile) filesEnum.nextElement();
			if (name.equals(file.getName())) {
				size = file.getSize();
			}
		}
		return size;
	}

	public static Vector getFiles(InetAddress _address, String _folder) throws IOException {
		FTPClient ftp = new FTPClient(_address);
		try {
			login(ftp);
			return getFiles(ftp, _folder);
		}
		finally {
			ftp.quit();
		}
	}

	public static Vector getFiles(FTPClient _ftp, String _folder) throws IOException {
		Vector filesVec = new Vector();
		String[] lines = _ftp.dir(_folder, true);
		Vector tokensVec = new Vector();
		for (int i = 0; i < lines.length; i ++) {
			tokensVec.removeAllElements();
			StringTokenizer st = new StringTokenizer(lines[i]);
			while (st.hasMoreTokens()) {
				tokensVec.addElement(st.nextToken());
			}
			if (tokensVec.size() >= 5) {
				String filename = (String) tokensVec.elementAt(tokensVec.size() - 1);
				String sizeStr = (String) tokensVec.elementAt(tokensVec.size() - 5);
				FtpFile ftpFile = new FtpFile(_folder, filename, Integer.parseInt(sizeStr));
				filesVec.addElement(ftpFile);
			}
		}
		return filesVec;
	}

	/**
	 * Logs into Hijack via FTP
	 *
	 * @param _ftp the FTPClient to connect with
	 * @param _address the address to connect to
	 * @throws IOException if the login fails
	 */
	public static void login(FTPClient _ftp) throws IOException {
		_ftp.login(PropertiesManager.getInstance().getProperty(JEmplodeProperties.HIJACK_LOGIN_KEY), PropertiesManager.getInstance().getProperty(JEmplodeProperties.HIJACK_PASSWORD_KEY));
		_ftp.setType(FTPTransferType.BINARY);
		_ftp.setConnectMode(FTPConnectMode.ACTIVE);
	}

	/**
	 * Send a reboot request via Hijack.
	 *
	 * @param _address the address to reboot
	 */
	public static void reboot(InetAddress _address) {
		try {
			URL u = new URL("http", _address.getHostAddress(), 80, "/?reboot");
			InputStream is = u.openStream();
			is.close();
		}
		catch (IOException e) {
			// this is expected
		}
	}
}
