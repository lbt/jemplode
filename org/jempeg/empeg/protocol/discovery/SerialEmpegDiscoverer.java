/* SerialEmpegDiscoverer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.discovery;
import java.io.IOException;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;

import com.inzyme.util.Debug;

import org.jempeg.empeg.protocol.SerialConnectionFactory;
import org.jempeg.protocol.discovery.AbstractDiscoverer;
import org.jempeg.protocol.discovery.BasicDevice;

public class SerialEmpegDiscoverer extends AbstractDiscoverer
{
    public static final int EMPEG_BAUD_RATE = 115200;
    private CommPortIdentifier myPortIdentifier;
    /*synthetic*/ static Class class$0;
    
    public SerialEmpegDiscoverer(int _timeoutMillis) {
	/* empty */
    }
    
    public SerialEmpegDiscoverer(String _portName) throws NoSuchPortException {
	myPortIdentifier = CommPortIdentifier.getPortIdentifier(_portName);
    }
    
    protected void stopDiscovery0() {
	/* empty */
    }
    
    protected void startDiscovery0() {
	try {
	    Class var_class = class$0;
	    if (var_class == null) {
		Class var_class_0_;
		try {
		    var_class_0_
			= (Class.forName
			   ("org.jempeg.empeg.protocol.SerialConnection"));
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
		if (myPortIdentifier == null) {
		    Enumeration portIdentifiers
			= CommPortIdentifier.getPortIdentifiers();
		    while (isRunning()) {
			if (!portIdentifiers.hasMoreElements())
			    break;
			final CommPortIdentifier portIdentifier
			    = ((CommPortIdentifier)
			       portIdentifiers.nextElement());
			if (portIdentifier.getPortType() == 1
			    && !portIdentifier.isCurrentlyOwned()) {
			    Thread t = new Thread(new Runnable() {
				public void run() {
				    try {
					discover0(portIdentifier);
				    } catch (IOException e) {
					Debug.println(e);
				    }
				}
			    }, "jEmplode Serial Discovery: " + portIdentifier);
			    t.start();
			}
		    }
		} else
		    discover0(myPortIdentifier);
	    }
	} catch (Exception e) {
	    Debug.println(e);
	}
    }
    
    protected boolean discover0(CommPortIdentifier _portIdentifier)
	throws IOException {
	boolean discovered = false;
	for (int i = 0; isRunning() && !discovered && i < 3; i++) {
	    try {
		SerialConnectionFactory connectionFactory
		    = new SerialConnectionFactory(_portIdentifier, 115200);
		if (EmpegDiscoveryUtils.isEmpegConnected(connectionFactory)) {
		    String type
			= EmpegDiscoveryUtils.getPlayerType(connectionFactory);
		    String name = (EmpegDiscoveryUtils.getEmpegName
				   (connectionFactory,
				    EmpegDiscoveryUtils.getDefaultName(type)));
		    fireDeviceDiscovered(new BasicDevice(name,
							 connectionFactory));
		    discovered = true;
		} else
		    discovered = false;
	    } catch (Throwable t) {
		Debug.println(t);
	    }
	}
	return discovered;
    }
}
