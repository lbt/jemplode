/* LogoFormatUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class LogoFormatUtils
{
    public static final int DEFAULT_WIDTH = 128;
    public static final int DEFAULT_HEIGHT = 32;
    public static final int DEFAULT_GRAY_VALUE_0 = 0;
    public static final int DEFAULT_GRAY_VALUE_1 = 80;
    public static final int DEFAULT_GRAY_VALUE_2 = 96;
    public static final int DEFAULT_GRAY_VALUE_3 = 255;
    public static final int DEFAULT_CUTOFF_0 = 40;
    public static final int DEFAULT_CUTOFF_1 = 91;
    public static final int DEFAULT_CUTOFF_2 = 160;
    public static final int[] DEFAULT_GRAY_VALUES = { 0, 80, 96, 255 };
    public static final int[] DEFAULT_CUTOFFS = { 40, 91, 160 };
    
    public static void fromLogo
	(OutputStream _os, Logo _logo, int[] _cutoffs) throws IOException {
	byte[] homeLogo
	    = toLogoFormat(_logo.getHomeImage(), 128, 32, _cutoffs);
	byte[] carLogo = toLogoFormat(_logo.getCarImage(), 128, 32, _cutoffs);
	_os.write(_logo.getType().getBytes());
	_os.write(homeLogo, 0, homeLogo.length);
	_os.write(carLogo, 0, carLogo.length);
    }
    
    public static Logo toLogo(Component _comp, InputStream _is,
			      int[] _grayValues) throws IOException {
	byte[] typeBytes = new byte[4];
	_is.read(typeBytes);
	String type;
	try {
	    type = new String(typeBytes, "ISO-8859-1");
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	    type = new String(typeBytes);
	}
	byte[] logoData = new byte[2048];
	_is.read(logoData);
	Image homeImage
	    = fromLogoFormat(_comp, logoData, 128, 32, _grayValues);
	_is.read(logoData);
	Image carImage = fromLogoFormat(_comp, logoData, 128, 32, _grayValues);
	Logo logo = new Logo(type, carImage, homeImage);
	return logo;
    }
    
    public static byte[] toLogoFormat(Image _image, int _width, int _height,
				      int[] _cutoffs) {
	int[] pixels = new int[_width * _height];
	PixelGrabber pg = new PixelGrabber(_image, 0, 0, _width, _height,
					   pixels, 0, _width);
	try {
	    pg.grabPixels();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	byte[] logoBytes = new byte[_width * _height];
	for (int y = _height - 1; y >= 0; y--) {
	    for (int x = _width - 1; x >= 0; x--) {
		int pixelNum = y * _width + x;
		int pixel = pixels[pixelNum] & 0xff;
		logoBytes[(_height - 1 - y) * _width + x]
		    = to4bpp(pixel, _cutoffs);
	    }
	}
	logoBytes = toLogoFormat(logoBytes, _width, _height);
	return logoBytes;
    }
    
    public static byte to4bpp(int _grayValue, int[] _cutoffs) {
	byte logoGrayValue = -1;
	for (byte i = 0; i < _cutoffs.length; i++) {
	    if (_grayValue < _cutoffs[i]) {
		logoGrayValue = i;
		break;
	    }
	}
	if (logoGrayValue == -1)
	    logoGrayValue = (byte) _cutoffs.length;
	return logoGrayValue;
    }
    
    public static int from4bpp(byte[] _empegData, int _x, int _y) {
	return from4bpp(_empegData, _y * 128 + _x);
    }
    
    public static int from4bpp(byte[] _empegData, int _pixel) {
	int bitIndex = 4 * _pixel;
	int byteIndex = (int) Math.floor((double) (float) bitIndex / 8.0);
	int val = _empegData[byteIndex];
	if (bitIndex % 8 >= 4)
	    val >>= 4;
	val &= 0xf;
	return val;
    }
    
    public static Image fromLogoFormat(Component _comp, byte[] _logoData,
				       int _width, int _height,
				       int[] _grayValues) {
	int[] pixels = new int[_width * _height];
	for (int i = 0; i < pixels.length; i++) {
	    int grayIndex = from4bpp(_logoData, i);
	    int a = 255;
	    int r = _grayValues[grayIndex];
	    int g = r;
	    int b = r;
	    pixels[i] = a << 24 | g << 16 | r << 8 | b;
	}
	MemoryImageSource imageSource
	    = new MemoryImageSource(_width, _height, pixels, 0, _width);
	Image toolkitImage
	    = Toolkit.getDefaultToolkit().createImage(imageSource);
	Image logoImage;
	if (_comp == null)
	    logoImage = toolkitImage;
	else {
	    logoImage = _comp.createImage(_width, _height);
	    logoImage.getGraphics().drawImage(toolkitImage, 0, 0, _comp);
	}
	return logoImage;
    }
    
    public static byte[] toLogoFormat(byte[] _logoGrayValues, int _width,
				      int _height) {
	byte[][] bin2 = new byte[_width / 2][_height];
	int x = 0;
	int y = _height - 1;
	for (int i = 0; i < _width * _height; i += 2) {
	    int bothNibbles = _logoGrayValues[i + 1] * 16 + _logoGrayValues[i];
	    bin2[x][y] = (byte) bothNibbles;
	    if (++x == _width / 2) {
		y--;
		x = 0;
	    }
	}
	byte[] bin3 = new byte[_width / 2 * _height];
	x = 0;
	y = 0;
	for (int i = 0; i < bin3.length; i++) {
	    bin3[i] = bin2[x][y];
	    if (++x == _width / 2) {
		y++;
		x = 0;
	    }
	}
	return bin3;
    }
}
