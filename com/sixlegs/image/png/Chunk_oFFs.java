/* Chunk_oFFs - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;

final class Chunk_oFFs extends Chunk
{
    Chunk_oFFs() {
	super(1866876531);
    }
    
    protected boolean multipleOK() {
	return false;
    }
    
    protected boolean beforeIDAT() {
	return true;
    }
    
    protected void readData() throws IOException {
	int imagePositionX = in_data.readInt();
	int imagePositionY = in_data.readInt();
	int unit = in_data.readUnsignedByte();
	if (unit != 0 && unit != 1)
	    throw new PngExceptionSoft("Illegal oFFs chunk unit specifier: "
				       + unit);
	img.data.properties.put("image position x",
				new Integer(imagePositionX));
	img.data.properties.put("image position y",
				new Integer(imagePositionY));
	img.data.properties.put("image position unit", new Integer(unit));
    }
}
