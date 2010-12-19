/* ZCDMFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.InflaterInputStream;

import com.tffenterprises.io.ByteArrayInputStream;

public class ZCDMFrame extends MetaFrame implements Serializable, Cloneable
{
    private static final String MY_ID = "ZCDM";
    
    public ZCDMFrame() {
	super.getHeader().setFrameID("ZCDM");
    }
    
    public ZCDMFrame(FrameHeader header) {
	super(header);
	header.setFrameID("ZCDM");
    }
    
    public ByteArrayInputStream getByteStream()
	throws FrameDataFormatException {
	ByteArrayInputStream in = super.getByteStream();
	if (in.read() != 122)
	    throw new FrameDataFormatException
		      ("ZCDMFrame.getDataStream() is unable to decompress data using any other algorithm than `inflate`");
	try {
	    byte[] inflatedBytes = new byte[in.readInt()];
	    int inflated = new InflaterInputStream(in).read(inflatedBytes);
	    return new ByteArrayInputStream(inflatedBytes, 0, inflated);
	} catch (IOException e) {
	    throw new FrameDataFormatException
		      ("ZCDMFrame.getDataStream() experienced difficulties when decompressing the frame data: "
		       + e);
	}
    }
}
