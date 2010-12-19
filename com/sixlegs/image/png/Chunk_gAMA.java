/* Chunk_gAMA - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;

final class Chunk_gAMA extends Chunk
{
    Chunk_gAMA() {
	super(1732332865);
    }
    
    protected boolean multipleOK() {
	return false;
    }
    
    protected boolean beforeIDAT() {
	return true;
    }
    
    protected void readData() throws IOException {
	if (img.data.palette != null)
	    throw new PngException("gAMA chunk must precede PLTE chunk");
	if (length != 4)
	    badLength(4);
	long gamma = in_data.readUnsignedInt();
	if (gamma == 0L)
	    throw new PngExceptionSoft("Meaningless zero gAMA chunk value");
	if (img.getChunk(1934772034) == null)
	    img.data.properties.put("gamma", new Long(gamma));
    }
}
