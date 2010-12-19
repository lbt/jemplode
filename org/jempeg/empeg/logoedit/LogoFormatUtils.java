/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
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

/**
* A set of utilities for manipulating 
* Empeg Logo Format data.
*
* @author Mike Comb
* @author Mike Schrag
*/
public class LogoFormatUtils {
  public static final int DEFAULT_WIDTH  = 128;
  public static final int DEFAULT_HEIGHT = 32;

  public static final int DEFAULT_GRAY_VALUE_0 = 0;
  public static final int DEFAULT_GRAY_VALUE_1 = 80;
  public static final int DEFAULT_GRAY_VALUE_2 = 96;
  public static final int DEFAULT_GRAY_VALUE_3 = 255; 

  public static final int DEFAULT_CUTOFF_0 = 40;
  public static final int DEFAULT_CUTOFF_1 = 91;
  public static final int DEFAULT_CUTOFF_2 = 160;

  public static final int[] DEFAULT_GRAY_VALUES = new int[] { DEFAULT_GRAY_VALUE_0, DEFAULT_GRAY_VALUE_1, DEFAULT_GRAY_VALUE_2, DEFAULT_GRAY_VALUE_3 };
  public static final int[] DEFAULT_CUTOFFS     = new int[] { DEFAULT_CUTOFF_0, DEFAULT_CUTOFF_1, DEFAULT_CUTOFF_2 };

  /**
  * Creates a byte array in the format that the
  * Empeg expects for home and car logos.
  *
  * @param _os the output stream to write to
  * @param _logo the logo to create
  * @param _cutoffs the color cutoffs to use
  */
  public static void fromLogo(OutputStream _os, Logo _logo, int[] _cutoffs) throws IOException {
    byte[] homeLogo = LogoFormatUtils.toLogoFormat(_logo.getHomeImage(), DEFAULT_WIDTH, DEFAULT_HEIGHT, _cutoffs);
    byte[] carLogo  = LogoFormatUtils.toLogoFormat(_logo.getCarImage(), DEFAULT_WIDTH, DEFAULT_HEIGHT, _cutoffs);
    _os.write(_logo.getType().getBytes());
    _os.write(homeLogo, 0, homeLogo.length);
    _os.write(carLogo, 0, carLogo.length);
  }

  /**
  * Creates a Logo from a byte array.
  *
  * @param _is the input stream to read from
  * @param _grayValues the gray values to use
  */
  public static Logo toLogo(Component _comp, InputStream _is, int[] _grayValues) throws IOException {
    byte[] typeBytes = new byte[4];
    _is.read(typeBytes);
    String type;
    try {
      type = new String(typeBytes, "ISO-8859-1");
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      type = new String(typeBytes);
    }

    byte[] logoData = new byte[DEFAULT_WIDTH * DEFAULT_HEIGHT / 2];
    _is.read(logoData);
    Image homeImage = LogoFormatUtils.fromLogoFormat(_comp, logoData, DEFAULT_WIDTH, DEFAULT_HEIGHT, _grayValues);

    _is.read(logoData);
    Image carImage = LogoFormatUtils.fromLogoFormat(_comp, logoData, DEFAULT_WIDTH, DEFAULT_HEIGHT, _grayValues);

    Logo logo = new Logo(type, carImage, homeImage);
    return logo;
  }

  /**
  * Converts a java.awt.Image to an Empeg logo format byte array.
  *
  * @param _image the image to convert
  * @param _width the width of the image
  * @param _height the height of the image
  * @param _cutoffs the gray cutoff values to use
  * @throws InterruptedException if the pixel grab is interrupted.
  */
  public static byte[] toLogoFormat(Image _image, int _width, int _height, int[] _cutoffs) {
    int[] pixels = new int[_width * _height];
    PixelGrabber pg = new PixelGrabber(_image, 0, 0, _width, _height, pixels, 0, _width);
    try {
      pg.grabPixels();
    }
    catch (InterruptedException e) {
      // Shouldn't ever happen
      e.printStackTrace();
    }
    byte[] logoBytes = new byte[_width * _height];
    for (int y = _height - 1; y >= 0; y --) {
      for (int x = _width - 1; x >= 0; x --) {
        int pixelNum = (y * _width + x);
        int pixel = pixels[pixelNum] & 0xff;
        logoBytes[(_height - 1 - y) * _width + x] = to4bpp(pixel, _cutoffs);
      }
    }
    logoBytes = toLogoFormat(logoBytes, _width, _height);
    return logoBytes;
  }

