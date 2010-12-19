/* FrameHeader_3_0_NoCompression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.tffenterprises.music.tag.TagDataFormatException;
import com.tffenterprises.music.tag.id3v2.PaddingException;

public class FrameHeader_3_0_NoCompression extends FrameHeader_3_0
    implements Serializable, Cloneable
{
    public FrameHeader_3_0_NoCompression() {
	/* empty */
    }
    
    public FrameHeader_3_0_NoCompression(InputStream in)
	throws IOException, PaddingException, TagDataFormatException {
	super(in);
    }
    
    protected OutputStream processOutput(OutputStream out) throws IOException {
	if (usesCompression())
	    unsetFormatMask((byte) -128);
	return super.processOutput(out);
    }
}
