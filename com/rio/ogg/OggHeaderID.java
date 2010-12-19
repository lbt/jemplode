/* OggHeaderID - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.ogg;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.util.ReflectionUtils;

public class OggHeaderID
{
    private static final String VORBIS = "vorbis";
    private int myType;
    
    public OggHeaderID() {
	/* empty */
    }
    
    public OggHeaderID(int _type) {
	myType = _type;
    }
    
    public int getType() {
	return myType;
    }
    
    public void read(LittleEndianInputStream _is) throws IOException {
	myType = _is.readUnsigned8();
	byte[] vorbis = new byte[6];
	_is.read(vorbis);
	String vorbisStr = new String(vorbis);
	if (!vorbisStr.equals("vorbis"))
	    throw new IOException
		      ("Invalid header.  Expected 'vorbis' but got '"
		       + vorbisStr + "' instead.");
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
