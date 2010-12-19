/* ReadReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;
import java.io.OutputStream;

import com.inzyme.io.PaddedInputStream;
import com.inzyme.io.StreamUtils;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.STATUS;
import com.inzyme.typeconv.UINT64;

import org.jempeg.protocol.ProtocolException;

public class ReadReplyPacket extends AbstractStatusReplyPacket
{
    private UINT64 mySize = new UINT64();
    private PaddedInputStream myPaddedInputStream;
    private STATUS myStatus2;
    
    public ReadReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public UINT64 getSize() {
	return mySize;
    }
    
    public void checkStatus2() throws ProtocolException {
	if (!myStatus2.isSucceeded()) {
	    System.out.println("ReadReplyPacket.checkStatus2: packet failed: "
			       + this);
	    throw new StatusFailedException(myStatus2);
	}
    }
    
    public void readInto
	(OutputStream _os, long _offset, long _totalLength,
	 ISimpleProgressListener _progressListener)
	throws IOException {
	if (myPaddedInputStream != null) {
	    StreamUtils.copy(myPaddedInputStream, _os, 16384,
			     mySize.getValue(), _offset, _totalLength,
			     _progressListener);
	    myPaddedInputStream.pad();
	    myStatus2 = new STATUS();
	    myStatus2.read((LittleEndianInputStream)
			   myPaddedInputStream.getProxiedInputStream());
	}
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException, ProtocolException {
	mySize.read(_is);
	super.readPayload(_is);
	checkStatus();
	myPaddedInputStream = new PaddedInputStream(_is, 4);
    }
}
