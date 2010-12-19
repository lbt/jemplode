package org.jempeg.empeg.logoedit;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Converter program to turn 24bit per pixel grayscale bitmap to 4bpp empeg logo.
 * To use it compile the file, add it to your CLASSPATH and then run
 *
 * > java bmpTo4bpp player-type home-logo car-logo output-file
 *
 * The first parameter should be either 'empeg' or 'rio' and determines if the 
 * empeg should boot with the empeg  graphic/animation or the rio graphic/animation. 
 * The second parameter is the home logo. The third parameter is the car logo. The 
 * fourth parameter is the output file to create.
 *
 * All bitmaps must be 24bit, uncompressed, grayscale images. This program will 
 * warn you if you try to feed it something else. You can convert other types of 
 * images using PhotoShop/Gimp/GraphicConverter/etc. Some software claims 24bit 
 * images are really 32bit so try both.
 *
 * The output file that this creates still needs to be copied to the empeg with 
 * download.c from the empeg website. The most excelent FAQ has some info on this
 * process.
 * 
 * Some code derived from javaworld programming sample (see comment below).  
 *
 * All other code written by Mike Comb (mcomb@mac.com) and may be used, modified, and
 * redistributed by anyone for any reason without prior permission.  If modified
 * code is redistributed please either remove this comment block or note that code
 * has been modified from original version.  Attributions are appreciated.
 */
public class BmpToLogo {
	/* 0 - 30 = Black
	 * 31 - 93 = Dark Grey
	 * 94 - 200 = Light Grey
	 * 201 - 255 = White
	 */
	public static void main(String[] args) {

		if (args.length != 4) {
			System.err.println("Usage 'java bmpTo4bpp player-type(empeg | rio) home-file car-file output-file'\n" + "Home and car files must be 24 bit/pixel, grayscale, uncompressed, 128 x 32 pixel bitmaps");
			System.exit(1);
		}

		if (!(args[0].equalsIgnoreCase("empeg") || args[0].equalsIgnoreCase("rio"))) {
			System.err.println("The first program argument must be 'empeg' or 'rio'");
			System.exit(1);
		}

		try {
			FileOutputStream os = new FileOutputStream(args[3]);
			if (args[0].equalsIgnoreCase("empeg")) {
				byte header[] = "empg".getBytes();
				os.write(header);
			}
			else if (args[0].equalsIgnoreCase("rio")) {
				byte header[] = "rioc".getBytes();
				os.write(header);
			}

			byte bin[] = convertBmpToLogo(new FileInputStream(args[1]), LogoFormatUtils.DEFAULT_CUTOFFS);
			byte bin2[] = convertBmpToLogo(new FileInputStream(args[2]), LogoFormatUtils.DEFAULT_CUTOFFS);
			os.write(bin);
			os.write(bin2);
			os.close();
		}
		catch (java.io.FileNotFoundException fnfe) {
			System.out.println("File not found");
			System.exit(1);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static byte[] convertBmpToLogo(InputStream _is, int[] _cutoffs) throws IOException {
		/* Bitmap header parsing code derived from sample at
		* http://www.javaworld.com/javaworld/javatips/jw-javatip43.html
		* by Jeff West with no copyright or license info listed.
		*/
		int bilen = 40; // 40-byte BITMAPINFOHEADER
		byte bi[] = new byte[bilen];

		// Skip past the File header
		_is.read(bi, 0, 14);

		// Read in the info header
		_is.read(bi, 0, bilen);

		int nwidth = (((int) bi[7] & 0xff) << 24) | (((int) bi[6] & 0xff) << 16) | (((int) bi[5] & 0xff) << 8) | (int) bi[4] & 0xff;
		// System.out.println("Width is :"+nwidth);

		int nheight = (((int) bi[11] & 0xff) << 24) | (((int) bi[10] & 0xff) << 16) | (((int) bi[9] & 0xff) << 8) | (int) bi[8] & 0xff;
		// System.out.println("Height is :"+nheight);

		int nbitcount = (((int) bi[15] & 0xff) << 8) | (int) bi[14] & 0xff;
		// System.out.println("BitCount is :"+nbitcount);

		int nsizeimage = (((int) bi[23] & 0xff) << 24) | (((int) bi[22] & 0xff) << 16) | (((int) bi[21] & 0xff) << 8) | (int) bi[20] & 0xff;
		// System.out.println("SizeImage is :"+nsizeimage);

		/* End of header parsing code */

		if (nbitcount == 24) {
			int npad = (nsizeimage / nheight) - nwidth * 3;
			byte brgb[] = new byte[(nwidth + npad) * 3 * nheight];
			byte bin[] = new byte[nwidth * nheight];
			_is.read(brgb, 0, (nwidth + npad) * 3 * nheight);
			// System.out.println("brgb.length:" + brgb.length);

			// Downsample everything to 4bpp
			for (int i = 0; i < brgb.length; i = i + 3) {
				int R = (int) brgb[i] & 0xff;
				int G = (int) brgb[i + 1] & 0xff;
				int B = (int) brgb[i + 2] & 0xff;

				// Make sure RGB values are all the same
				if (R == G && G == B) {
					bin[i / 3] = LogoFormatUtils.to4bpp(R, _cutoffs);
				}
				else {
					throw new IOException("Please supply a 24bit grayscale bitmap");
				}
			}

			byte[] logoBytes = LogoFormatUtils.toLogoFormat(bin, nwidth, nheight);
			_is.close();

			return logoBytes;
		}
		else {
			_is.close();
			throw new IOException("Please supply a 24bit grayscale bitmap");
		}
	}
}
