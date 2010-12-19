/* Residue2 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;

class Residue2 extends Residue0
{
    int forward(Block vb, Object vl, float[][] in, int ch) {
	System.err.println("Residue0.forward: not implemented");
	return 0;
    }
    
    int inverse(Block vb, Object vl, float[][] in, int[] nonzero, int ch) {
	int i = 0;
	for (i = 0; i < ch; i++) {
	    if (nonzero[i] != 0)
		break;
	}
	if (i == ch)
	    return 0;
	return _2inverse(vb, vl, in, ch);
    }
}
