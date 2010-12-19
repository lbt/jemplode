/* WriteRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;
import java.io.InputStream;

import com.inzyme.io.PaddedOutputStream;
import com.inzyme.io.StreamUtils;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;
import com.inzyme.typeconv.UINT64;

public class WriteRequestPacket extends AbstractRequestPacket
{
    private UINT64 myOffset;
    private UINT64 mySize;
    private UINT32 myFID;
    private UINT32 myStorage;
    private InputStream myIS;
    private long myTotalSize;
    private ISimpleProgressListener myListener;
    
    public WriteRequestPacket
	(UINT64 _offset, UINT64 _size, UINT32 _fid, UINT32 _storage,
	 InputStream _is, long _totalSize, ISimpleProgressListener _listener) {
	super(new PacketHeader(12));
	myOffset = _offset;
	mySize = _size;
	myFID = _fid;
	myStorage = _storage;
	myIS = _is;
	myTotalSize = _totalSize;
	myListener = _listener;
    }
    
    public UINT64 getOffset() {
	return myOffset;
    }
    
    public UINT64 getSize() {
	return mySize;
    }
    
    public UINT32 getFID() {
	return myFID;
    }
    
    public UINT32 getStorage() {
	return myStorage;
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	myOffset.write(_os);
	mySize.write(_os);
	myFID.write(_os);
	myStorage.write(_os);
	PaddedOutputStream pos = new PaddedOutputStream(_os, 4);
	StreamUtils.copy(myIS, pos, 16384, mySize.getValue(),
			 myOffset.getValue(), myTotalSize, myListener);
	pos.pad();
    }
}
