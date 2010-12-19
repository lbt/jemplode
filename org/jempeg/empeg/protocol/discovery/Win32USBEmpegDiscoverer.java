/* Win32USBEmpegDiscoverer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.discovery;
import java.io.IOException;
import java.util.Vector;

import com.inzyme.util.Debug;

import org.jempeg.empeg.protocol.Win32USBConnection;
import org.jempeg.empeg.protocol.Win32USBConnectionFactory;
import org.jempeg.protocol.discovery.AbstractDiscoverer;
import org.jempeg.protocol.discovery.BasicDevice;

public class Win32USBEmpegDiscoverer extends AbstractDiscoverer
{
    private Vector vec = new Vector();
    /*synthetic*/ static Class class$0;
    
    protected void startDiscovery0() {
	try {
	    if (Win32USBConnection.DLL_EXISTS) {
		Class var_class = class$0;
		if (var_class == null) {
		    Class var_class_0_;
		    try {
			var_class_0_
			    = (Class.forName
			       ("org.jempeg.empeg.protocol.Win32USBConnection"));
		    } catch (ClassNotFoundException classnotfoundexception) {
			NoClassDefFoundError noclassdeffounderror
			    = new NoClassDefFoundError;
			((UNCONSTRUCTED)noclassdeffounderror)
			    .NoClassDefFoundError
			    (classnotfoundexception.getMessage());
			throw noclassdeffounderror;
		    }
		    var_class = class$0 = var_class_0_;
		}
		Class var_class_1_;
		MONITORENTER (var_class_1_ = var_class);
		MISSING MONITORENTER
		synchronized (var_class_1_) {
		    findDevices();
		}
	    }
	} catch (Throwable t) {
	    Debug.println(t);
	}
    }
    
    protected void deviceDiscovered(String _deviceName) throws IOException {
	Win32USBConnectionFactory connFactory
	    = new Win32USBConnectionFactory(_deviceName);
	if (EmpegDiscoveryUtils.isEmpegConnected(connFactory)) {
	    String type = EmpegDiscoveryUtils.getPlayerType(connFactory);
	    String name
		= EmpegDiscoveryUtils.getEmpegName(connFactory,
						   EmpegDiscoveryUtils
						       .getDefaultName(type));
	    fireDeviceDiscovered(new BasicDevice(name, connFactory));
	}
    }
    
    protected void stopDiscovery0() {
	/* empty */
    }
    
    private native void findDevices();
}
