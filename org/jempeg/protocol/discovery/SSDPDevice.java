/* SSDPDevice - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol.discovery;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.util.ReflectionUtils;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLException;
import net.n3.nanoxml.XMLParserFactory;

import org.jempeg.protocol.IConnectionFactory;
import org.jempeg.protocol.SocketConnectionFactory;

public class SSDPDevice implements IDevice
{
    private String myName;
    private InetAddress myAddress;
    private Properties myProps;
    
    public SSDPDevice(InetAddress _address, Properties _props) {
	myAddress = _address;
	myProps = _props;
	try {
	    retrieveDescriptionXml();
	} catch (Throwable t) {
	    myName = "?";
	}
    }
    
    public InetAddress getAddress() {
	return myAddress;
    }
    
    public String getName() {
	return myName;
    }
    
    public IConnectionFactory getConnectionFactory() {
	try {
	    URL url = new URL(getLocation());
	    InetAddress hostAddress = InetAddress.getByName(url.getHost());
	    int port = url.getPort();
	    SocketConnectionFactory socketConnFactory
		= new SocketConnectionFactory(hostAddress, port);
	    return socketConnFactory;
	} catch (IOException e) {
	    throw new ChainedRuntimeException
		      ("Failed to create connection factory for device.", e);
	}
    }
    
    private synchronized void retrieveDescriptionXml()
	throws IOException, IllegalAccessException, ClassNotFoundException,
	       InstantiationException, XMLException {
	String descriptionXmlLocation
	    = "http://" + myAddress.getHostAddress() + ":80/description.xml";
	URL descriptionXmlUrl = new URL(descriptionXmlLocation);
	InputStream is = descriptionXmlUrl.openStream();
	try {
	    IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
	    net.n3.nanoxml.IXMLReader reader = new StdXMLReader(is);
	    parser.setReader(reader);
	    IXMLElement descriptionXml = (IXMLElement) parser.parse();
	    myName = descriptionXml.getFirstChildNamed("device")
			 .getFirstChildNamed
			 ("friendlyName").getContent();
	} catch (Object object) {
	    is.close();
	    throw object;
	}
	is.close();
    }
    
    public String getLocation() {
	return myProps.getProperty("LOCATION", "unknown");
    }
    
    public String getUSN() {
	return myProps.getProperty("USN", "unknown");
    }
    
    public String getST() {
	return myProps.getProperty("ST", "unknown");
    }
    
    public Properties getProperties() {
	return myProps;
    }
    
    public boolean equals(Object _obj) {
	if (_obj instanceof SSDPDevice
	    && ((SSDPDevice) _obj).getUSN().equals(getUSN()))
	    return true;
	return false;
    }
    
    public int hashCode() {
	return getUSN().hashCode();
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
