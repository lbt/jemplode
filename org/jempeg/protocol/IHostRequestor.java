/* IHostRequestor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.io.IOException;
import java.net.InetAddress;

public interface IHostRequestor
{
    public InetAddress requestHost(InetAddress inetaddress) throws IOException;
}
