/* DummyImageConsumer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.util.Hashtable;

class DummyImageConsumer implements ImageConsumer
{
    public void setDimensions(int width, int height) {
	/* empty */
    }
    
    public void setProperties(Hashtable props) {
	/* empty */
    }
    
    public void setHints(int hintflags) {
	/* empty */
    }
    
    public void setPixels(int x, int y, int w, int h, ColorModel model,
			  byte[] pixels, int off, int scansize) {
	/* empty */
    }
    
    public void setPixels(int x, int y, int w, int h, ColorModel model,
			  int[] pixels, int off, int scansize) {
	/* empty */
    }
    
    public void imageComplete(int status) {
	/* empty */
    }
    
    public void setColorModel(ColorModel model) {
	/* empty */
    }
}
