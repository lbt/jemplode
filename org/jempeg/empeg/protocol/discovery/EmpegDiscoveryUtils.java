/* EmpegDiscoveryUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.discovery;
import com.inzyme.io.ChainedIOException;
import com.inzyme.progress.SilentProgressListener;

import org.jempeg.empeg.protocol.EmpegSynchronizeClient;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.protocol.IConnectionFactory;
import org.jempeg.protocol.IProtocolClient;

public class EmpegDiscoveryUtils
{
    public static String getPlayerType(IConnectionFactory _connFactory)
	throws ChainedIOException {
	try {
	    EmpegSynchronizeClient client
		= new EmpegSynchronizeClient(_connFactory);
	    IProtocolClient protocolClient
		= client.getProtocolClient(new SilentProgressListener());
	    String string;
	    try {
		String type = "unknown";
		type = protocolClient.getPlayerType();
		string = type;
	    } catch (Object object) {
		protocolClient.close();
		throw object;
	    }
	    protocolClient.close();
	    return string;
	} catch (Exception e) {
	    throw new ChainedIOException
		      ("Unable to get the player type for this device.", e);
	}
    }
    
    public static boolean isEmpegConnected(IConnectionFactory _connFactory)
	throws ChainedIOException {
	try {
	    EmpegSynchronizeClient client
		= new EmpegSynchronizeClient(_connFactory);
	    IProtocolClient protocolClient
		= client.getProtocolClient(new SilentProgressListener());
	    boolean bool;
	    try {
		boolean connected = protocolClient.isDeviceConnected();
		bool = connected;
	    } catch (Object object) {
		protocolClient.close();
		throw object;
	    }
	    protocolClient.close();
	    return bool;
	} catch (Exception e) {
	    throw new ChainedIOException
		      ("Unable to determine if a device is connected.", e);
	}
    }
    
    public static String getDefaultName(String _playerType) {
	String name;
	if ("empeg-car-1".equals(_playerType))
	    name = "empeg car";
	else if ("empeg-car-2".equals(_playerType))
	    name = "empeg car";
	else if ("rio-car".equals(_playerType))
	    name = "Rio Car";
	else if ("jupiter".equals(_playerType))
	    name = "Jupiter";
	else
	    name = "Unknown Player";
	return name;
    }
    
    public static String getEmpegName
	(IConnectionFactory _connFactory, String _defaultName)
	throws ChainedIOException {
	try {
	    EmpegSynchronizeClient client
		= new EmpegSynchronizeClient(_connFactory);
	    IProtocolClient protocolClient
		= client.getProtocolClient(new SilentProgressListener());
	    String name;
	    try {
		IDeviceSettings dcf = protocolClient.getDeviceSettings();
		name = dcf.getName();
		if (name == null || name.length() == 0)
		    name = _defaultName;
	    } catch (Object object) {
		protocolClient.close();
		throw object;
	    }
	    protocolClient.close();
	    return name;
	} catch (Exception e) {
	    throw new ChainedIOException("Unable to get the device name.", e);
	}
    }
}
