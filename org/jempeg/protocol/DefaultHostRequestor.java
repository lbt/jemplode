package org.jempeg.protocol;

import java.awt.Window;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JOptionPane;

import com.inzyme.text.ResourceBundleUtils;

/**
 * A host requestor that can input a password using a dialog box.
 * 
 * @author Mike Schrag
 */
public class DefaultHostRequestor implements IHostRequestor {
	private static Window myTopLevelWindow;
	
	/**
	 * Sets the top level window for this application that the host
	 * dialog will be modal relative to.
	 * 
	 * @param _topLevelWindow the window to use as the parent of the host dialog
	 */
	public static void setTopLevelWindow(Window _topLevelWindow) {
		myTopLevelWindow = _topLevelWindow;
	}
	
	public InetAddress requestHost(InetAddress _lastKnownHost) throws IOException {
		String hostAddressStr = (String) JOptionPane.showInputDialog(myTopLevelWindow, ResourceBundleUtils.getUIString("enterHostname.prompt"), ResourceBundleUtils.getUIString("enterHostname.frameTitle"), JOptionPane.QUESTION_MESSAGE, null, null, _lastKnownHost == null ? "" : _lastKnownHost.getHostAddress());
		InetAddress hostAddress = (hostAddressStr == null) ? null : InetAddress.getByName(hostAddressStr);
		return hostAddress;
	}
}
