/* FTPDataSocket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.enterprisedt.net.ftp;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPDataSocket
{
    private ServerSocket activeSocket = null;
    private Socket passiveSocket = null;
    
    FTPDataSocket(ServerSocket s) {
	activeSocket = s;
    }
    
    FTPDataSocket(Socket s) {
	passiveSocket = s;
    }
    
    void setTimeout(int millis) throws IOException {
	if (passiveSocket != null)
	    passiveSocket.setSoTimeout(millis);
	else if (activeSocket != null)
	    activeSocket.setSoTimeout(millis);
    }
    
    OutputStream getOutputStream() throws IOException {
	if (passiveSocket != null)
	    return passiveSocket.getOutputStream();
	passiveSocket = activeSocket.accept();
	return passiveSocket.getOutputStream();
    }
    
    InputStream getInputStream() throws IOException {
	if (passiveSocket != null)
	    return passiveSocket.getInputStream();
	passiveSocket = activeSocket.accept();
	return passiveSocket.getInputStream();
    }
    
    void close() throws IOException {
	if (passiveSocket != null)
	    passiveSocket.close();
	if (activeSocket != null)
	    activeSocket.close();
    }
}
