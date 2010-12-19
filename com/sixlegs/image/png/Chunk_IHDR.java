/* Chunk_IHDR - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.io.IOException;

final class Chunk_IHDR extends Chunk
{
    int width;
    int height;
    int depth;
    int outputDepth;
    int compress;
    int filter;
    int interlace;
    Interlacer interlacer;
    int samples = 1;
    int colorType;
    int cmBits;
    boolean paletteUsed = false;
    boolean colorUsed = false;
    boolean alphaUsed = false;
    ColorModel alphaModel;
    ColorModel model;
    
    Chunk_IHDR() {
	super(1229472850);
    }
    
    protected boolean multipleOK() {
	return false;
    }
    
    protected void readData() throws IOException {
	img.data.header = this;
	if (length != 13)
	    badLength(13);
	width = in_data.readInt();
	height = in_data.readInt();
	if (width <= 0 || height <= 0)
	    throw new PngException("Bad image size: "
				   + ExDataInputStream.unsign(width) + "x"
				   + ExDataInputStream.unsign(height));
	depth = in_data.readUnsignedByte();
	outputDepth = depth == 16 ? 8 : depth;
	int blue = 0;
	switch (outputDepth) {
	case 1:
	    blue = 1;
	    break;
	case 2:
	    blue = 3;
	    break;
	case 4:
	    blue = 15;
	    break;
	case 8:
	    blue = 255;
	    break;
	default:
	    throw new PngException("Bad bit depth: " + depth);
	}
	byte[] sbit = null;
	int green = blue << outputDepth;
	int red = green << outputDepth;
	int alpha = red << outputDepth;
	byte b_depth = (byte) depth;
	colorType = in_data.readUnsignedByte();
	switch (colorType) {
	case 0:
	    sbit = new byte[] { b_depth, b_depth, b_depth };
	    cmBits = 3 * outputDepth;
	    break;
	case 2:
	    sbit = new byte[] { b_depth, b_depth, b_depth };
	    cmBits = 3 * outputDepth;
	    samples = 3;
	    colorUsed = true;
	    break;
	case 3:
	    sbit = new byte[] { 8, 8, 8 };
	    cmBits = outputDepth;
	    colorUsed = paletteUsed = true;
	    break;
	case 4:
	    sbit = new byte[] { b_depth, b_depth, b_depth, b_depth };
	    cmBits = 4 * outputDepth;
	    samples = 2;
	    alphaUsed = true;
	    break;
	case 6:
	    sbit = new byte[] { b_depth, b_depth, b_depth, b_depth };
	    cmBits = 4 * outputDepth;
	    samples = 4;
	    alphaUsed = colorUsed = true;
	    break;
	default:
	    cmBits = 0;
	    throw new PngException("Bad color type: " + colorType);
	}
	img.data.properties.put("significant bits", sbit);
	if (!paletteUsed) {
	    if (alphaUsed)
		model = alphaModel
		    = new DirectColorModel(cmBits, red, green, blue, alpha);
	    else {
		alphaModel = ColorModel.getRGBdefault();
		model = new DirectColorModel(24, 16711680, 65280, 255);
	    }
	}
	switch (colorType) {
	case 0:
	    break;
	case 3:
	    if (depth == 16)
		throw new PngException("Bad bit depth for color type "
				       + colorType + ": " + depth);
	    break;
	default:
	    if (depth <= 4)
		throw new PngException("Bad bit depth for color type "
				       + colorType + ": " + depth);
	}
	if ((compress = in_data.readUnsignedByte()) != 0)
	    throw new PngException("Unrecognized compression method: "
				   + compress);
	if ((filter = in_data.readUnsignedByte()) != 0)
	    throw new PngException("Unrecognized filter method: " + filter);
	interlace = in_data.readUnsignedByte();
	switch (interlace) {
	case 0:
	    interlacer = new NullInterlacer(width, height);
	    break;
	case 1:
	    interlacer = new Adam7Interlacer(width, height);
	    break;
	default:
	    throw new PngException("Unrecognized interlace method: "
				   + interlace);
	}
	img.data.properties.put("width", new Integer(width));
	img.data.properties.put("height", new Integer(height));
	img.data.properties.put("bit depth", new Integer(depth));
	img.data.properties.put("interlace type", new Integer(interlace));
	img.data.properties.put("compression type", new Integer(compress));
	img.data.properties.put("filter type", new Integer(filter));
	img.data.properties.put("color type", new Integer(colorType));
    }
}
