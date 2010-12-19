/* DeviceOperationRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.inzyme.io.PaddedOutputStream;
import com.inzyme.io.StreamUtils;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT64;

public class DeviceOperationRequestPacket extends AbstractRequestPacket
{
    private UINT64 mySize;
    private InputStream myIS;
    private long myTotalOffset;
    private long myTotalSize;
    private ISimpleProgressListener myListener;
    
    public DeviceOperationRequestPacket(byte[] _data,
					ISimpleProgressListener _listener) {
	this(new UINT64((long) _data.length), new ByteArrayInputStream(_data),
	     0L, (long) _data.length, _listener);
    }
    
    public DeviceOperationRequestPacket(UINT64 _size, InputStream _is,
					long _totalOffset, long _totalSize,
					ISimpleProgressListener _listener) {
	super(new PacketHeader(20));
	mySize = _size;
	myIS = _is;
	myTotalOffset = _totalOffset;
	myTotalSize = _totalSize;
	myListener = _listener;
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	mySize.write(_os);
	PaddedOutputStream pos = new PaddedOutputStream(_os, 4);
	StreamUtils.copy(myIS, pos, 16384, mySize.getValue(), myTotalOffset,
			 myTotalSize, myListener);
	pos.pad();
    }
}
