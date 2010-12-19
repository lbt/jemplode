/* PearlUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;
import java.util.Vector;

import com.inzyme.progress.IProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.UIUtils;
import com.inzyme.util.ApplicationUtils;
import com.inzyme.util.Debug;
import com.rio.tags.FlacTagExtractor;
import com.rio.tags.OggTagExtractor;

import org.jempeg.JEmplodeProperties;
import org.jempeg.protocol.HostRequestorFactory;
import org.jempeg.protocol.SocketConnectionFactory;
import org.jempeg.protocol.discovery.AccumulatingDiscoveryListener;
import org.jempeg.protocol.discovery.IDevice;
import org.jempeg.protocol.discovery.IDiscoverer;
import org.jempeg.protocol.discovery.SSDPDevice;
import org.jempeg.protocol.discovery.SSDPDiscoverer;
import org.jempeg.protocol.discovery.TimeoutDiscoveryListener;
import org.jempeg.tags.ASFTagExtractor;
import org.jempeg.tags.TagExtractorFactory;
import org.jempeg.tags.id3.ID3TagExtractor;

public class PearlUtils
{
    public static final String PEARL_URN = "urn:empeg-com:protocol2";
    
    public static boolean initializeApplication() {
	Debug.getDebugLevel();
	ResourceBundleUtils.putBaseName("errors", "com.rio.errors");
	ResourceBundleUtils.putBaseName("ui", "com.rio.ui");
	UIUtils.initializeOSX("Rio");
	boolean hasPrivileges = true;
	try {
	    PropertiesManager.initializeInstance
		("Rio Music Manager Lite Properties",
		 new File(ApplicationUtils.getSettingsFolder(), "rmmlrc"));
	} catch (Throwable t) {
	    hasPrivileges = false;
	    Debug.println(t);
	    PropertiesManager.getInstance();
	}
	JEmplodeProperties.initializeDefaults();
	String language
	    = PropertiesManager.getInstance().getProperty("locale.language",
							  null);
	if (language != null) {
	    String country
		= PropertiesManager.getInstance().getProperty("locale.country",
							      "");
	    String variant
		= PropertiesManager.getInstance().getProperty("locale.variant",
							      "");
	    Locale.setDefault(new Locale(language, country, variant));
	}
	return hasPrivileges;
    }
    
    public static void initializeTagExtractors(boolean _allowUnknownTypes) {
	PropertiesManager.getInstance().setBooleanProperty
	    ("jempeg.allowUnknownTypes", _allowUnknownTypes);
	TagExtractorFactory.addTagExtractor(new FlacTagExtractor());
	TagExtractorFactory.addTagExtractor(new ID3TagExtractor());
	TagExtractorFactory.addTagExtractor(new ASFTagExtractor());
	TagExtractorFactory.addTagExtractor(new OggTagExtractor());
    }
    
    public static InetAddress findPearl
	(IProgressListener _progressListener) throws IOException {
	_progressListener.setStopEnabled(true);
	_progressListener.taskStarted
	    (ResourceBundleUtils.getUIString("searchingForDevice.message"));
	_progressListener.progressStarted();
	InetAddress inetaddress;
	try {
	    final IDiscoverer discoverer
		= new SSDPDiscoverer(new String[]
				     { "urn:empeg-com:protocol2" });
	    _progressListener.addStopListener(new ActionListener() {
		public void actionPerformed(ActionEvent _event) {
		    discoverer.stopDiscovery();
		}
	    });
	    InetAddress hostAddress = null;
	    String forceHostAddress
		= PropertiesManager.getInstance().getProperty("karma.host",
							      null);
	    if (forceHostAddress != null) {
		System.out.println
		    ("PearlUtils.findPearl: Discovery overridden by karma.host setting of "
		     + forceHostAddress);
		hostAddress = InetAddress.getByName(forceHostAddress);
	    } else {
		AccumulatingDiscoveryListener accumulatingListener
		    = new AccumulatingDiscoveryListener();
		discoverer.addDiscoveryListener(accumulatingListener);
		TimeoutDiscoveryListener timeoutListener
		    = new TimeoutDiscoveryListener(discoverer, 2000);
		discoverer.addDiscoveryListener(timeoutListener);
		discoverer.startDiscovery();
		IDevice[] devices = accumulatingListener.getDevices();
		Vector karmas = new Vector();
		for (int i = 0; i < devices.length; i++) {
		    if (((SSDPDevice) devices[i]).getST()
			    .equals("urn:empeg-com:protocol2"))
			karmas.addElement(devices[i]);
		}
		if (karmas.size() != 0 && karmas.size() == 1) {
		    IDevice device = (IDevice) karmas.elementAt(0);
		    SocketConnectionFactory connFactory
			= ((SocketConnectionFactory)
			   device.getConnectionFactory());
		    hostAddress = connFactory.getAddress();
		}
	    }
	    if (hostAddress == null) {
		InetAddress lastKnownHost;
		try {
		    String lastKnownHostStr
			= PropertiesManager.getInstance()
			      .getProperty("lastKnownHost", null);
		    lastKnownHost
			= (lastKnownHostStr == null ? null
			   : InetAddress.getByName(lastKnownHostStr));
		} catch (Throwable t) {
		    Debug.println(t);
		    lastKnownHost = null;
		}
		if (hostAddress == null)
		    hostAddress = HostRequestorFactory.getInstance()
				      .requestHost(lastKnownHost);
	    }
	    if (hostAddress == null)
		Debug.handleError(ResourceBundleUtils
				      .getErrorString("noHostSpecified"),
				  true);
	    else
		PropertiesManager.getInstance().setProperty
		    ("lastKnownHost", hostAddress.getHostAddress());
	    inetaddress = hostAddress;
	} catch (Object object) {
	    _progressListener.progressCompleted();
	    throw object;
	}
	_progressListener.progressCompleted();
	return inetaddress;
    }
}
