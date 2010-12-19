/* MetaFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.Serializable;

import com.tffenterprises.io.ByteArrayInputStream;

public abstract class MetaFrame extends GenericFrame
    implements Serializable, Cloneable
{
    public MetaFrame() {
	/* empty */
    }
    
    public MetaFrame(FrameHeader header) {
	super(header);
    }
    
    public boolean isOfRepeatableType() {
	return false;
    }
    
    public ByteArrayInputStream getByteStream()
	throws FrameDataFormatException {
	return new ByteArrayInputStream(getBytes());
    }
}
