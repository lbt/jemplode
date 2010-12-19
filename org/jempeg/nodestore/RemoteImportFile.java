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
package org.jempeg.nodestore;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import org.jempeg.nodestore.model.TagValueRetriever;
import org.jempeg.protocol.FIDInputStream;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.SocketConnection;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.ChainedIOException;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.text.StringUtils;

/**
 * RemoteImportFile is an implementation of the ImportFile
 * interface on an FID from a remote Empeg.  This will
 * actually provide a stream directly to an FID on the
 * Empeg.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.4 $
 */
public class RemoteImportFile implements IImportFile {
	private String myID;
	private IFIDNode myRemoteNode;
	private IProtocolClient myProtocolClient;

  public static RemoteImportFile createInstance(IFIDNode _remoteNode, IProtocolClient _protocolClient) {
    RemoteImportFile remoteImportFile; 
    if (_remoteNode instanceof FIDPlaylist) {
      remoteImportFile = new RemoteImportFolder((FIDPlaylist)_remoteNode, _protocolClient); 
    }
    else {
      remoteImportFile = new RemoteImportFile(_remoteNode, _protocolClient); 
    }
    return remoteImportFile;
  }
  
	protected RemoteImportFile(IFIDNode _remoteNode, IProtocolClient _protocolClient) {
		myRemoteNode = _remoteNode;
		myProtocolClient = _protocolClient;
	}
  
  public IProtocolClient getProtocolClient() {
    return myProtocolClient;
  }
  
  public Properties getTags() throws IOException {
    return myRemoteNode.getTags().toProperties();
  }

	public synchronized Object getID() {
		if (myID == null) {
			myID = String.valueOf(myRemoteNode.getFID());
		}
		return myID;
	}

	public String getName() {
		IFIDNode fidNode = getNode();
		StringBuffer sb = new StringBuffer();
		sb.append(fidNode.getTitle());
		sb.append(TagValueRetriever.getValue(fidNode, DatabaseTags.EXTENSION_TAG));
		return sb.toString();
	}

	public String getLocation() {
		return null; // ??
	}

	public long getLength() {
		IFIDNode fidNode = getNode();
		int length = fidNode.getLength();
		return length;
	}

	/**
	* Returns an InputStream onto this RemoteFile.  This defaults to
	* not using HiJack to return the stream.
	*
	* @returns an InputStream onto this RemoteFile
	* @throws IOException if a suitable stream cannot be returned
	*/
	public InputStream getInputStream() throws IOException {
		return getInputStream(false);
	}

	/**
	* Returns an InputStream onto this RemoteFile.
	*
	* @param _useHijack whether or not to use HTTP w/ HiJack to download the file
	* @returns an InputStream onto this RemoteFile
	* @throws IOException if a suitable stream cannot be returned
	*/
	public InputStream getInputStream(boolean _useHijack) throws IOException {
		try {
			InputStream is;
			IFIDNode remoteNode = getNode();
			if (remoteNode instanceof FIDLocalFile) {
				is = ((FIDLocalFile) remoteNode).getFile().getInputStream();
			}
			else {
				if (_useHijack) {
					InetAddress address = ((SocketConnection)myProtocolClient.getConnection()).getAddress();
					String hostAddress = address.getHostAddress();

					long fid = remoteNode.getFID();
					String empegAddress = "http://" + hostAddress + "/empeg/fids" + remoteNode.getTags().getValue(DatabaseTags.DRIVE_TAG) + "/";
					
					try {
						String fullHexString = StringUtils.pad(fid, 8, 16).toLowerCase();
						URL u = new URL(empegAddress + "_" + fullHexString.substring(0, 5) + "/" + fullHexString.substring(5));
						is = u.openStream();
					}
					catch (FileNotFoundException e) {
						URL u = new URL(empegAddress + Long.toHexString(fid));
						is = u.openStream();
					}
				}
				else {
					FIDInputStream fidInputStream = new FIDInputStream(myProtocolClient, myRemoteNode.getFID());
					is = new BufferedInputStream(fidInputStream, myProtocolClient.getConnection().getPacketSize());
				}
			}
			return is;
		}
		catch (Exception e) {
			throw new ChainedIOException("Unable to get an input stream onto the requested FID.", e);
		}
	}

	public SeekableInputStream getSeekableInputStream() throws IOException {
		try {
			FIDInputStream fidInputStream = new FIDInputStream(myProtocolClient, myRemoteNode.getFID());
			return fidInputStream;
		}
		catch (Exception e) {
			throw new ChainedIOException("Unable to get an input stream onto the requested FID.", e);
		}
	}

	public IFIDNode getNode() {
		return myRemoteNode;
	}

	public boolean isContainer() {
		IFIDNode fidNode = getNode();
		boolean container = (fidNode instanceof FIDPlaylist);
		return container;
	}

	public int getSize() {
		int size;
		if (isContainer()) {
			FIDPlaylist playlist = (FIDPlaylist) getNode();
			size = playlist.size();
		}
		else {
			size = 0;
		}
		return size;
	}

	public Object getValueAt(int _index) {
		Object value;
		if (isContainer()) {
			FIDPlaylist playlist = (FIDPlaylist) getNode();
			value = playlist.getNodeAt(_index);
		}
		else {
			value = null;
		}
		return value;
	}

	public String toString() {
		String toString = getName();
		return toString;
	}
}
