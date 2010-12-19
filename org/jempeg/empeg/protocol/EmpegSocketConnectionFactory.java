package org.jempeg.empeg.protocol;

import java.net.InetAddress;

import org.jempeg.protocol.SocketConnectionFactory;

public class EmpegSocketConnectionFactory extends SocketConnectionFactory {
	public EmpegSocketConnectionFactory(InetAddress _address) {
		super(_address, EmpegProtocolClient.PROTOCOL_TCP_PORT);
	}
}
