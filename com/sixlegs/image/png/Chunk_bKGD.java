/* Chunk_bKGD - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.awt.Color;
import java.io.IOException;

final class Chunk_bKGD extends Chunk
{
    Chunk_bKGD() {
	super(1649100612);
    }
    
    protected boolean multipleOK() {
	return false;
    }
    
    protected boolean beforeIDAT() {
	return true;
    }
    
    protected void readData() throws IOException {
	int r;
	int g;
	int b;
	switch (img.data.header.colorType) {
	case 3: {
	    if (length != 1)
		badLength(1);
	    int index = in_data.readUnsignedByte();
	    if (img.data.palette == null)
		throw new PngException("hIST chunk must follow PLTE chunk");
	    img.data.properties.put("background index", new Integer(index));
	    r = img.data.palette.r_raw[index];
	    g = img.data.palette.g_raw[index];
	    b = img.data.palette.b_raw[index];
	    break;
	}
	case 0:
	case 4:
	    if (length != 2)
		badLength(2);
	    if (img.data.header.depth == 16) {
		r = g = b = in_data.readUnsignedByte();
		int low = in_data.readUnsignedByte();
		img.data.properties.put("background low bytes",
					new Color(low, low, low));
	    } else
		r = g = b = in_data.readUnsignedShort();
	    break;
	default:
	    if (length != 6)
		badLength(6);
	    if (img.data.header.depth == 16) {
		r = in_data.readUnsignedByte();
		int low_r = in_data.readUnsignedByte();
		g = in_data.readUnsignedByte();
		int low_g = in_data.readUnsignedByte();
		b = in_data.readUnsignedByte();
		int low_b = in_data.readUnsignedByte();
		img.data.properties.put("background low bytes",
					new Color(low_r, low_g, low_b));
	    } else {
		r = in_data.readUnsignedShort();
		g = in_data.readUnsignedShort();
		b = in_data.readUnsignedShort();
	    }
	}
	img.data.properties.put("background", new Color(r, g, b));
    }
}
