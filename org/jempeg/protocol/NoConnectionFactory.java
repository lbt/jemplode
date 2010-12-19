/* NoConnectionFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;

public class NoConnectionFactory implements IConnectionFactory
{
    public IConnection createConnection() {
	return null;
    }
    
    public String getLocationName() {
	return "";
    }
}
