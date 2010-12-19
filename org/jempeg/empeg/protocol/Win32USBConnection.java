/* Win32USBConnection - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import java.io.BufferedOutputStream;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.util.Debug;

import org.jempeg.protocol.ConnectionException;
import org.jempeg.protocol.IConnection;

public class Win32USBConnection implements IConnection
{
    public static boolean DLL_EXISTS = false;
    private String myDeviceName;
    private int myNativeConnection;
    private boolean myIsOpen;
    
    static {
	try {
	    System.loadLibrary("jEmplode");
	    DLL_EXISTS = true;
	} catch (UnsatisfiedLinkError e) {
	    Debug.println
		(8,
		 "You do not have jEmplode.dll installed somewhere in your PATH.  USB support will not work until you do.");
	}
    }
    
    public Win32USBConnection(String _deviceName) {
	myDeviceName = _deviceName;
	myNativeConnection = createNativeConnection();
    }
    
    public boolean isOpen() {
	return myIsOpen;
    }
    
    public LittleEndianInputStream getInputStream()
	throws ConnectionException {
	java.io.InputStream is = new Win32USBInputStream(myNativeConnection);
	LittleEndianInputStream eis = new LittleEndianInputStream(is);
	return eis;
    }
    
    public LittleEndianOutputStream getOutputStream()
	throws ConnectionException {
	java.io.OutputStream os
	    = (new BufferedOutputStream
	       (new Win32USBOutputStream(myNativeConnection)));
	LittleEndianOutputStream eos = new LittleEndianOutputStream(os);
	return eos;
    }
    
    public IConnection getFastConnection() throws ConnectionException {
	return null;
    }
    
    public int getPacketSize() {
	return 16384;
    }
    
    public void open() throws ConnectionException {
	close();
	open0();
	myIsOpen = true;
    }
    
    public void close() throws ConnectionException {
	close0();
	myIsOpen = false;
    }
    
    public void pause() throws ConnectionException {
	close();
    }
    
    public void unpause() throws ConnectionException {
	open();
    }
    
    public void flushReceiveBuffer() throws ConnectionException {
	flushReceiveBuffer0();
    }
    
    public void setTimeout(long _millis) throws ConnectionException {
	/* empty */
    }
    
    private native int createNativeConnection();
    
    private native void open0();
    
    private native void close0();
    
    private native void pause0();
    
    private native void unpause0();
    
    private native void flushReceiveBuffer0();
}
