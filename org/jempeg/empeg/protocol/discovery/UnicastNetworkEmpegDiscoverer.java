/* UnicastNetworkEmpegDiscoverer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.discovery;
import java.net.InetAddress;

import com.inzyme.progress.SilentProgressListener;
import com.inzyme.util.Debug;

import org.jempeg.empeg.protocol.EmpegSynchronizeClient;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.SocketConnectionFactory;
import org.jempeg.protocol.discovery.AbstractDiscoverer;
import org.jempeg.protocol.discovery.BasicDevice;

public class UnicastNetworkEmpegDiscoverer extends AbstractDiscoverer
{
    private InetAddress myInetAddress;
    private int myTimeoutMillis;
    /*synthetic*/ static Class class$0;
    
    public UnicastNetworkEmpegDiscoverer(InetAddress _inetAddress,
					 int _timeoutMillis) {
	myInetAddress = _inetAddress;
	myTimeoutMillis = _timeoutMillis;
    }
    
    protected void startDiscovery0() {
	try {
	    Class var_class = class$0;
	    if (var_class == null) {
		Class var_class_0_;
		try {
		    var_class_0_
			= Class
			      .forName("org.jempeg.protocol.SocketConnection");
		} catch (ClassNotFoundException classnotfoundexception) {
		    NoClassDefFoundError noclassdeffounderror
			= new NoClassDefFoundError;
		    ((UNCONSTRUCTED)noclassdeffounderror).NoClassDefFoundError
			(classnotfoundexception.getMessage());
		    throw noclassdeffounderror;
		}
		var_class = class$0 = var_class_0_;
	    }
	    Class var_class_1_;
	    MONITORENTER (var_class_1_ = var_class);
	    MISSING MONITORENTER
	    synchronized (var_class_1_) {
		SocketConnectionFactory socketConnFactory
		    = new SocketConnectionFactory(myInetAddress, 8300);
		IProtocolClient client = null;
		try {
		    EmpegSynchronizeClient syncClient
			= new EmpegSynchronizeClient(socketConnFactory);
		    client
			= syncClient
			      .getProtocolClient(new SilentProgressListener());
		    client.getConnection().setTimeout((long) myTimeoutMillis);
		    if (client.isDeviceConnected()) {
			String type = client.getPlayerType();
			IDeviceSettings dcf = client.getDeviceSettings();
			String name = dcf.getName();
			if (name == null || name.length() == 0)
			    name = EmpegDiscoveryUtils.getDefaultName(type);
			fireDeviceDiscovered
			    (new BasicDevice(name, socketConnFactory));
		    }
		} catch (Object object) {
		    if (client != null)
			client.close();
		    throw object;
		}
		if (client != null)
		    client.close();
	    }
	} catch (Exception e) {
	    Debug.println(e);
	}
    }
    
    protected void stopDiscovery0() {
	/* empty */
    }
}
