/* USBEmpegDiscovererFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.discovery;
import com.inzyme.util.Debug;

import org.jempeg.protocol.discovery.IDiscoverer;

public class USBEmpegDiscovererFactory
{
    public static IDiscoverer createUSBDiscoverer() {
	IDiscoverer discoverer = null;
	try {
	    String os = System.getProperty("os.version").toLowerCase();
	    if (os.indexOf("windows") != 0)
		discoverer = new Win32USBEmpegDiscoverer();
	    else
		Debug.println
		    (8,
		     "There currently is no USB implementation for your operating system.");
	} catch (Throwable t) {
	    Debug.println(t);
	}
	return discoverer;
    }
}
