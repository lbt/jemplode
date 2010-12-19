/* BitMover2G - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;
import java.io.InputStream;

final class BitMover2G extends BitMover
{
    int fill(int[] pixels, InputStream str, int off, int len)
	throws IOException {
	for (int n = 0; n < len; n++) {
	    int x = str.read();
	    int p1 = x >>> 6 & 0x3;
	    int p2 = x >>> 4 & 0x3;
	    int p3 = x >>> 2 & 0x3;
	    int p4 = x & 0x3;
	    pixels[off++]
		= (p1 == transgray ? 0 : -268435456) | gammaTable[p1];
	    pixels[off++]
		= (p2 == transgray ? 0 : -268435456) | gammaTable[p2];
	    pixels[off++]
		= (p3 == transgray ? 0 : -268435456) | gammaTable[p3];
	    pixels[off++]
		= (p4 == transgray ? 0 : -268435456) | gammaTable[p4];
	}
	return off;
    }
}
