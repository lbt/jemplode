/* ApplicationMetadataBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.flac;
import java.io.IOException;
import java.io.InputStream;

import com.inzyme.typeconv.TypeConversionUtils;
import com.inzyme.util.ReflectionUtils;

public class ApplicationMetadataBlock implements IFlacMetadataBlock
{
    private FlacMetadataBlockHeader myHeader;
    private int myApplicationID;
    private byte[] myApplicationData;
    
    public ApplicationMetadataBlock(FlacMetadataBlockHeader _header) {
	myHeader = _header;
    }
    
    public FlacMetadataBlockHeader getHeader() {
	return myHeader;
    }
    
    public int getApplicationID() {
	return myApplicationID;
    }
    
    public byte[] getApplicationData() {
	return myApplicationData;
    }
    
    public void read(InputStream _is) throws IOException {
	myApplicationID = (TypeConversionUtils.toUnsigned8(_is.read()) << 24
			   | TypeConversionUtils.toUnsigned8(_is.read()) << 16
			   | TypeConversionUtils.toUnsigned8(_is.read()) << 8
			   | TypeConversionUtils.toUnsigned8(_is.read()));
	myApplicationData = new byte[myHeader.getLength() - 4];
	for (int pos = 0; pos < myApplicationData.length;
	     pos += _is.read(myApplicationData)) {
	    /* empty */
	}
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
