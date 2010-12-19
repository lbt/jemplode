/* BitMover - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;
import java.io.InputStream;

abstract class BitMover
{
    int trans = -1;
    int transgray = -1;
    int translow = -1;
    int[] gammaTable;
    
    abstract int fill(int[] is, InputStream inputstream, int i, int i_0_)
	throws IOException;
    
    static BitMover getBitMover(PngImage img) throws PngException {
	StringBuffer clsname
	    = new StringBuffer("com.sixlegs.image.png.BitMover");
	clsname.append(img.data.header.depth);
	if (img.data.header.paletteUsed)
	    clsname.append('P');
	else
	    clsname.append(img.data.header.colorUsed ? "RGB" : "G");
	if (img.data.header.alphaUsed)
	    clsname.append('A');
	try {
	    BitMover b
		= (BitMover) Class.forName(clsname.toString()).newInstance();
	    b.gammaTable = img.data.gammaTable;
	    if (img.data.header.colorType == 0
		|| img.data.header.colorType == 2) {
		Chunk_tRNS trans = (Chunk_tRNS) img.getChunk(1951551059);
		if (trans != null) {
		    b.trans = trans.rgb;
		    b.translow = trans.rgb_low;
		    b.transgray = trans.r;
		}
	    }
	    return b;
	} catch (Exception e) {
	    throw new PngException("Error loading " + (Object) clsname);
	}
    }
}
