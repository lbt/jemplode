/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol.discovery;

import org.jempeg.protocol.discovery.IDiscoverer;

import com.inzyme.util.Debug;

public class USBEmpegDiscovererFactory {
  public static IDiscoverer createUSBDiscoverer() {
    IDiscoverer discoverer = null;
    try {
      String os = System.getProperty("os.version").toLowerCase();
      if (os.indexOf("windows") != 0) {
        discoverer = new Win32USBEmpegDiscoverer();
      } else {
        Debug.println(Debug.WARNING, "There currently is no USB implementation for your operating system.");
      }
    }
    catch (Throwable t) {
      Debug.println(t);
    }
    return discoverer;
  }
}
