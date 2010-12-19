/* PaddingMetadataBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.flac;
import java.io.IOException;
import java.io.InputStream;

import com.inzyme.util.ReflectionUtils;

public class PaddingMetadataBlock implements IFlacMetadataBlock
{
    private FlacMetadataBlockHeader myHeader;
    
    public PaddingMetadataBlock(FlacMetadataBlockHeader _header) {
	myHeader = _header;
    }
    
    public FlacMetadataBlockHeader getHeader() {
	return myHeader;
    }
    
    public void read(InputStream _is) throws IOException {
	_is.read(new byte[myHeader.getLength()]);
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
