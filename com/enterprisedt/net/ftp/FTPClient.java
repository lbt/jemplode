/* FTPClient - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.enterprisedt.net.ftp;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Vector;

public class FTPClient
{
    private FTPControlSocket control = null;
    private FTPDataSocket data = null;
    private int timeout = 0;
    private FTPTransferType transferType = FTPTransferType.ASCII;
    private FTPConnectMode connectMode = FTPConnectMode.PASV;
    
    public FTPClient(String remoteHost) throws IOException, FTPException {
	control = new FTPControlSocket(remoteHost);
    }
    
    public FTPClient(String remoteHost, int controlPort)
	throws IOException, FTPException {
	control = new FTPControlSocket(remoteHost, controlPort);
    }
    
    public FTPClient(InetAddress remoteAddr) throws IOException, FTPException {
	control = new FTPControlSocket(remoteAddr);
    }
    
    public FTPClient(InetAddress remoteAddr, int controlPort)
	throws IOException, FTPException {
	control = new FTPControlSocket(remoteAddr, controlPort);
    }
    
    public void setTimeout(int millis) throws IOException {
	timeout = millis;
	control.setTimeout(millis);
    }
    
    public void setConnectMode(FTPConnectMode mode) {
	connectMode = mode;
    }
    
    public void login(String user, String password)
	throws IOException, FTPException {
	control.sendCommand("USER " + user);
	control.sendCommand("PASS " + password);
    }
    
    public void user(String user) throws IOException, FTPException {
	String reply = control.sendCommand("USER " + user);
	String[] validCodes = { "230", "331" };
	control.validateReply(reply, validCodes);
    }
    
    public void password(String password) throws IOException, FTPException {
	String reply = control.sendCommand("PASS " + password);
	String[] validCodes = { "230", "202" };
	control.validateReply(reply, validCodes);
    }
    
    public void initSOCKS(String port, String host) {
	Properties props = System.getProperties();
	props.put("socksProxyPort", port);
	props.put("socksProxyHost", host);
	System.setProperties(props);
    }
    
    String getRemoteHostName() {
	return control.getRemoteHostName();
    }
    
    public void quote(String command, String[] validCodes)
	throws IOException, FTPException {
	String reply = control.sendCommand(command);
	if (validCodes != null && validCodes.length > 0)
	    control.validateReply(reply, validCodes);
    }
    
    public void put(String localPath, String remoteFile)
	throws IOException, FTPException {
	put(localPath, remoteFile, false);
    }
    
    public void put(String localPath, String remoteFile, boolean append)
	throws IOException, FTPException {
	if (getType() == FTPTransferType.ASCII)
	    putASCII(new FileReader(localPath), remoteFile, append);
	else
	    putBinary(new FileInputStream(localPath), remoteFile, 0, append);
	String[] validCodes2 = { "226", "250" };
	String reply = control.readReply();
	control.validateReply(reply, validCodes2);
    }
    
    public void put(InputStream localInputStream, String remoteFile,
		    boolean append) throws IOException, FTPException {
	if (getType() == FTPTransferType.ASCII)
	    putASCII(new InputStreamReader(localInputStream), remoteFile,
		     append);
	else
	    putBinary(localInputStream, remoteFile, 0, append);
	String[] validCodes2 = { "226", "250" };
	String reply = control.readReply();
	control.validateReply(reply, validCodes2);
    }
    
    private void initPut(String remoteFile, int _offset, boolean append)
	throws IOException, FTPException {
	data = control.createDataSocket(connectMode);
	data.setTimeout(timeout);
	if (_offset > 0) {
	    String cmd = "REST " + _offset;
	    control.sendCommand(cmd);
	}
	String cmd = append ? "APPE " : "STOR ";
	String reply = control.sendCommand(cmd + remoteFile);
	String[] validCodes1 = { "125", "150" };
	control.validateReply(reply, validCodes1);
    }
    
    private void putASCII(Reader localReader, String remoteFile,
			  boolean append) throws IOException, FTPException {
	LineNumberReader in = new LineNumberReader(localReader);
	initPut(remoteFile, 0, append);
	BufferedWriter out
	    = (new BufferedWriter
	       (new OutputStreamWriter(data.getOutputStream())));
	String line = null;
	while ((line = in.readLine()) != null) {
	    out.write(line, 0, line.length());
	    out.write("\r\n", 0, "\r\n".length());
	}
	in.close();
	out.flush();
	out.close();
	try {
	    data.close();
	} catch (IOException ioexception) {
	    /* empty */
	}
    }
    
    public OutputStream putBinary(String _remoteFile, int _offset)
	throws IOException, FTPException {
	initPut(_remoteFile, _offset, false);
	return data.getOutputStream();
    }
    
    private void putBinary(InputStream _localInputStream, String remoteFile,
			   int _offset,
			   boolean append) throws IOException, FTPException {
	BufferedInputStream in = new BufferedInputStream(_localInputStream);
	initPut(remoteFile, _offset, append);
	BufferedOutputStream out
	    = (new BufferedOutputStream
	       (new DataOutputStream(data.getOutputStream())));
	byte[] buf = new byte[512];
	int count = 0;
	while ((count = in.read(buf)) > 0)
	    out.write(buf, 0, count);
	in.close();
	out.flush();
	out.close();
	try {
	    data.close();
	} catch (IOException ioexception) {
	    /* empty */
	}
    }
    
    public void put(byte[] bytes, String remoteFile)
	throws IOException, FTPException {
	put(bytes, remoteFile, false);
    }
    
    public void put(byte[] bytes, String remoteFile, boolean append)
	throws IOException, FTPException {
	initPut(remoteFile, 0, append);
	BufferedOutputStream out
	    = (new BufferedOutputStream
	       (new DataOutputStream(data.getOutputStream())));
	out.write(bytes, 0, bytes.length);
	out.flush();
	out.close();
	try {
	    data.close();
	} catch (IOException ioexception) {
	    /* empty */
	}
	String[] validCodes2 = { "226", "250" };
	String reply = control.readReply();
	control.validateReply(reply, validCodes2);
    }
    
    public void get(OutputStream localStream, String remoteFile)
	throws IOException, FTPException {
	if (getType() == FTPTransferType.ASCII)
	    getASCII(new OutputStreamWriter(localStream), remoteFile);
	else
	    getBinary(localStream, remoteFile);
	String[] validCodes2 = { "226", "250" };
	String reply = control.readReply();
	control.validateReply(reply, validCodes2);
    }
    
    private void initGet(String remoteFile) throws IOException, FTPException {
	data = control.createDataSocket(connectMode);
	data.setTimeout(timeout);
	String reply = control.sendCommand("RETR " + remoteFile);
	String[] validCodes1 = { "125", "150" };
	control.validateReply(reply, validCodes1);
    }
    
    private void getASCII(Writer localWriter, String remoteFile)
	throws IOException, FTPException {
	BufferedWriter out = new BufferedWriter(localWriter);
	initGet(remoteFile);
	LineNumberReader in = (new LineNumberReader
			       (new InputStreamReader(data.getInputStream())));
	String line = null;
	while ((line = in.readLine()) != null) {
	    out.write(line, 0, line.length());
	    out.newLine();
	}
	out.close();
	try {
	    in.close();
	    data.close();
	} catch (IOException ioexception) {
	    /* empty */
	}
    }
    
    public InputStream getBinary(String _remoteFile)
	throws IOException, FTPException {
	initGet(_remoteFile);
	InputStream is = data.getInputStream();
	return is;
    }
    
    public void readGetResponse() throws IOException, FTPException {
	String[] validCodes2 = { "226", "250", "200" };
	String reply = control.readReply();
	control.validateReply(reply, validCodes2);
    }
    
    private void getBinary(OutputStream localStream, String remoteFile)
	throws IOException, FTPException {
	BufferedOutputStream out = new BufferedOutputStream(localStream);
	initGet(remoteFile);
	BufferedInputStream in
	    = (new BufferedInputStream
	       (new DataInputStream(data.getInputStream())));
	int chunksize = 4096;
	byte[] chunk = new byte[chunksize];
	int count;
	while ((count = in.read(chunk, 0, chunksize)) >= 0)
	    out.write(chunk, 0, count);
	out.close();
	try {
	    in.close();
	    data.close();
	} catch (IOException ioexception) {
	    /* empty */
	}
    }
    
    public byte[] get(String remoteFile) throws IOException, FTPException {
	data = control.createDataSocket(connectMode);
	data.setTimeout(timeout);
	String reply = control.sendCommand("RETR " + remoteFile);
	String[] validCodes1 = { "125", "150" };
	control.validateReply(reply, validCodes1);
	BufferedInputStream in
	    = (new BufferedInputStream
	       (new DataInputStream(data.getInputStream())));
	int chunksize = 4096;
	byte[] chunk = new byte[chunksize];
	byte[] resultBuf = new byte[chunksize];
	byte[] temp = null;
	int bufsize = 0;
	int count;
	while ((count = in.read(chunk, 0, chunksize)) >= 0) {
	    temp = new byte[bufsize + count];
	    System.arraycopy(resultBuf, 0, temp, 0, bufsize);
	    System.arraycopy(chunk, 0, temp, bufsize, count);
	    resultBuf = temp;
	    bufsize += count;
	}
	try {
	    in.close();
	    data.close();
	} catch (IOException ioexception) {
	    /* empty */
	}
	String[] validCodes2 = { "226", "250" };
	reply = control.readReply();
	control.validateReply(reply, validCodes2);
	return resultBuf;
    }
    
    public boolean site(String command) throws IOException, FTPException {
	String reply = control.sendCommand("SITE " + command);
	String[] validCodes = { "200", "202", "502" };
	control.validateReply(reply, validCodes);
	if (reply.substring(0, 3).equals("200"))
	    return true;
	return false;
    }
    
    /**
     * @deprecated
     */
    public String list(String mask) throws IOException, FTPException {
	return list(mask, false);
    }
    
    /**
     * @deprecated
     */
    public String list(String mask, boolean full)
	throws IOException, FTPException {
	String[] list = dir(mask, full);
	StringBuffer result = new StringBuffer();
	String sep = System.getProperty("line.separator");
	for (int i = 0; i < list.length; i++) {
	    result.append(list[i]);
	    result.append(sep);
	}
	return result.toString();
    }
    
    public String[] dir(String mask) throws IOException, FTPException {
	return dir(mask, false);
    }
    
    public String[] dir(String mask, boolean full)
	throws IOException, FTPException {
	data = control.createDataSocket(connectMode);
	data.setTimeout(timeout);
	String command = full ? "LIST " : "NLST ";
	command += (String) mask;
	command = command.trim();
	String reply = control.sendCommand(command);
	String[] validCodes1 = { "125", "150" };
	control.validateReply(reply, validCodes1);
	LineNumberReader in = (new LineNumberReader
			       (new InputStreamReader(data.getInputStream())));
	Vector lines = new Vector();
	String line = null;
	while ((line = in.readLine()) != null)
	    lines.addElement(line);
	try {
	    in.close();
	    data.close();
	} catch (IOException ioexception) {
	    /* empty */
	}
	String[] validCodes2 = { "226", "250" };
	reply = control.readReply();
	control.validateReply(reply, validCodes2);
	String[] linesArray = new String[lines.size()];
	lines.copyInto(linesArray);
	return linesArray;
    }
    
    public void debugResponses(boolean on) {
	control.debugResponses(on);
    }
    
    public FTPTransferType getType() {
	return transferType;
    }
    
    public void setType(FTPTransferType type)
	throws IOException, FTPException {
	String typeStr = FTPTransferType.ASCII_CHAR;
	if (type.equals(FTPTransferType.BINARY))
	    typeStr = FTPTransferType.BINARY_CHAR;
	String reply = control.sendCommand("TYPE " + typeStr);
	control.validateReply(reply, "200");
	transferType = type;
    }
    
    public void delete(String remoteFile) throws IOException, FTPException {
	String reply = control.sendCommand("DELE " + remoteFile);
	control.validateReply(reply, "250");
    }
    
    public void rename(String from, String to)
	throws IOException, FTPException {
	String reply = control.sendCommand("RNFR " + from);
	control.validateReply(reply, "350");
	reply = control.sendCommand("RNTO " + to);
	control.validateReply(reply, "250");
    }
    
    public void rmdir(String dir) throws IOException, FTPException {
	String reply = control.sendCommand("RMD " + dir);
	control.validateReply(reply, "250");
    }
    
    public void mkdir(String dir) throws IOException, FTPException {
	String reply = control.sendCommand("MKD " + dir);
	control.validateReply(reply, "257");
    }
    
    public void chdir(String dir) throws IOException, FTPException {
	String reply = control.sendCommand("CWD " + dir);
	control.validateReply(reply, "250");
    }
    
    public String pwd() throws IOException, FTPException {
	String reply = control.sendCommand("PWD");
	control.validateReply(reply, "257");
	return reply.substring(4);
    }
    
    public String system() throws IOException, FTPException {
	String reply = control.sendCommand("SYST");
	control.validateReply(reply, "215");
	return reply.substring(4);
    }
    
    public void quit() throws IOException, FTPException {
	control.sendCommand("QUIT\r\n");
	control.logout();
	control = null;
    }
}
