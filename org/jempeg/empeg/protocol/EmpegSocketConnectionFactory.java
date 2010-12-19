/* EmpegSocketConnectionFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import java.net.InetAddress;

import org.jempeg.protocol.SocketConnectionFactory;

public class EmpegSocketConnectionFactory extends SocketConnectionFactory
{
    public EmpegSocketConnectionFactory(InetAddress _address) {
	super(_address, 8300);
    }
}
