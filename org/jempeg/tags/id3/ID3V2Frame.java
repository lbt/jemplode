/* ID3V2Frame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags.id3;
import java.io.IOException;

import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.INumericPrimitive;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT0;
import com.inzyme.typeconv.UINT16;
import com.inzyme.util.ReflectionUtils;

public class ID3V2Frame
{
    public static final int ID3V2_FRAME_ID_SIZE = 4;
    private boolean myFrameSizeUnsynchronized;
    private CharArray myFrameID;
    private long mySize;
    private int mySizeLength;
    private INumericPrimitive myFlags;
    
    public ID3V2Frame(String _frameID, int _sizeLength, long _size,
		      INumericPrimitive _flags) {
	myFrameID = new CharArray(_frameID, _frameID.length());
	mySizeLength = _sizeLength;
	mySize = _size;
	myFlags = _flags;
    }
    
    public ID3V2Frame(ID3V2Tag _tag) {
	int minorVersion = _tag.getMinorVersion().getValue();
	if (minorVersion <= 2) {
	    myFrameID = new CharArray(3);
	    mySizeLength = 3;
	    myFlags = new UINT0();
	} else {
	    myFrameID = new CharArray(4);
	    mySizeLength = 4;
	    myFlags = new UINT16();
	}
	myFrameSizeUnsynchronized = minorVersion > 3;
    }
    
    public boolean isLastTag() {
	if (myFrameID.getValue()[0] == 0)
	    return true;
	return false;
    }
    
    public int getLength() {
	return myFrameID.getLength() + mySizeLength + myFlags.getLength();
    }
    
    public CharArray getFrameID() {
	return myFrameID;
    }
    
    public int getSizeLength() {
	return mySizeLength;
    }
    
    public long getSize() {
	return mySize;
    }
    
    public INumericPrimitive getFlags() {
	return myFlags;
    }
    
    public void read(LittleEndianInputStream _is) throws IOException {
	myFrameID.read(_is);
	if (!isLastTag()) {
	    byte[] sizeBuf = new byte[mySizeLength];
	    _is.read(sizeBuf);
	    if (myFrameSizeUnsynchronized) {
		for (int i = 0; i < mySizeLength; i++) {
		    if (sizeBuf[i] < 0)
			throw new IOException("Bogus tag, aborting.");
		}
		if (mySizeLength == 4)
		    mySize = ID3V2Tag.ss32_to_cpu(sizeBuf);
		else
		    mySize = (long) ID3V2Tag.ss24_to_cpu(sizeBuf);
	    } else if (mySizeLength == 4)
		mySize = ID3V2Tag.p32_to_cpu(sizeBuf);
	    else
		mySize = (long) ID3V2Tag.p24_to_cpu(sizeBuf);
	    myFlags.read(_is);
	}
    }
    
    public void write(LittleEndianOutputStream _os) throws IOException {
	myFrameID.write(_os);
	myFlags.write(_os);
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
