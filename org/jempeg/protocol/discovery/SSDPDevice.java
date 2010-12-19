package org.jempeg.protocol.discovery;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLException;
import net.n3.nanoxml.XMLParserFactory;

import org.jempeg.protocol.IConnectionFactory;
import org.jempeg.protocol.SocketConnectionFactory;

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.util.ReflectionUtils;

/**
 * Represents an device that was discovered with SSDP and 
 * provides access to the SSDP properties.
 * 
 * @author Mike Schrag
 */
public class SSDPDevice implements IDevice {
	private String myName;
	private InetAddress myAddress;
	private Properties myProps;

	/**
	 * Constructs an SSDP-discovered device.
	 * 
	 * @param _address the address of the device
	 * @param _props the SSDP properties
	 */
	public SSDPDevice(InetAddress _address, Properties _props) {
		myAddress = _address;
		myProps = _props;
		try {
			retrieveDescriptionXml();
		}
		catch (Throwable t) {
			myName = "?";
		}
	}

	/**
	 * Returns the address of this device.
	 */
	public InetAddress getAddress() {
		return myAddress;
	}

	/**
	 * Returns the display name of this device.
	 */
	public String getName() {
		return myName;
	}

	public IConnectionFactory getConnectionFactory() {
		try {
			URL url = new URL(getLocation());
			InetAddress hostAddress = InetAddress.getByName(url.getHost());
			int port = url.getPort();
			SocketConnectionFactory socketConnFactory = new SocketConnectionFactory(hostAddress, port);
			return socketConnFactory;
		}
		catch (IOException e) {
			throw new ChainedRuntimeException("Failed to create connection factory for device.", e);
		}
	}
	
	private synchronized void retrieveDescriptionXml() throws IOException, IllegalAccessException, ClassNotFoundException, InstantiationException, XMLException {
		String descriptionXmlLocation = "http://" + myAddress.getHostAddress() + ":80/description.xml";
		URL descriptionXmlUrl = new URL(descriptionXmlLocation);
		InputStream is = descriptionXmlUrl.openStream();
		try {
			IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
			IXMLReader reader = new StdXMLReader(is);
			parser.setReader(reader);
			IXMLElement descriptionXml = (IXMLElement) parser.parse();
			myName = descriptionXml.getFirstChildNamed("device").getFirstChildNamed("friendlyName").getContent();
		}
		finally {
			is.close();
		}
	}
	
	/**
	 * Returns the LOCATION SSDP property.
	 */
	public String getLocation() {
		return myProps.getProperty("LOCATION", "unknown");
	}

	/**
	 * Returns the USN SSDP property.
	 */
	public String getUSN() {
		return myProps.getProperty("USN", "unknown");
	}

	/**
	 * Returns the ST SSDP property.
	 */
	public String getST() {
		return myProps.getProperty("ST", "unknown");
	}

	/**
	 * Returns the set of SSDP properties.
	 */
	public Properties getProperties() {
		return myProps;
	}

	/**
	 * Compares SSDPDevice USN's
	 */
	public boolean equals(Object _obj) {
		return (_obj instanceof SSDPDevice) && (((SSDPDevice) _obj).getUSN().equals(getUSN()));
	}

	public int hashCode() {
		return getUSN().hashCode();
	}

	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
