/* LookFloor1 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;

class LookFloor1
{
    static final int VIF_POSIT = 63;
    int[] sorted_index = new int[65];
    int[] forward_index = new int[65];
    int[] reverse_index = new int[65];
    int[] hineighbor = new int[63];
    int[] loneighbor = new int[63];
    int posts;
    int n;
    int quant_q;
    InfoFloor1 vi;
    int phrasebits;
    int postbits;
    int frames;
    
    void free() {
	sorted_index = null;
	forward_index = null;
	reverse_index = null;
	hineighbor = null;
	loneighbor = null;
    }
}
