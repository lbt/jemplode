/* Lsp - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;

class Lsp
{
    static final float M_PI = 3.1415927F;
    
    static void lsp_to_curve(float[] curve, int[] map, int n, int ln,
			     float[] lsp, int m, float amp, float ampoffset) {
	float wdel = 3.1415927F / (float) ln;
	for (int i = 0; i < m; i++)
	    lsp[i] = Lookup.coslook(lsp[i]);
	int m2 = m / 2 * 2;
	int i = 0;
	while (i < n) {
	    int k = map[i];
	    float p = 0.70710677F;
	    float q = 0.70710677F;
	    float w = Lookup.coslook(wdel * (float) k);
	    for (int j = 0; j < m2; j += 2) {
		q *= lsp[j] - w;
		p *= lsp[j + 1] - w;
	    }
	    if ((m & 0x1) != 0) {
		q *= lsp[m - 1] - w;
		q *= q;
		p *= p * (1.0F - w * w);
	    } else {
		q *= q * (1.0F + w);
		p *= p * (1.0F - w);
	    }
	    q = p + q;
	    int hx = Float.floatToIntBits(q);
	    int ix = 0x7fffffff & hx;
	    int qexp = 0;
	    if (ix < 2139095040 && ix != 0) {
		if (ix < 8388608) {
		    q *= 3.3554432E7;
		    hx = Float.floatToIntBits(q);
		    ix = 0x7fffffff & hx;
		    qexp = -25;
		}
		qexp += (ix >>> 23) - 126;
		hx = hx & ~0x7f800000 | 0x3f000000;
		q = Float.intBitsToFloat(hx);
	    }
	    q = Lookup.fromdBlook((amp * Lookup.invsqlook(q)
				   * Lookup.invsq2explook(qexp + m))
				  - ampoffset);
	    do
		curve[i++] *= q;
	    while (i < n && map[i] == k);
	}
    }
}
