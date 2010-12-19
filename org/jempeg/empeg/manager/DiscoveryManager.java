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
package org.jempeg.empeg.manager;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

import org.jempeg.JEmplodeProperties;
import org.jempeg.empeg.protocol.discovery.NetworkEmpegDiscoverer;
import org.jempeg.empeg.protocol.discovery.SerialEmpegDiscoverer;
import org.jempeg.empeg.protocol.discovery.USBEmpegDiscovererFactory;
import org.jempeg.empeg.protocol.discovery.UnicastNetworkEmpegDiscoverer;
import org.jempeg.protocol.discovery.CompoundDiscoverer;
import org.jempeg.protocol.discovery.IDiscoverer;
import org.jempeg.protocol.discovery.IDiscoveryListener;
import org.jempeg.protocol.discovery.SSDPDevice;
import org.jempeg.protocol.discovery.SSDPDiscoverer;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

/**
 * EmpegDiscoveryManager is in charge of managing the discovery
 * process for finding Empegs. It provides an interface for
 * reading/writing settings, starting discovery, and cancelling
 * discovery.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.4 $
 */
public class DiscoveryManager {
	private PropertiesManager myPropertiesManager;
	private CompoundDiscoverer myEmpegDiscoverer;

	private boolean mySerialEnabled;
	private boolean myUsbEnabled;
	private boolean myEthernetEnabled;
	private boolean myUnicastEnabled;
	private boolean myAutoSelectEnabled;
	private InetAddress myUnicastAddress;
	private String myDoNotBroadcastTo;

	/**
	 * Instantiates a new EmpegDiscoveryManager,
	 * 
	 * @throws IOException if the initial settings cannot be loaded
	 */
	public DiscoveryManager() {
		myPropertiesManager = PropertiesManager.getInstance();
		loadSettings();
	}

	/**
	 * Loads the configuration settings for discovery.
	 * 
	 * @throws IOException if the settings cannot be loaded
	 */
	public void loadSettings() {
		mySerialEnabled = myPropertiesManager.getBooleanProperty(JEmplodeProperties.SERIAL_FLAG_PROPERTY);
		myUsbEnabled = myPropertiesManager.getBooleanProperty(JEmplodeProperties.USB_FLAG_PROPERTY);
		myEthernetEnabled = myPropertiesManager.getBooleanProperty(JEmplodeProperties.ETHERNET_BROADCAST_FLAG_PROPERTY);
		myUnicastEnabled = myPropertiesManager.getBooleanProperty(JEmplodeProperties.SPECIFIC_ADDRESS_FLAG_PROPERTY);
		String inetAddressString = myPropertiesManager.getProperty(JEmplodeProperties.SPECIFIC_ADDRESS_PROPERTY);
		if (inetAddressString != null && inetAddressString.length() > 0) {
			try {
				myUnicastAddress = InetAddress.getByName(inetAddressString);
			}
			catch (IOException e) {
				Debug.println(Debug.WARNING, e);
			}
		}
		else {
			myUnicastAddress = null;
		}
		myAutoSelectEnabled = myPropertiesManager.getBooleanProperty(JEmplodeProperties.AUTO_SELECT_PROPERTY);
		myDoNotBroadcastTo = myPropertiesManager.getProperty(JEmplodeProperties.DO_NOT_BROADCAST_TO_PROPERTY);
	}

	/**
	 * Saves the configuration settings for discovery.
	 * 
	 * @throws IOException if the settings cannot be saved
	 */
	public void saveSettings() throws IOException {
		myPropertiesManager.setBooleanProperty(JEmplodeProperties.SERIAL_FLAG_PROPERTY, mySerialEnabled);
		myPropertiesManager.setBooleanProperty(JEmplodeProperties.USB_FLAG_PROPERTY, myUsbEnabled);
		myPropertiesManager.setBooleanProperty(JEmplodeProperties.ETHERNET_BROADCAST_FLAG_PROPERTY, myEthernetEnabled);
		myPropertiesManager.setBooleanProperty(JEmplodeProperties.SPECIFIC_ADDRESS_FLAG_PROPERTY, myUnicastEnabled);
		if (myUnicastAddress == null) {
			myPropertiesManager.removeProperty(JEmplodeProperties.SPECIFIC_ADDRESS_PROPERTY);
		}
		else {
			myPropertiesManager.setProperty(JEmplodeProperties.SPECIFIC_ADDRESS_PROPERTY, myUnicastAddress.getHostAddress());
		}
		myPropertiesManager.setBooleanProperty(JEmplodeProperties.AUTO_SELECT_PROPERTY, myAutoSelectEnabled);
		myPropertiesManager.setProperty(JEmplodeProperties.DO_NOT_BROADCAST_TO_PROPERTY, myDoNotBroadcastTo);
		myPropertiesManager.save();
	}

