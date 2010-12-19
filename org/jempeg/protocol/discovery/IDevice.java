/* IDevice - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol.discovery;
import org.jempeg.protocol.IConnectionFactory;

public interface IDevice
{
    public String getName();
    
    public IConnectionFactory getConnectionFactory();
}
