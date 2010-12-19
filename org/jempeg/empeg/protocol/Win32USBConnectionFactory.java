/* Win32USBConnectionFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.IConnectionFactory;

public class Win32USBConnectionFactory implements IConnectionFactory
{
    private String myDeviceName;
    
    public Win32USBConnectionFactory(String _deviceName) {
	myDeviceName = _deviceName;
    }
    
    public IConnection createConnection() {
	return new Win32USBConnection(myDeviceName);
    }
    
    public String getLocationName() {
	return "USB";
    }
}