  /**
  * Converts the given gray value to an Empeg color
  * index based on the given set of cutoffs.
  *
  * @param _grayValue the gray value to convert
  * @param _cutoffs the set of cutoff points
  * @returns a color index
  */
  public static byte to4bpp(int _grayValue, int[] _cutoffs) {
    byte logoGrayValue = -1;
    for (byte i = 0; i < _cutoffs.length; i ++) {
      if (_grayValue < _cutoffs[i]) {
        logoGrayValue = i;
        break;
      }
    }
    if (logoGrayValue == -1) {
      logoGrayValue = (byte)_cutoffs.length;
    }
    return logoGrayValue;
  }

  /**
  * Converts a logo 4bpp pixel to an index in the color array.
  *
  * @param _empegData the Empeg-format image
  * @param _x the x coordinate to lookup
  * @param _y the y coordinate to lookup
  * @returns an index in the color array
  */
  public static int from4bpp(byte[] _empegData, int _x, int _y) {
    return from4bpp(_empegData, (_y * DEFAULT_WIDTH + _x));
  }

  /**
  * Converts a logo 4bpp pixel to an index in the color array.
  *
  * @param _empegData the Empeg-format image
  * @param _pixel the pixel index to lookup
  * @returns an index in the color array
  */
  public static int from4bpp(byte[] _empegData, int _pixel) {
    int bitIndex = 4 * _pixel;
    int byteIndex = (int)Math.floor((float)bitIndex / 8.0);

    int val = _empegData[byteIndex];
    if (bitIndex % 8 >= 4) {
      val >>= 4;
    }
    val &= 0x0000000F;
  
    return val;
  }

  /**
  * Creates a java.awt.Image from the given Empeg logo data.
  *
  * @param _logoData Empeg logo data
  * @param _width the width of the image
  * @param _height the height of the image
  * @param _grayValues gray values to use
  * @returns a java.awt.Image from the given Empeg logo data
  */
  public static Image fromLogoFormat(Component _comp, byte[] _logoData, int _width, int _height, int[] _grayValues) {
    int[] pixels = new int[_width * _height];
    for (int i = 0; i < pixels.length; i ++) {
      int grayIndex = from4bpp(_logoData, i);
      int a = 255;
      int r = _grayValues[grayIndex];
      int g = r;
      int b = r;
      pixels[i] = (a << 24) | (g << 16) | (r << 8) | b;
    }
    MemoryImageSource imageSource = new MemoryImageSource(_width, _height, pixels, 0, _width);
    Image toolkitImage = Toolkit.getDefaultToolkit().createImage(imageSource);
    Image logoImage;
    if (_comp == null) {
      logoImage = toolkitImage;
    } else {
      logoImage = _comp.createImage(_width, _height);
      logoImage.getGraphics().drawImage(toolkitImage, 0, 0, _comp);
    }
    return logoImage;
  }

  /**
  * Converts a byte array of values {0, 1, 2, 3} that
  * correspond to the four valid Empeg gray values.
  *
  * @param _grayValues a set of gray values
  * @param _width the width of the image
  * @param _height the height of the image
  * @returns an Empeg logo format byte array
  */
  public static byte[] toLogoFormat(byte[] _logoGrayValues, int _width, int _height) {
    // Pack everything
    // 2D array makes this easier to flip
    byte[][] bin2 = new byte[_width / 2][_height];
    int x = 0;
    int y = (_height - 1);
    
    for (int i = 0; i < (_width * _height); i += 2) {
      int bothNibbles = ((_logoGrayValues[i + 1] * 16) + (_logoGrayValues[i]));
      // System.out.println("bothnibbles[" + c + "]: " + BothNibbles);
      bin2[x][y] = (byte)bothNibbles;
      x++;
      if (x == _width / 2) {
        y --;
        x = 0;
      }
    }

    // back to a 1D array
    // damn this is inneficient :-)
    byte[] bin3 = new byte[(_width / 2) * _height];
    x = 0;
    y = 0;
    // System.out.println("Length:" + bin3.length);
    for (int i = 0; i < bin3.length; i ++) {
      bin3[i] = bin2[x][y];
      x ++;
      if (x == _width / 2) {
        y ++;
        x = 0;
      }
    }
    
    return bin3;
  }
}
