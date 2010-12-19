/* Chunk_iCCP - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;

final class Chunk_iCCP extends KeyValueChunk
{
    Chunk_iCCP() {
	super(1766015824);
    }
    
    protected boolean isCompressed() {
	return true;
    }
    
    protected boolean multipleOK() {
	return false;
    }
    
    protected boolean beforeIDAT() {
	return true;
    }
    
    protected String getEncoding() {
	return "8859_1";
    }
    
    protected void readData() throws IOException {
	if (img.data.palette != null)
	    throw new PngException("iCCP chunk must precede PLTE chunk");
	super.readData();
	img.data.properties.put("icc profile name", key);
	img.data.properties.put("icc profile", value);
    }
}
