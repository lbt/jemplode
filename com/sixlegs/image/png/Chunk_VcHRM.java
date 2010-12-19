/* Chunk_VcHRM - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;

final class Chunk_VcHRM extends Chunk_cHRM
{
    Chunk_VcHRM(PngImage img) {
	this.img = img;
	xw = 31270.0;
	yw = 32900.0;
	xr = 64000.0;
	yr = 33000.0;
	xg = 30000.0;
	yg = 60000.0;
	xb = 15000.0;
	yb = 6000.0;
	calc();
    }
}
