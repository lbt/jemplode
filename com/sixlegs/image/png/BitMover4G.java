/* BitMover4G - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;
import java.io.InputStream;

final class BitMover4G extends BitMover
{
    int fill(int[] pixels, InputStream str, int off, int len)
	throws IOException {
	for (int n = 0; n < len; n++) {
	    int x = str.read();
	    int p1 = x >>> 4 & 0xf;
	    int p2 = x & 0xf;
	    pixels[off++] = (p1 == transgray ? 0 : -16777216) | gammaTable[p1];
	    pixels[off++] = (p1 == transgray ? 0 : -16777216) | gammaTable[p2];
	}
	return off;
    }
}
