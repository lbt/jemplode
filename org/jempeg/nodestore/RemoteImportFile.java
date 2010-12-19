/* RemoteImportFile - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.ChainedIOException;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.text.StringUtils;

import org.jempeg.nodestore.model.TagValueRetriever;
import org.jempeg.protocol.FIDInputStream;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.SocketConnection;

public class RemoteImportFile implements IImportFile
{
    private String myID;
    private IFIDNode myRemoteNode;
    private IProtocolClient myProtocolClient;
    
    public static RemoteImportFile createInstance
	(IFIDNode _remoteNode, IProtocolClient _protocolClient) {
	RemoteImportFile remoteImportFile;
	if (_remoteNode instanceof FIDPlaylist)
	    remoteImportFile
		= new RemoteImportFolder((FIDPlaylist) _remoteNode,
					 _protocolClient);
	else
	    remoteImportFile
		= new RemoteImportFile(_remoteNode, _protocolClient);
	return remoteImportFile;
    }
    
    protected RemoteImportFile(IFIDNode _remoteNode,
			       IProtocolClient _protocolClient) {
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
	if (myID == null)
	    myID = String.valueOf(myRemoteNode.getFID());
	return myID;
    }
    
    public String getName() {
	IFIDNode fidNode = getNode();
	StringBuffer sb = new StringBuffer();
	sb.append(fidNode.getTitle());
	sb.append(TagValueRetriever.getValue(fidNode, "ext"));
	return sb.toString();
    }
    
    public String getLocation() {
	return null;
    }
    
    public long getLength() {
	IFIDNode fidNode = getNode();
	int length = fidNode.getLength();
	return (long) length;
    }
    
    public InputStream getInputStream() throws IOException {
	return getInputStream(false);
    }
    
    public InputStream getInputStream(boolean _useHijack) throws IOException {
	try {
	    IFIDNode remoteNode = getNode();
	    InputStream is;
	    do {
		if (remoteNode instanceof FIDLocalFile)
		    is = ((FIDLocalFile) remoteNode).getFile()
			     .getInputStream();
		else {
		    if (_useHijack) {
			InetAddress address
			    = ((SocketConnection)
			       myProtocolClient.getConnection())
				  .getAddress();
			String hostAddress = address.getHostAddress();
			long fid = remoteNode.getFID();
			String empegAddress
			    = ("http://" + hostAddress + "/empeg/fids"
			       + remoteNode.getTags().getValue("drive") + "/");
			try {
			    String fullHexString
				= StringUtils.pad(fid, 8, 16).toLowerCase();
			    URL u
				= new URL(empegAddress + "_"
					  + fullHexString.substring(0, 5) + "/"
					  + fullHexString.substring(5));
			    is = u.openStream();
			    break;
			} catch (FileNotFoundException e) {
			    URL u = new URL(empegAddress
					    + Long.toHexString(fid));
			    is = u.openStream();
			    break;
			}
		    }
		    FIDInputStream fidInputStream
			= new FIDInputStream(myProtocolClient,
					     myRemoteNode.getFID());
		    is = new BufferedInputStream(fidInputStream,
						 myProtocolClient.getConnection
						     ().getPacketSize());
		}
	    } while (false);
	    return is;
	} catch (Exception e) {
	    throw new ChainedIOException
		      ("Unable to get an input stream onto the requested FID.",
		       e);
	}
    }
    
    public SeekableInputStream getSeekableInputStream() throws IOException {
	try {
	    FIDInputStream fidInputStream
		= new FIDInputStream(myProtocolClient, myRemoteNode.getFID());
	    return fidInputStream;
	} catch (Exception e) {
	    throw new ChainedIOException
		      ("Unable to get an input stream onto the requested FID.",
		       e);
	}
    }
    
    public IFIDNode getNode() {
	return myRemoteNode;
    }
    
    public boolean isContainer() {
	IFIDNode fidNode = getNode();
	boolean container = fidNode instanceof FIDPlaylist;
	return container;
    }
    
    public int getSize() {
	int size;
	if (isContainer()) {
	    FIDPlaylist playlist = (FIDPlaylist) getNode();
	    size = playlist.size();
	} else
	    size = 0;
	return size;
    }
    
    public Object getValueAt(int _index) {
	Object value;
	if (isContainer()) {
	    FIDPlaylist playlist = (FIDPlaylist) getNode();
	    value = playlist.getNodeAt(_index);
	} else
	    value = null;
	return value;
    }
    
    public String toString() {
	String toString = getName();
	return toString;
    }
}
