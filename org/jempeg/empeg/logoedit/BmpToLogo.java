/* BmpToLogo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BmpToLogo
{
    public static void main(String[] args) {
	if (args.length != 4) {
	    System.err.println
		("Usage 'java bmpTo4bpp player-type(empeg | rio) home-file car-file output-file'\nHome and car files must be 24 bit/pixel, grayscale, uncompressed, 128 x 32 pixel bitmaps");
	    System.exit(1);
	}
	if (!args[0].equalsIgnoreCase("empeg")
	    && !args[0].equalsIgnoreCase("rio")) {
	    System.err.println
		("The first program argument must be 'empeg' or 'rio'");
	    System.exit(1);
	}
	try {
	    FileOutputStream os = new FileOutputStream(args[3]);
	    if (args[0].equalsIgnoreCase("empeg")) {
		byte[] header = "empg".getBytes();
		os.write(header);
	    } else if (args[0].equalsIgnoreCase("rio")) {
		byte[] header = "rioc".getBytes();
		os.write(header);
	    }
	    byte[] bin = convertBmpToLogo(new FileInputStream(args[1]),
					  LogoFormatUtils.DEFAULT_CUTOFFS);
	    byte[] bin2 = convertBmpToLogo(new FileInputStream(args[2]),
					   LogoFormatUtils.DEFAULT_CUTOFFS);
	    os.write(bin);
	    os.write(bin2);
	    os.close();
	} catch (FileNotFoundException fnfe) {
	    System.out.println("File not found");
	    System.exit(1);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }
    
    public static byte[] convertBmpToLogo(InputStream _is, int[] _cutoffs)
	throws IOException {
	int bilen = 40;
	byte[] bi = new byte[bilen];
	_is.read(bi, 0, 14);
	_is.read(bi, 0, bilen);
	int nwidth = ((bi[7] & 0xff) << 24 | (bi[6] & 0xff) << 16
		      | (bi[5] & 0xff) << 8 | bi[4] & 0xff);
	int nheight = ((bi[11] & 0xff) << 24 | (bi[10] & 0xff) << 16
		       | (bi[9] & 0xff) << 8 | bi[8] & 0xff);
	int nbitcount = (bi[15] & 0xff) << 8 | bi[14] & 0xff;
	int nsizeimage = ((bi[23] & 0xff) << 24 | (bi[22] & 0xff) << 16
			  | (bi[21] & 0xff) << 8 | bi[20] & 0xff);
	if (nbitcount == 24) {
	    int npad = nsizeimage / nheight - nwidth * 3;
	    byte[] brgb = new byte[(nwidth + npad) * 3 * nheight];
	    byte[] bin = new byte[nwidth * nheight];
	    _is.read(brgb, 0, (nwidth + npad) * 3 * nheight);
	    for (int i = 0; i < brgb.length; i += 3) {
		int R = brgb[i] & 0xff;
		int G = brgb[i + 1] & 0xff;
		int B = brgb[i + 2] & 0xff;
		if (R == G && G == B)
		    bin[i / 3] = LogoFormatUtils.to4bpp(R, _cutoffs);
		else
		    throw new IOException
			      ("Please supply a 24bit grayscale bitmap");
	    }
	    byte[] logoBytes
		= LogoFormatUtils.toLogoFormat(bin, nwidth, nheight);
	    _is.close();
	    return logoBytes;
	}
	_is.close();
	throw new IOException("Please supply a 24bit grayscale bitmap");
    }
}
