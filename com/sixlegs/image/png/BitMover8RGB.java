/* BitMover8RGB - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;
import java.io.InputStream;

final class BitMover8RGB extends BitMover
{
    int fill(int[] pixels, InputStream str, int off, int len)
	throws IOException {
	for (int n = 0; n < len; n++) {
	    int r = str.read();
	    int g = str.read();
	    int b = str.read();
	    int val = gammaTable[r] << 16 | gammaTable[g] << 8 | gammaTable[b];
	    pixels[off++]
		= ((r << 16 | g << 8 | b) == trans ? 0 : -16777216) | val;
	}
	return off;
    }
}
