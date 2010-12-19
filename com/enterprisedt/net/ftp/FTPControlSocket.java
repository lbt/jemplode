/* FTPControlSocket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.enterprisedt.net.ftp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPControlSocket
{
    static final String EOL = "\r\n";
    private static final int CONTROL_PORT = 21;
    private boolean debugResponses = false;
    private Socket controlSock = null;
    private Writer writer = null;
    private BufferedReader reader = null;
    
    public FTPControlSocket(String remoteHost)
	throws IOException, FTPException {
	this(remoteHost, 21);
    }
    
    public FTPControlSocket(String remoteHost, int controlPort)
	throws IOException, FTPException {
	controlSock = new Socket(remoteHost, controlPort);
	initStreams();
	validateConnection();
    }
    
    public FTPControlSocket(InetAddress remoteAddr)
	throws IOException, FTPException {
	this(remoteAddr, 21);
    }
    
    public FTPControlSocket(InetAddress remoteAddr, int controlPort)
	throws IOException, FTPException {
	controlSock = new Socket(remoteAddr, controlPort);
	initStreams();
	validateConnection();
    }
    
    private void validateConnection() throws IOException, FTPException {
	String reply = readReply();
	validateReply(reply, "220");
    }
    
    private void initStreams() throws IOException {
	InputStream is = controlSock.getInputStream();
	reader = new BufferedReader(new InputStreamReader(is));
	OutputStream os = controlSock.getOutputStream();
	writer = new OutputStreamWriter(os);
    }
    
    String getRemoteHostName() {
	InetAddress addr = controlSock.getInetAddress();
	return addr.getHostName();
    }
    
    void setTimeout(int millis) throws IOException {
	if (controlSock == null)
	    throw new IllegalStateException
		      ("Failed to set timeout - no control socket");
	controlSock.setSoTimeout(millis);
    }
    
    public void logout() throws IOException {
	writer.close();
	reader.close();
	controlSock.close();
    }
    
    FTPDataSocket createDataSocket(FTPConnectMode connectMode)
	throws IOException, FTPException {
	if (connectMode == FTPConnectMode.ACTIVE)
	    return new FTPDataSocket(createDataSocketActive());
	return new FTPDataSocket(createDataSocketPASV());
    }
    
    ServerSocket createDataSocketActive() throws IOException, FTPException {
	ServerSocket socket = new ServerSocket(0);
	InetAddress localhost = controlSock.getLocalAddress();
	setDataPort(localhost, (short) socket.getLocalPort());
	return socket;
    }
    
    private short toUnsignedShort(byte value) {
	return value < 0 ? (short) (value + 256) : (short) value;
    }
    
    protected byte[] toByteArray(short value) {
	byte[] bytes = new byte[2];
	bytes[0] = (byte) (value >> 8);
	bytes[1] = (byte) (value & 0xff);
	return bytes;
    }
    
    private void setDataPort(InetAddress host, short portNo)
	throws IOException, FTPException {
	byte[] hostBytes = host.getAddress();
	byte[] portBytes = toByteArray(portNo);
	String cmd = ("PORT " + toUnsignedShort(hostBytes[0]) + ","
		      + toUnsignedShort(hostBytes[1]) + ","
		      + toUnsignedShort(hostBytes[2]) + ","
		      + toUnsignedShort(hostBytes[3]) + ","
		      + toUnsignedShort(portBytes[0]) + ","
		      + toUnsignedShort(portBytes[1]));
	String reply = sendCommand(cmd);
	validateReply(reply, "200");
    }
    
    Socket createDataSocketPASV() throws IOException, FTPException {
	String reply = sendCommand("PASV");
	validateReply(reply, "227");
	int bracket1 = reply.indexOf('(');
	int bracket2 = reply.indexOf(')');
	String ipData = reply.substring(bracket1 + 1, bracket2);
	int[] parts = new int[6];
	int len = ipData.length();
	int partCount = 0;
	StringBuffer buf = new StringBuffer();
	for (int i = 0; i < len && partCount <= 6; i++) {
	    char ch = ipData.charAt(i);
	    if (Character.isDigit(ch))
		buf.append(ch);
	    else if (ch != ',')
		throw new FTPException("Malformed PASV reply: " + reply);
	    if (ch == ',' || i + 1 == len) {
		try {
		    parts[partCount++] = Integer.parseInt(buf.toString());
		    buf.setLength(0);
		} catch (NumberFormatException ex) {
		    throw new FTPException("Malformed PASV reply: " + reply);
		}
	    }
	}
	String ipAddress = (String.valueOf(parts[0]) + "." + parts[1] + "."
			    + parts[2] + "." + parts[3]);
	int port = (parts[4] << 8) + parts[5];
	return new Socket(ipAddress, port);
    }
    
    String sendCommand(String command) throws IOException {
	if (debugResponses)
	    System.out.println("---> " + command);
	writer.write(command + "\r\n");
	writer.flush();
	return readReply();
    }
    
    String readReply() throws IOException {
	StringBuffer reply = new StringBuffer(reader.readLine());
	if (debugResponses)
	    System.out.println(reply.toString());
	String replyCode = reply.toString().substring(0, 3);
	if (reply.charAt(3) == '-') {
	    boolean complete = false;
	    while (!complete) {
		String line = reader.readLine();
		if (debugResponses)
		    System.out.println(line);
		if (line.length() > 3 && line.substring(0, 3).equals(replyCode)
		    && line.charAt(3) == ' ') {
		    reply.append(line.substring(3));
		    complete = true;
		} else {
		    reply.append(" ");
		    reply.append(line);
		}
	    }
	}
	return reply.toString();
    }
    
    void validateReply(String reply, String expectedReplyCode)
	throws IOException, FTPException {
	String replyCode = reply.substring(0, 3);
	if (!replyCode.equals(expectedReplyCode))
	    throw new FTPException(reply.substring(4), replyCode);
    }
    
    void validateReply(String reply, String[] expectedReplyCodes)
	throws IOException, FTPException {
	String replyCode = reply.substring(0, 3);
	for (int i = 0; i < expectedReplyCodes.length; i++) {
	    if (replyCode.equals(expectedReplyCodes[i]))
		return;
	}
	throw new FTPException(reply.substring(4), replyCode);
    }
    
    void debugResponses(boolean on) {
	debugResponses = on;
    }
}
