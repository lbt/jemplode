/* Chunk_cHRM - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;

class Chunk_cHRM extends Chunk
{
    protected long xwlong;
    protected long ywlong;
    protected long xrlong;
    protected long yrlong;
    protected long xglong;
    protected long yglong;
    protected long xblong;
    protected long yblong;
    protected double xw;
    protected double yw;
    protected double xr;
    protected double yr;
    protected double xg;
    protected double yg;
    protected double xb;
    protected double yb;
    protected double Xw;
    protected double Yw;
    protected double Zw;
    protected double Xr;
    protected double Yr;
    protected double Zr;
    protected double Xg;
    protected double Yg;
    protected double Zg;
    protected double Xb;
    protected double Yb;
    protected double Zb;
    
    Chunk_cHRM() {
	super(1665684045);
    }
    
    protected boolean multipleOK() {
	return false;
    }
    
    protected boolean beforeIDAT() {
	return true;
    }
    
    protected void calc() {
	double zr = 1.0 - (xr + yr);
	double zg = 1.0 - (xg + yg);
	double zb = 1.0 - (xb + yb);
	double zw = 1.0 - (xw + yw);
	Xw = xw / yw;
	Yw = 1.0;
	Zw = zw / yw;
	double det = (xr * (yg * zb - zg * yb) - xg * (yr * zb - zr * yb)
		      + xb * (yr * zg - zr * yg));
	double fr = ((Xw * (yg * zb - zg * yb) - xg * (zb - Zw * yb)
		      + xb * (zg - Zw * yg))
		     / det);
	double fg = ((xr * (zb - Zw * yb) - Xw * (yr * zb - zr * yb)
		      + xb * (yr * Zw - zr))
		     / det);
	double fb = ((xr * (yg * Zw - zg) - xg * (yr * Zw - zr)
		      + Xw * (yr * zg - zr * yg))
		     / det);
	Xr = fr * xr;
	Yr = fr * yr;
	Zr = fr * zr;
	Xg = fg * xg;
	Yg = fg * yg;
	Zg = fg * zg;
	Xb = fb * xb;
	Yb = fb * yb;
	Zb = fb * zb;
	if (img.getChunk(1934772034) == null) {
	    img.data.properties.put("chromaticity xy",
				    new long[][] { { xwlong, ywlong },
						   { xrlong, yrlong },
						   { xglong, yglong },
						   { xblong, yblong } });
	    img.data.properties.put("chromaticity xyz",
				    (new double[][]
				     { { Xw, Yw, Zw }, { Xr, Yr, Zr },
				       { Xg, Yg, Zg }, { Xb, Yb, Zb } }));
	}
    }
    
    protected void readData() throws IOException {
	if (img.data.palette != null)
	    throw new PngException("cHRM chunk must precede PLTE chunk");
	if (length != 32)
	    badLength(32);
	checkRange(xw = ((double) (xwlong = in_data.readUnsignedInt())
			 / 100000.0),
		   "white");
	checkRange(yw = ((double) (ywlong = in_data.readUnsignedInt())
			 / 100000.0),
		   "white");
	checkRange(xr = ((double) (xrlong = in_data.readUnsignedInt())
			 / 100000.0),
		   "red");
	checkRange(yr = ((double) (yrlong = in_data.readUnsignedInt())
			 / 100000.0),
		   "red");
	checkRange(xg = ((double) (xglong = in_data.readUnsignedInt())
			 / 100000.0),
		   "green");
	checkRange(yg = ((double) (yglong = in_data.readUnsignedInt())
			 / 100000.0),
		   "green");
	checkRange(xb = ((double) (xblong = in_data.readUnsignedInt())
			 / 100000.0),
		   "blue");
	checkRange(yb = ((double) (yblong = in_data.readUnsignedInt())
			 / 100000.0),
		   "blue");
	calc();
    }
    
    private void checkRange(double value, String color) throws PngException {
	if (value < 0.0 || value > 0.8)
	    throw new PngExceptionSoft("Invalid cHRM " + color + " point");
    }
}
