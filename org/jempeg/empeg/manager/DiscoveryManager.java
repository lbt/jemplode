/* DiscoveryManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

import org.jempeg.empeg.protocol.discovery.NetworkEmpegDiscoverer;
import org.jempeg.empeg.protocol.discovery.SerialEmpegDiscoverer;
import org.jempeg.empeg.protocol.discovery.USBEmpegDiscovererFactory;
import org.jempeg.empeg.protocol.discovery.UnicastNetworkEmpegDiscoverer;
import org.jempeg.protocol.discovery.CompoundDiscoverer;
import org.jempeg.protocol.discovery.IDiscoverer;
import org.jempeg.protocol.discovery.IDiscoveryListener;
import org.jempeg.protocol.discovery.SSDPDevice;
import org.jempeg.protocol.discovery.SSDPDiscoverer;

public class DiscoveryManager
{
    private PropertiesManager myPropertiesManager
	= PropertiesManager.getInstance();
    private CompoundDiscoverer myEmpegDiscoverer;
    private boolean mySerialEnabled;
    private boolean myUsbEnabled;
    private boolean myEthernetEnabled;
    private boolean myUnicastEnabled;
    private boolean myAutoSelectEnabled;
    private InetAddress myUnicastAddress;
    private String myDoNotBroadcastTo;
    
    public DiscoveryManager() {
	loadSettings();
    }
    
    public void loadSettings() {
	mySerialEnabled
	    = myPropertiesManager
		  .getBooleanProperty("jempeg.connections.serialPort");
	myUsbEnabled
	    = myPropertiesManager.getBooleanProperty("jempeg.connections.USB");
	myEthernetEnabled
	    = myPropertiesManager
		  .getBooleanProperty("jempeg.connections.ethernetBroadcast");
	myUnicastEnabled
	    = myPropertiesManager
		  .getBooleanProperty("jempeg.connections.specificAddress");
	String inetAddressString
	    = myPropertiesManager
		  .getProperty("jempeg.connections.inetAddress");
	do {
	    if (inetAddressString != null && inetAddressString.length() > 0) {
		try {
		    myUnicastAddress
			= InetAddress.getByName(inetAddressString);
		    break;
		} catch (IOException e) {
		    Debug.println(8, e);
		    break;
		}
	    }
	    myUnicastAddress = null;
	} while (false);
	myAutoSelectEnabled
	    = myPropertiesManager
		  .getBooleanProperty("jempeg.connections.autoSelect");
	myDoNotBroadcastTo
	    = myPropertiesManager
		  .getProperty("jempeg.connections.doNotBroadcastTo");
    }
    
    public void saveSettings() throws IOException {
	myPropertiesManager.setBooleanProperty("jempeg.connections.serialPort",
					       mySerialEnabled);
	myPropertiesManager.setBooleanProperty("jempeg.connections.USB",
					       myUsbEnabled);
	myPropertiesManager.setBooleanProperty
	    ("jempeg.connections.ethernetBroadcast", myEthernetEnabled);
	myPropertiesManager.setBooleanProperty
	    ("jempeg.connections.specificAddress", myUnicastEnabled);
	if (myUnicastAddress == null)
	    myPropertiesManager
		.removeProperty("jempeg.connections.inetAddress");
	else
	    myPropertiesManager.setProperty("jempeg.connections.inetAddress",
					    myUnicastAddress.getHostAddress());
	myPropertiesManager.setBooleanProperty("jempeg.connections.autoSelect",
					       myAutoSelectEnabled);
	myPropertiesManager.setProperty("jempeg.connections.doNotBroadcastTo",
					myDoNotBroadcastTo);
	myPropertiesManager.save();
    }
    
    public void refresh(IDiscoveryListener _listener) {
	cancel();
	int timeoutMillis = 10000;
	myEmpegDiscoverer = new CompoundDiscoverer();
	myEmpegDiscoverer.addDiscoveryListener(_listener);
	if (mySerialEnabled) {
	    SerialEmpegDiscoverer serialEmpegDiscoverer
		= new SerialEmpegDiscoverer(timeoutMillis);
	    myEmpegDiscoverer.addDiscoverer(serialEmpegDiscoverer);
	}
	if (myUsbEnabled) {
	    IDiscoverer usbEmpegDiscoverer
		= USBEmpegDiscovererFactory.createUSBDiscoverer();
	    if (usbEmpegDiscoverer != null)
		myEmpegDiscoverer.addDiscoverer(usbEmpegDiscoverer);
	}
	if (myEthernetEnabled) {
	    NetworkEmpegDiscoverer networkEmpegDiscoverer
		= new NetworkEmpegDiscoverer(timeoutMillis);
	    networkEmpegDiscoverer.setDoNotBroadcastTo(getDoNotBroadcastTo());
	    myEmpegDiscoverer.addDiscoverer(networkEmpegDiscoverer);
	}
	if (myUnicastEnabled) {
	    UnicastNetworkEmpegDiscoverer unicastNetworkEmpegDiscoverer
		= new UnicastNetworkEmpegDiscoverer(myUnicastAddress,
						    timeoutMillis);
	    myEmpegDiscoverer.addDiscoverer(unicastNetworkEmpegDiscoverer);
	}
	String[] uris
	    = { "urn:empeg-com:protocol2", "urn:empeg-com:receiver-server",
		"urn:empeg-com:empeg-car" };
	SSDPDiscoverer karmaDiscoverer = new SSDPDiscoverer(uris);
	myEmpegDiscoverer.addDiscoverer(karmaDiscoverer);
	try {
	    String karmaHost
		= PropertiesManager.getInstance().getProperty("karma.host");
	    if (karmaHost != null) {
		String karmaPort = PropertiesManager.getInstance()
				       .getProperty("karma.port");
		Properties props = new Properties();
		props.put("LOCATION",
			  "http://" + karmaHost + ":" + karmaPort + "/");
		props.put("USN", "hardcoded");
		props.put("ST", "urn:empeg-com:protocol2");
		myEmpegDiscoverer.deviceDiscovered
		    (myEmpegDiscoverer,
		     new SSDPDevice(InetAddress.getByName(karmaHost), props));
	    }
	} catch (Throwable t) {
	    Debug.println(t);
	}
	myEmpegDiscoverer.startDiscovery();
    }
    
    public void cancel() {
	if (myEmpegDiscoverer != null)
	    myEmpegDiscoverer.stopDiscovery();
    }
    
    public boolean isCancelled() {
	boolean cancelled = false;
	if (myEmpegDiscoverer != null)
	    cancelled = !myEmpegDiscoverer.isRunning();
	return cancelled;
    }
    
    public IDiscoverer getDiscoverer() {
	return myEmpegDiscoverer;
    }
    
    public boolean isAutoSelectEnabled() {
	return myAutoSelectEnabled;
    }
    
    public boolean isEthernetEnabled() {
	return myEthernetEnabled;
    }
    
    public boolean isSerialEnabled() {
	return mySerialEnabled;
    }
    
    public InetAddress getUnicastAddress() {
	return myUnicastAddress;
    }
    
    public boolean isUnicastEnabled() {
	return myUnicastEnabled;
    }
    
    public boolean isUsbEnabled() {
	return myUsbEnabled;
    }
    
    public void setAutoSelectEnabled(boolean _autoSelectEnabled) {
	myAutoSelectEnabled = _autoSelectEnabled;
    }
    
    public void setEthernetEnabled(boolean _ethernetEnabled) {
	myEthernetEnabled = _ethernetEnabled;
    }
    
    public void setSerialEnabled(boolean _serialEnabled) {
	mySerialEnabled = _serialEnabled;
    }
    
    public void setUnicastAddress(InetAddress _unicastAddress) {
	myUnicastAddress = _unicastAddress;
    }
    
    public void setUnicastEnabled(boolean _unicastEnabled) {
	myUnicastEnabled = _unicastEnabled;
    }
    
    public void setUsbEnabled(boolean _usbEnabled) {
	myUsbEnabled = _usbEnabled;
    }
    
    public String getDoNotBroadcastTo() {
	return myDoNotBroadcastTo;
    }
    
    public void setDoNotBroadcastTo(String _doNotBroadcastTo) {
	myDoNotBroadcastTo = _doNotBroadcastTo;
    }
}
