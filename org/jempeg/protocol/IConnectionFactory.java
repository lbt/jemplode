/* IConnectionFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;

public interface IConnectionFactory
{
    public IConnection createConnection();
    
    public String getLocationName();
}
