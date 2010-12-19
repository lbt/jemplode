/* FlacMetadataBlockHeader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.flac;
import java.io.IOException;
import java.io.InputStream;

import com.inzyme.typeconv.TypeConversionUtils;
import com.inzyme.util.ReflectionUtils;

public class FlacMetadataBlockHeader
{
    private boolean myLastMetadataBlock;
    private int myBlockType;
    private int myLength;
    
    public int getBlockType() {
	return myBlockType;
    }
    
    public boolean isLastMetadataBlock() {
	return myLastMetadataBlock;
    }
    
    public int getLength() {
	return myLength;
    }
    
    public void read(InputStream _is) throws IOException {
	int value = TypeConversionUtils.toUnsigned8(_is.read());
	myLastMetadataBlock = value >> 7 == 1;
	myBlockType = value & 0x7f;
	myLength = (TypeConversionUtils.toUnsigned8(_is.read()) << 16
		    | TypeConversionUtils.toUnsigned8(_is.read()) << 8
		    | TypeConversionUtils.toUnsigned8(_is.read()));
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
