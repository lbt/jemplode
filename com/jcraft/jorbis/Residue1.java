/* Residue1 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;

class Residue1 extends Residue0
{
    int forward(Block vb, Object vl, float[][] in, int ch) {
	System.err.println("Residue0.forward: not implemented");
	return 0;
    }
    
    int inverse(Block vb, Object vl, float[][] in, int[] nonzero, int ch) {
	int used = 0;
	for (int i = 0; i < ch; i++) {
	    if (nonzero[i] != 0)
		in[used++] = in[i];
	}
	if (used != 0)
	    return _01inverse(vb, vl, in, used, 1);
	return 0;
    }
}
