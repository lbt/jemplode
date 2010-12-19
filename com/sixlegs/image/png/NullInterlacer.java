/* NullInterlacer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;

final class NullInterlacer extends Interlacer
{
    NullInterlacer(int w, int h) {
	super(w, h);
    }
    
    final int numPasses() {
	return 1;
    }
    
    final int getSpacingX(int pass) {
	return 1;
    }
    
    final int getSpacingY(int pass) {
	return 1;
    }
    
    final int getOffsetX(int pass) {
	return 0;
    }
    
    final int getOffsetY(int pass) {
	return 0;
    }
}
