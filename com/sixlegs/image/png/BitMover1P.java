/* BitMover1P - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;
import java.io.InputStream;

final class BitMover1P extends BitMover
{
    int fill(int[] pixels, InputStream str, int off, int len)
	throws IOException {
	for (int n = 0; n < len; n++) {
	    int x = str.read();
	    for (int i = 7; i >= 0; i--)
		pixels[off++] = x >>> i & 0x1;
	}
	return off;
    }
}
