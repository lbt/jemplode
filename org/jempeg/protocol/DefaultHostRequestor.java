/* DefaultHostRequestor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.awt.Window;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JOptionPane;

import com.inzyme.text.ResourceBundleUtils;

public class DefaultHostRequestor implements IHostRequestor
{
    private static Window myTopLevelWindow;
    
    public static void setTopLevelWindow(Window _topLevelWindow) {
	myTopLevelWindow = _topLevelWindow;
    }
    
    public InetAddress requestHost(InetAddress _lastKnownHost)
	throws IOException {
	String hostAddressStr
	    = ((String)
	       (JOptionPane.showInputDialog
		(myTopLevelWindow,
		 ResourceBundleUtils.getUIString("enterHostname.prompt"),
		 ResourceBundleUtils.getUIString("enterHostname.frameTitle"),
		 3, null, null,
		 (_lastKnownHost == null ? ""
		  : _lastKnownHost.getHostAddress()))));
	InetAddress hostAddress = (hostAddressStr == null ? null
				   : InetAddress.getByName(hostAddressStr));
	return hostAddress;
    }
}