	/**
	 * Resets the discoverer and attempts to find Empegs again
	 * with the current settings.
	 * 
	 * @param _listener the listener to fire discovery events to
	 * @throws IOException if there is a problem communicating
	 */
	public void refresh(IDiscoveryListener _listener) {
		cancel();

		int timeoutMillis = 10000;

		myEmpegDiscoverer = new CompoundDiscoverer();
		myEmpegDiscoverer.addDiscoveryListener(_listener);

		if (mySerialEnabled) {
			SerialEmpegDiscoverer serialEmpegDiscoverer = new SerialEmpegDiscoverer(timeoutMillis);
			myEmpegDiscoverer.addDiscoverer(serialEmpegDiscoverer);
		}

		if (myUsbEnabled) {
			IDiscoverer usbEmpegDiscoverer = USBEmpegDiscovererFactory.createUSBDiscoverer();
			if (usbEmpegDiscoverer != null) {
				myEmpegDiscoverer.addDiscoverer(usbEmpegDiscoverer);
			}
		}

		if (myEthernetEnabled) {
			NetworkEmpegDiscoverer networkEmpegDiscoverer = new NetworkEmpegDiscoverer(timeoutMillis);
			networkEmpegDiscoverer.setDoNotBroadcastTo(getDoNotBroadcastTo());
			myEmpegDiscoverer.addDiscoverer(networkEmpegDiscoverer);
		}

		if (myUnicastEnabled) {
			UnicastNetworkEmpegDiscoverer unicastNetworkEmpegDiscoverer = new UnicastNetworkEmpegDiscoverer(myUnicastAddress, timeoutMillis);
			myEmpegDiscoverer.addDiscoverer(unicastNetworkEmpegDiscoverer);
		}

		String[] uris = new String[] { "urn:empeg-com:protocol2", "urn:empeg-com:receiver-server", "urn:empeg-com:empeg-car" };
		SSDPDiscoverer karmaDiscoverer = new SSDPDiscoverer(uris);
		myEmpegDiscoverer.addDiscoverer(karmaDiscoverer);

		try {
			String karmaHost = PropertiesManager.getInstance().getProperty("karma.host");
			if (karmaHost != null) {
				String karmaPort = PropertiesManager.getInstance().getProperty("karma.port");
				Properties props = new Properties();
				props.put("LOCATION", "http://" + karmaHost + ":" + karmaPort + "/");
				props.put("USN", "hardcoded");
				props.put("ST", "urn:empeg-com:protocol2");
				myEmpegDiscoverer.deviceDiscovered(myEmpegDiscoverer, new SSDPDevice(InetAddress.getByName(karmaHost), props));
			}
		}
		catch (Throwable t) {
			Debug.println(t);
		}

		myEmpegDiscoverer.startDiscovery();
	}

	/**
	 * Cancels the current discovery.
	 */
	public void cancel() {
		if (myEmpegDiscoverer != null) {
			myEmpegDiscoverer.stopDiscovery();
		}
	}

	/**
	 * Returns whether or not this discovery manager was cancelled.
	 * 
	 * @return whether or not this discovery manager was cancelled
	 */
	public boolean isCancelled() {
		boolean cancelled = false;
		if (myEmpegDiscoverer != null) {
			cancelled = !myEmpegDiscoverer.isRunning();
		}
		return cancelled;
	}

	/**
	 * Returns the discoverer that is used by this DiscoveryManager.
	 * 
	 * @return the discoverer that is used by this DiscoveryManager
	 */
	public IDiscoverer getDiscoverer() {
		return myEmpegDiscoverer;
	}

	/**
	 * Returns the autoSelectEnabled.
	 * @return boolean
	 */
	public boolean isAutoSelectEnabled() {
		return myAutoSelectEnabled;
	}

	/**
	 * Returns the ethernetEnabled.
	 * @return boolean
	 */
	public boolean isEthernetEnabled() {
		return myEthernetEnabled;
	}

	/**
	 * Returns the serialEnabled.
	 * @return boolean
	 */
	public boolean isSerialEnabled() {
		return mySerialEnabled;
	}

	/**
	 * Returns the unicastAddress.
	 * @return InetAddress
	 */
	public InetAddress getUnicastAddress() {
		return myUnicastAddress;
	}

	/**
	 * Returns the unicastEnabled.
	 * @return boolean
	 */
	public boolean isUnicastEnabled() {
		return myUnicastEnabled;
	}

	/**
	 * Returns the usbEnabled.
	 * @return boolean
	 */
	public boolean isUsbEnabled() {
		return myUsbEnabled;
	}

	/**
	 * Sets the autoSelectEnabled.
	 * @param autoSelectEnabled The autoSelectEnabled to set
	 */
	public void setAutoSelectEnabled(boolean _autoSelectEnabled) {
		myAutoSelectEnabled = _autoSelectEnabled;
	}

	/**
	 * Sets the ethernetEnabled.
	 * @param ethernetEnabled The ethernetEnabled to set
	 */
	public void setEthernetEnabled(boolean _ethernetEnabled) {
		myEthernetEnabled = _ethernetEnabled;
	}

	/**
	 * Sets the serialEnabled.
	 * @param serialEnabled The serialEnabled to set
	 */
	public void setSerialEnabled(boolean _serialEnabled) {
		mySerialEnabled = _serialEnabled;
	}

	/**
	 * Sets the unicastAddress.
	 * @param unicastAddress The unicastAddress to set
	 */
	public void setUnicastAddress(InetAddress _unicastAddress) {
		myUnicastAddress = _unicastAddress;
	}

	/**
	 * Sets the unicastEnabled.
	 * @param unicastEnabled The unicastEnabled to set
	 */
	public void setUnicastEnabled(boolean _unicastEnabled) {
		myUnicastEnabled = _unicastEnabled;
	}

	/**
	 * Sets the usbEnabled.
	 * @param usbEnabled The usbEnabled to set
	 */
	public void setUsbEnabled(boolean _usbEnabled) {
		myUsbEnabled = _usbEnabled;
	}

	/**
	 * Returns the doNotBroadcastTo.
	 * @return String
	 */
	public String getDoNotBroadcastTo() {
		return myDoNotBroadcastTo;
	}

	/**
	 * Sets the doNotBroadcastTo.
	 * @param doNotBroadcastTo The doNotBroadcastTo to set
	 */
	public void setDoNotBroadcastTo(String _doNotBroadcastTo) {
		myDoNotBroadcastTo = _doNotBroadcastTo;
	}

}
