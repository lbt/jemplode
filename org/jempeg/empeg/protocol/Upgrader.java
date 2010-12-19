/* Upgrader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import javax.comm.CommPortIdentifier;

import com.inzyme.io.FileSeekableInputStream;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.progress.SilentProgressListener;
import com.inzyme.typeconv.CRC32;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.LittleEndianUtils;
import com.inzyme.util.Debug;

import org.jempeg.protocol.ConnectionException;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.IConnectionFactory;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.discovery.IDevice;
import org.jempeg.protocol.discovery.IDiscoverer;
import org.jempeg.protocol.discovery.IDiscoveryListener;

public class Upgrader
{
    private IConnection myConnection;
    private SeekableInputStream myUploadData;
    private byte[] myBuf32 = new byte[4];
    private byte[] myBuffer = new byte[16384];
    private long mySection;
    private UpgradeListenerIfc myListener;
    private int myProduct;
    private String myInfo;
    private String myWhat;
    private String myRelease;
    private String myVersion;
    
    public static class SerialDiscovererListener implements IDiscoveryListener
    {
	private Upgrader myUpgrader;
	
	public SerialDiscovererListener(Upgrader _upgrader) {
	    myUpgrader = _upgrader;
	}
	
	public void deviceDiscovered(IDiscoverer _discoverer,
				     IDevice _device) {
	    try {
		IConnectionFactory connFactory
		    = _device.getConnectionFactory();
		myUpgrader.doUpgrade(connFactory.createConnection());
	    } catch (Exception e) {
		Debug.println(e);
	    }
	}
    }
    
    public void close() throws IOException, ConnectionException {
	dumpText();
	if (myUploadData != null) {
	    myUploadData.close();
	    myUploadData = null;
	}
	closeConnection();
    }
    
    public void checkUpgrade(SeekableInputStream _upgradeData)
	throws IOException, ConnectionException {
	myUploadData = _upgradeData;
	long fileLength = myUploadData.length();
	long claimedLength = readUL();
	if (fileLength < claimedLength + 8L) {
	    error(2);
	    throw new IOException("Short file (length = " + fileLength
				  + ", claimed length = " + claimedLength
				  + ")");
	}
	status(0, 0, (int) claimedLength);
	CRC32 fileCRC = new CRC32();
	long chunk = 0L;
	long i = 0L;
	while (i < claimedLength) {
	    chunk = claimedLength - i > 16384L ? 16384L : claimedLength - i;
	    myUploadData.readFully(myBuffer, 0, (int) chunk);
	    fileCRC.update(myBuffer, 0, (int) chunk);
	    i += chunk;
	    status(0, (int) i, (int) claimedLength);
	}
	long theirCRC = readUL();
	if (fileCRC.getValue() != theirCRC) {
	    error(3);
	    throw new IOException("Mismatched CRC (" + fileCRC.getValue()
				  + " != " + theirCRC);
	}
	dumpText();
	long currentPos = 4L;
	long upgraderVersion = -1L;
	long blockLength;
	for (/**/; currentPos < claimedLength && currentPos != 0L;
	     currentPos += 8L + blockLength) {
	    myUploadData.seek(currentPos);
	    long blockType = readUL();
	    blockLength = readUL();
	    mySection = blockType;
	    if (blockType == 255L)
		upgraderVersion = readUL();
	    else if (blockType == 0L)
		myInfo = readString(blockLength);
	    else if (blockType == 1L)
		myWhat = readString(blockLength);
	    else if (blockType == 2L)
		myRelease = readString(blockLength);
	    else if (blockType == 3L)
		myVersion = readString(blockLength);
	}
	if (upgraderVersion > 2L) {
	    error(15);
	    throw new IOException("Old upgrader");
	}
	if (myListener != null)
	    myListener.textLoaded(myInfo, myWhat, myRelease, myVersion);
	status(1, 1, 1);
    }
    
    public void upload
	(IConnection _connection, SeekableInputStream _uploadData,
	 int _address)
	throws IOException, ConnectionException, ProtocolException {
	try {
	    myUploadData = _uploadData;
	    myUploadData.seek(0L);
	    int length = (int) _uploadData.length();
	    initializeConnection(_connection);
	    startFlash();
	    flashArea(_address, length);
	} catch (Object object) {
	    closeConnection();
	    throw object;
	}
	closeConnection();
    }
    
    public void doUpgrade(IConnection _connection)
	throws IOException, ConnectionException, ProtocolException {
	try {
	    mySection = 0L;
	    myUploadData.seek(0L);
	    long claimedLength = readUL();
	    initializeConnection(_connection);
	    startFlash();
	    int hwRev = getHwRev();
	    long currentPos = 4L;
	    boolean firstFlash = false;
	    boolean firstPump = true;
	    LittleEndianOutputStream eos = myConnection.getOutputStream();
	    while (currentPos < claimedLength && currentPos != 0L) {
		myUploadData.seek(currentPos);
		long blockType = readUL();
		long blockLength = readUL();
		mySection = blockType;
		if (blockType != 255L && blockType != 0L && blockType != 1L
		    && blockType != 2L) {
		    if (blockType == 4L) {
			boolean ok = false;
			String text = readString(blockLength);
			StringTokenizer tokenizer
			    = new StringTokenizer(text, "\n");
			while (!ok && tokenizer.hasMoreTokens()) {
			    String token = tokenizer.nextToken();
			    int thisRev = Integer.parseInt(token, 16);
			    if (thisRev == hwRev)
				ok = true;
			}
			if (!ok) {
			    error(16);
			    currentPos = 0L;
			}
		    } else if (blockType == 16L || blockType == 17L
			       || blockType == 18L || blockType == 19L) {
			long start = readUL();
			if (firstFlash) {
			    firstFlash = false;
			    startFlash();
			}
			flashArea((int) start, (int) blockLength - 4);
		    } else if (blockType == 32L || blockType == 48L
			       || blockType == 64L) {
			if (firstPump) {
			    firstPump = false;
			    if (!firstFlash)
				eos.write(114);
			    startPump();
			}
			String partition
			    = (blockType == 32L ? "/dev/hda" : blockType == 48L
			       ? "/dev/hdb" : "/dev/hdc");
			partition(partition);
		    } else if (blockType == 33L || blockType == 34L
			       || blockType == 35L || blockType == 36L
			       || blockType == 37L || blockType == 38L
			       || blockType == 39L || blockType == 40L
			       || blockType == 49L || blockType == 50L
			       || blockType == 51L || blockType == 52L
			       || blockType == 53L || blockType == 54L
			       || blockType == 55L || blockType == 56L
			       || blockType == 65L || blockType == 66L
			       || blockType == 67L || blockType == 68L
			       || blockType == 69L || blockType == 70L
			       || blockType == 71L || blockType == 72L) {
			if (firstPump) {
			    firstPump = false;
			    if (!firstFlash)
				eos.write(114);
			    startPump();
			}
			String partition;
			if (blockType >= 33L && blockType <= 40L)
			    partition = "/dev/hda" + (blockType - 32L);
			else if (blockType >= 49L && blockType <= 56L)
			    partition = "/dev/hdb" + (blockType - 48L);
			else
			    partition = "/dev/hdc" + (blockType - 64L);
			pumpPartition(partition, (int) blockLength);
		    }
		}
		if (currentPos > 0L)
		    currentPos += 8L + blockLength;
	    }
	    if (!firstPump)
		eos.write("reboot\r", "ISO-8859-1");
	    else if (!firstFlash)
		eos.write(114);
	    if (currentPos <= 0L) {
		status(13);
		throw new IOException("Something bad happened");
	    }
	    status(12);
	} catch (Object object) {
	    closeConnection();
	    throw object;
	}
	closeConnection();
    }
    
    public void setListener(UpgradeListenerIfc _listener) {
	myListener = _listener;
    }
    
    private void initializeConnection(IConnection _connection)
	throws IOException, ConnectionException, ProtocolException {
	org.jempeg.protocol.IProtocolClient protocolClient
	    = new EmpegProtocolClient(_connection,
				      new SilentProgressListener());
	myConnection = protocolClient.getConnection();
	if (protocolClient.isDeviceConnected()
	    || protocolClient.isDeviceConnected())
	    ((EmpegProtocolClient) protocolClient).restartUnit(false, false);
	myConnection.open();
    }
    
    private void closeConnection() throws IOException, ConnectionException {
	if (myConnection != null) {
	    myConnection.close();
	    myConnection = null;
	}
    }
    
    private int getHwRev() throws IOException, ConnectionException {
	LittleEndianInputStream eis = myConnection.getInputStream();
	LittleEndianOutputStream eos = myConnection.getOutputStream();
	eos.write(105);
	myConnection.setTimeout(1000L);
	char ch = (char) eis.read();
	int hwRev;
	if (ch == 'i') {
	    myConnection.setTimeout(500L);
	    if (eis.read() != 13)
		throw new IOException("Expected '\\r'");
	    if (eis.read() != 10)
		throw new IOException("Expected '\\n'");
	    hwRev = readHex(500, 2);
	} else
	    hwRev = 1;
	return hwRev;
    }
    
    private long readUL() throws IOException {
	myUploadData.readFully(myBuf32);
	long b = LittleEndianUtils.toUnsigned32(myBuf32[0], myBuf32[1],
						myBuf32[2], myBuf32[3]);
	return b;
    }
    
    private String readString(long _length) throws IOException {
	byte[] bytes = new byte[(int) _length];
	myUploadData.readFully(bytes);
	String str;
	try {
	    str = new String(bytes, "ISO-8859-1");
	} catch (UnsupportedEncodingException e) {
	    Debug.println(e);
	    str = new String(bytes);
	}
	return str;
    }
    
    private boolean waitFor(String _string, int _timeout)
	throws IOException, ConnectionException {
	long to = System.currentTimeMillis() + (long) (_timeout * 1000);
	myConnection.setTimeout(1000L);
	LittleEndianInputStream eis = myConnection.getInputStream();
	int i = 0;
	int length = _string.length();
	char c0 = _string.charAt(0);
	while (i < length && System.currentTimeMillis() < to) {
	    char ch = (char) eis.read();
	    if (ch == _string.charAt(i))
		i++;
	    else
		i = ch == c0 ? 1 : 0;
	}
	boolean matches = i == length;
	return matches;
    }
    
    private int readHex(int _timeout, int _digits)
	throws IOException, ConnectionException {
	byte[] buf = new byte[_digits];
	myConnection.setTimeout((long) (_timeout * 1000));
	LittleEndianInputStream eis = myConnection.getInputStream();
	eis.read(buf);
	String hexStr;
	try {
	    hexStr = new String(buf, "ISO-8859-1");
	} catch (UnsupportedEncodingException e) {
	    Debug.println(e);
	    hexStr = new String(buf);
	}
	int hex = Integer.parseInt(hexStr, 16);
	return hex;
    }
    
    private void commandAddress(char _command, int _address)
	throws IOException, ConnectionException {
	flushRx();
	LittleEndianInputStream eis = myConnection.getInputStream();
	LittleEndianOutputStream eos = myConnection.getOutputStream();
	eos.write(_command);
	myConnection.setTimeout(1000L);
	char ch = (char) eis.read();
	if (ch != _command)
	    throw new IOException("Command mismatch (" + ch + " != " + _command
				  + ")");
	eos.write(_address >> 0 & 0xff);
	eos.write(_address >> 8 & 0xff);
	eos.write(_address >> 16 & 0xff);
	eos.write(_address >> 24 & 0xff);
	int a = readHex(1, 8);
	if (a != _address)
	    throw new IOException("Address mismatch (" + a + " != " + _address
				  + ")");
	ch = (char) eis.read();
	if (ch != '-')
	    throw new IOException("Expected hyphen was not found (found '" + ch
				  + "' instead).");
	int res = readHex(2, 2);
	if (res != 128)
	    throw new IOException("Chip-specific error.  (" + res
				  + " != 0x80)");
    }
    
    private void erasePage(int _address)
	throws IOException, ConnectionException {
	commandAddress('e', _address);
    }
    
    private void lockPage(int _address)
	throws IOException, ConnectionException {
	commandAddress('l', _address);
    }
    
    private void unlockPage(int _address)
	throws IOException, ConnectionException {
	commandAddress('u', _address);
    }
    
    private void programBlock(int _address, int _length)
	throws IOException, ConnectionException {
	flushRx();
	myConnection.setTimeout(1000L);
	LittleEndianInputStream eis = myConnection.getInputStream();
	LittleEndianOutputStream eos = myConnection.getOutputStream();
	eos.write(112);
	char ch = (char) eis.read();
	if (ch != 'p')
	    throw new IOException("Expected 'p'");
	eos.write(_address >> 0 & 0xff);
	eos.write(_address >> 8 & 0xff);
	eos.write(_address >> 16 & 0xff);
	eos.write(_address >> 24 & 0xff);
	int reportedAddress = readHex(1, 8);
	if (reportedAddress != _address)
	    throw new IOException("Address " + _address
				  + " != Reported Address " + reportedAddress);
	ch = (char) eis.read();
	if (ch != '-')
	    throw new IOException("Expected hyphen");
	eos.write(_length >> 0 & 0xff);
	eos.write(_length >> 8 & 0xff);
	int reportedLength = readHex(1, 4);
	if (reportedLength != _length)
	    throw new IOException("Length " + _length + " != Reported Length "
				  + reportedLength);
	ch = (char) eis.read();
	if (ch != '-')
	    throw new IOException("Expected hyphen");
	int checksum = 0;
	for (int i = 0; i < _length; i++) {
	    checksum += myBuffer[i];
	    eos.write(myBuffer[i]);
	}
	eos.write(checksum & 0xff);
	ch = (char) eis.read();
	if (ch != 'o')
	    throw new IOException("Expected 'o'");
	ch = (char) eis.read();
	if (ch != 'k')
	    throw new IOException("Expected 'k'");
	ch = (char) eis.read();
	if (ch != '\r')
	    throw new IOException("Expected '\\r'");
	ch = (char) eis.read();
	if (ch != '\n')
	    throw new IOException("Expected '\\n'");
	ch = (char) eis.read();
	if (ch != 'o')
	    throw new IOException("Expected 'o'");
	ch = (char) eis.read();
	if (ch != 'k')
	    throw new IOException("Expected 'k'");
    }
    
    private void flushRx() throws IOException, ConnectionException {
	myConnection.flushReceiveBuffer();
    }
    
    private void startFlash() throws IOException, ConnectionException {
	status(2);
	myConnection.setTimeout(1000L);
	LittleEndianInputStream eis = myConnection.getInputStream();
	LittleEndianOutputStream eos = myConnection.getOutputStream();
	String magic = "Flash";
	int i = 0;
	Timeout giveup = new Timeout(180000);
	Timeout statusInterval = new Timeout(1000);
	Timeout sendInterval = new Timeout(100);
	giveup.reset();
	statusInterval.reset();
	do {
	    try {
		if (i >= magic.length())
		    break;
		if (statusInterval.isTimedOut()) {
		    status(2,
			   (int) (180000L - giveup.getRemainingMilliseconds()),
			   180000);
		    statusInterval.reset();
		}
		if (sendInterval.isTimedOut()) {
		    eos.write(1);
		    sendInterval.reset();
		}
		long remainingSend = sendInterval.getRemainingMilliseconds();
		long remainingGiveup = giveup.getRemainingMilliseconds();
		long waitTime = remainingSend;
		if (remainingGiveup < waitTime)
		    waitTime = remainingGiveup;
		if (waitTime < 0L)
		    waitTime = 0L;
		char c = (char) eis.read();
		if (c >= 0) {
		    if (c == magic.charAt(i))
			i++;
		    else
			i = 0;
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	} while (!giveup.isTimedOut());
	if (giveup.isTimedOut()) {
	    error(5);
	    throw new IOException("No unit found.");
	}
	myConnection.setTimeout(1000L);
	while (eis.read() != 35) {
	    /* empty */
	}
	readHex(1, 4);
	while (eis.read() != 35) {
	    /* empty */
	}
	myProduct = readHex(1, 4);
	while (eis.read() != 63) {
	    /* empty */
	}
	while (eis.read() != -1) {
	    /* empty */
	}
    }
    
    private void flashArea(int _address, int _length)
	throws IOException, ConnectionException {
	status(4, 0, _length);
	int a = _address;
	int d = 0;
	do {
	    if (myProduct == 35009)
		unlockPage(a);
	    erasePage(a);
	    if (a < 65536) {
		a += 8192;
		d += 8192;
	    } else {
		a += 65536;
		d += 65536;
	    }
	    status(4, d, _length);
	} while (d < _length);
	int done = 0;
	status(5, 0, _length);
	int p = _address;
	while (done < _length) {
	    int chunk = _length - done > 16384 ? 16384 : _length - done;
	    myUploadData.readFully(myBuffer, 0, chunk);
	    if ((chunk & 0x1) > 0)
		chunk++;
	    programBlock(p, chunk);
	    done += chunk;
	    p += chunk;
	    status(5, done, _length);
	}
	if (myProduct == 35009) {
	    a = _address;
	    d = 0;
	    do {
		lockPage(a);
		if (a < 65536) {
		    a += 8192;
		    d += 8192;
		} else {
		    a += 65536;
		    d += 65536;
		}
	    } while (d < _length);
	}
    }
    
    private void startPump() throws IOException, ConnectionException {
	status(7);
	int timeout;
	for (timeout = 0; timeout <= 60; timeout++) {
	    status(7, timeout, 60);
	    if (waitFor("Ctrl-A", 3))
		break;
	}
	if (timeout > 60) {
	    error(11);
	    throw new IOException("No pump");
	}
	LittleEndianOutputStream eos = myConnection.getOutputStream();
	eos.write(1);
    }
    
    private void pumpPartition(String _partitionName, int _length)
	throws IOException, ConnectionException {
	status(8);
	flushRx();
	LittleEndianInputStream eis = myConnection.getInputStream();
	LittleEndianOutputStream eos = myConnection.getOutputStream();
	eos.write(13);
	if (!waitFor("pump> ", 5)) {
	    error(11);
	    throw new IOException("No pump");
	}
	status(9, 0, 1);
	String buf = "device " + _partitionName;
	eos.write(buf, "ISO-8859-1");
	eos.write(13);
	if (!waitFor(buf, 5)) {
	    error(12);
	    throw new IOException("Bad Pump Select");
	}
	buf = "pump " + _length;
	eos.write(buf, "ISO-8859-1");
	eos.write(13);
	if (!waitFor(buf, 5)) {
	    error(13);
	    throw new IOException("Bad pump");
	}
	int chunk = _length < 4096 ? _length : 4096;
	myUploadData.readFully(myBuffer, 0, chunk);
	int offset = 0;
	int errors = 0;
	while (offset < _length && errors < 16) {
	    eos.write(1);
	    eos.write(myBuffer, 0, 4096);
	    CRC32 crcObj = new CRC32();
	    crcObj.update(myBuffer, 0, 4096);
	    long crc = crcObj.getValue();
	    flushRx();
	    eos.writeUnsigned32(crc);
	    int l = 0;
	    try {
		myConnection.setTimeout(20000L);
		do {
		    l = eis.read();
		    if (l < 0) {
			l = 21;
			throw new IOException("Received a NAK");
		    }
		    if (l == 6)
			break;
		} while (l != 21);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    if (l == 21)
		errors++;
	    else {
		offset += 4096;
		errors = 0;
		status(10, offset > _length ? _length : offset, _length);
		if (offset < _length) {
		    if (_length - offset < 4096)
			chunk = _length - offset;
		    else
			chunk = 4096;
		    myUploadData.readFully(myBuffer, 0, chunk);
		}
	    }
	}
	if (offset < _length) {
	    try {
		Thread.sleep(4000L);
	    } catch (Throwable throwable) {
		/* empty */
	    }
	    eos.write(24);
	    error(13);
	    throw new IOException("Bad pump");
	}
	status(11, 1, 1);
    }
    
    private void partition(String _partitionName)
	throws IOException, ConnectionException {
	status(8);
	flushRx();
	LittleEndianOutputStream eos = myConnection.getOutputStream();
	eos.write(13);
	if (!waitFor("pump> ", 5)) {
	    error(11);
	    throw new IOException("No pump");
	}
	status(9, 0, 1);
	String buf = "partition " + _partitionName;
	eos.write(buf, "ISO-8859-1");
	eos.write(13);
	if (!waitFor(buf, 5)) {
	    error(12);
	    throw new IOException("Bad Pump Select");
	}
    }
    
    private void status(int _state, int _current, int _max) {
	if (_current > _max)
	    _current = _max;
	if (myListener != null)
	    myListener.showStatus((int) mySection, _state, _current, _max);
    }
    
    private void status(int _state) {
	status(_state, 0, 0);
    }
    
    private void error(int _state) {
	if (myListener != null)
	    myListener.showError((int) mySection, _state);
    }
    
    private void dumpText() throws IOException {
	myInfo = null;
	myWhat = null;
	myRelease = null;
	myVersion = null;
    }
    
    public static void main(String[] _args) throws Throwable {
	FileSeekableInputStream fsis = null;
	try {
	    File upgradeFile = new File(_args[0]);
	    Upgrader u = new Upgrader();
	    u.setListener(new BasicUpgradeListener());
	    fsis = new FileSeekableInputStream(upgradeFile);
	    u.checkUpgrade(fsis);
	    CommPortIdentifier commPort
		= CommPortIdentifier.getPortIdentifier("COM1");
	    SerialConnection conn = new SerialConnection(commPort, 115200);
	    u.doUpgrade(conn);
	} catch (Object object) {
	    if (fsis != null)
		fsis.close();
	    throw object;
	}
	if (fsis != null)
	    fsis.close();
    }
}
