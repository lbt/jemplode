/* BasicDevice - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol.discovery;
import com.inzyme.util.ReflectionUtils;

import org.jempeg.protocol.IConnectionFactory;

public class BasicDevice implements IDevice
{
    private String myName;
    private IConnectionFactory myConnectionFactory;
    
    public BasicDevice(String _name, IConnectionFactory _connectionFactory) {
	myName = _name;
	myConnectionFactory = _connectionFactory;
    }
    
    public String getName() {
	return myName;
    }
    
    public IConnectionFactory getConnectionFactory() {
	return myConnectionFactory;
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
