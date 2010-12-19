/* PearlSocketConnectionFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2;
import java.net.InetAddress;

import org.jempeg.protocol.SocketConnectionFactory;

public class PearlSocketConnectionFactory extends SocketConnectionFactory
{
    public PearlSocketConnectionFactory(InetAddress _address) {
	super(_address, 8302);
    }
}
