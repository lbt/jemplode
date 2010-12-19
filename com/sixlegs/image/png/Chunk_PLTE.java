/* Chunk_PLTE - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.awt.image.IndexColorModel;
import java.io.IOException;

final class Chunk_PLTE extends Chunk
{
    private int size;
    int[] r_raw;
    int[] g_raw;
    int[] b_raw;
    int[] a_raw;
    byte[] r;
    byte[] g;
    byte[] b;
    byte[] a;
    
    Chunk_PLTE() {
	super(1347179589);
    }
    
    protected boolean multipleOK() {
	return false;
    }
    
    protected boolean beforeIDAT() {
	return true;
    }
    
    protected void readData() throws IOException {
	img.data.palette = this;
	if (img.getChunk(1649100612) != null)
	    throw new PngException("bKGD chunk must follow PLTE chunk");
	if (!img.data.header.colorUsed)
	    throw new PngExceptionSoft("PLTE chunk found in grayscale image");
	if (length % 3 != 0)
	    throw new PngException("PLTE chunk length indivisible by 3");
	size = length / 3;
	if (img.data.header.colorType == 3) {
	    if (size > 2 << img.data.header.depth)
		throw new PngException("Too many palette entries");
	    if (size > 256)
		throw new PngExceptionSoft("Too many palette entries");
	}
	r = new byte[size];
	g = new byte[size];
	b = new byte[size];
	int[][] raw = new int[3][size];
	r_raw = raw[0];
	g_raw = raw[1];
	b_raw = raw[2];
	for (int i = 0; i < size; i++) {
	    r_raw[i] = in_data.readUnsignedByte();
	    g_raw[i] = in_data.readUnsignedByte();
	    b_raw[i] = in_data.readUnsignedByte();
	}
	updateProperties(false);
    }
    
    void updateProperties(boolean alpha) {
	int[][] prop = new int[alpha ? 4 : 3][size];
	System.arraycopy(r_raw, 0, prop[0], 0, size);
	System.arraycopy(g_raw, 0, prop[1], 0, size);
	System.arraycopy(b_raw, 0, prop[2], 0, size);
	if (alpha)
	    System.arraycopy(a_raw, 0, prop[3], 0, size);
	img.data.properties.put("palette", prop);
	img.data.properties.put("palette size", new Integer(size));
    }
    
    void calculate() {
	for (int i = 0; i < size; i++) {
	    r[i] = (byte) img.data.gammaTable[r_raw[i]];
	    g[i] = (byte) img.data.gammaTable[g_raw[i]];
	    b[i] = (byte) img.data.gammaTable[b_raw[i]];
	}
	if (img.data.header.paletteUsed) {
	    if (a != null)
		img.data.header.model
		    = new IndexColorModel(img.data.header.cmBits, size, r, g,
					  b, a);
	    else
		img.data.header.model
		    = new IndexColorModel(img.data.header.cmBits, size, r, g,
					  b);
	}
    }
}
