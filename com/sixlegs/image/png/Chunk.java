/* Chunk - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;

class Chunk implements Cloneable
{
    int length;
    int type;
    protected PngImage img;
    protected ExDataInputStream in_data;
    static final int IHDR = 1229472850;
    static final int PLTE = 1347179589;
    static final int IDAT = 1229209940;
    static final int IEND = 1229278788;
    static final int bKGD = 1649100612;
    static final int cHRM = 1665684045;
    static final int gAMA = 1732332865;
    static final int hIST = 1749635924;
    static final int pHYs = 1883789683;
    static final int sBIT = 1933723988;
    static final int tEXt = 1950701684;
    static final int tIME = 1950960965;
    static final int tRNS = 1951551059;
    static final int zTXt = 2052348020;
    static final int sRGB = 1934772034;
    static final int sPLT = 1934642260;
    static final int oFFs = 1866876531;
    static final int sCAL = 1933787468;
    static final int iCCP = 1766015824;
    static final int pCAL = 1883455820;
    static final int iTXt = 1767135348;
    static final int gIFg = 1732855399;
    static final int gIFx = 1732855416;
    /*synthetic*/ static Class class$0;
    
    Chunk(int type) {
	this.type = type;
    }
    
    Chunk copy() {
	try {
	    return (Chunk) this.clone();
	} catch (CloneNotSupportedException e) {
	    return null;
	}
    }
    
    boolean isAncillary() {
	if ((type & 0x20000000) != 0)
	    return true;
	return false;
    }
    
    final boolean isPrivate() {
	if ((type & 0x200000) != 0)
	    return true;
	return false;
    }
    
    final boolean isReservedSet() {
	if ((type & 0x2000) != 0)
	    return true;
	return false;
    }
    
    final boolean isSafeToCopy() {
	if ((type & 0x20) != 0)
	    return true;
	return false;
    }
    
    final boolean isUnknown() {
	Class var_class = this.getClass();
	Class var_class_0_ = class$0;
	if (var_class_0_ == null) {
	    Class var_class_1_;
	    try {
		var_class_1_ = Class.forName("com.sixlegs.image.png.Chunk");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class_0_ = class$0 = var_class_1_;
	}
	if (var_class == var_class_0_)
	    return true;
	return false;
    }
    
    int bytesRemaining() {
	return Math.max(0, length + 4 - img.data.in_idat.count());
    }
    
    protected boolean multipleOK() {
	return true;
    }
    
    protected boolean beforeIDAT() {
	return false;
    }
    
    static String typeToString(int x) {
	return ("" + (char) (x >>> 24 & 0xff) + (char) (x >>> 16 & 0xff)
		+ (char) (x >>> 8 & 0xff) + (char) (x & 0xff));
    }
    
    static int stringToType(String id) {
	return ((id.charAt(0) & 0xff) << 24 | (id.charAt(1) & 0xff) << 16
		| (id.charAt(2) & 0xff) << 8 | id.charAt(3) & 0xff);
    }
    
    final void badLength(int correct) throws PngException {
	throw new PngException("Bad " + typeToString(type) + " chunk length: "
			       + ExDataInputStream.unsign(length)
			       + " (expected " + correct + ")");
    }
    
    final void badLength() throws PngException {
	throw new PngException("Bad " + typeToString(type) + " chunk length: "
			       + ExDataInputStream.unsign(length));
    }
    
    protected void readData() throws IOException {
	in_data.skipBytes(length);
    }
}
