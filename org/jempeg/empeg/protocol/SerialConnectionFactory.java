/* SerialConnectionFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import javax.comm.CommPortIdentifier;

import com.inzyme.util.ReflectionUtils;

import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.IConnectionFactory;

public class SerialConnectionFactory implements IConnectionFactory
{
    private CommPortIdentifier myPortIdentifier;
    private int myBaudRate;
    
    public SerialConnectionFactory(CommPortIdentifier _portIdentifier,
				   int _baudRate) {
	myPortIdentifier = _portIdentifier;
	myBaudRate = _baudRate;
    }
    
    public CommPortIdentifier getPortIdentifier() {
	return myPortIdentifier;
    }
    
    public int getBaudRate() {
	return myBaudRate;
    }
    
    public IConnection createConnection() {
	return new SerialConnection(myPortIdentifier, myBaudRate);
    }
    
    public String getLocationName() {
	return myPortIdentifier.getName();
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
