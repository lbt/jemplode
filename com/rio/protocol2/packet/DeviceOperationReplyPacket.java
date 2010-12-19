/* DeviceOperationReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.inzyme.io.PaddedInputStream;
import com.inzyme.io.StreamUtils;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.UINT64;

import org.jempeg.protocol.ProtocolException;

public class DeviceOperationReplyPacket extends AbstractStatusReplyPacket
{
    private UINT64 mySize = new UINT64();
    private PaddedInputStream myPaddedInputStream;
    
    public DeviceOperationReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public UINT64 getSize() {
	return mySize;
    }
    
    public void readInto
	(OutputStream _os, long _totalOffset, long _totalLength,
	 ISimpleProgressListener _progressListener)
	throws IOException {
	if (myPaddedInputStream != null) {
	    StreamUtils.copy(myPaddedInputStream, _os, 16384,
			     mySize.getValue(), _totalOffset, _totalLength,
			     _progressListener);
	    myPaddedInputStream.pad();
	}
    }
    
    public void consume() throws IOException {
	readInto(new ByteArrayOutputStream(), 0L, 0L, null);
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException, ProtocolException {
	mySize.read(_is);
	super.readPayload(_is);
	checkStatus();
	myPaddedInputStream = new PaddedInputStream(_is, 4);
    }
}
