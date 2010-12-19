/* SizeFormat - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.format;
import java.text.NumberFormat;

public class SizeFormat
{
    private static final SizeFormat SIZE_FORMAT = new SizeFormat();
    
    public static SizeFormat getInstance() {
	return SIZE_FORMAT;
    }
    
    public String format(double _sizeInBytes) {
	double gb = Math.pow(2.0, 30.0);
	String unit;
	double divSize;
	if (_sizeInBytes >= gb) {
	    unit = "GB";
	    divSize = gb;
	} else {
	    double mb = Math.pow(2.0, 20.0);
	    if (_sizeInBytes >= mb) {
		unit = "MB";
		divSize = mb;
	    } else {
		double kb = Math.pow(2.0, 10.0);
		if (_sizeInBytes >= kb) {
		    unit = "KB";
		    divSize = kb;
		} else {
		    unit = "B";
		    divSize = 1.0;
		}
	    }
	}
	double visibleSize = _sizeInBytes / divSize;
	NumberFormat sizeFormat = NumberFormat.getInstance();
	sizeFormat.setMinimumFractionDigits(1);
	sizeFormat.setMaximumFractionDigits(2);
	StringBuffer sizeBuf = new StringBuffer();
	sizeBuf.append(sizeFormat.format(visibleSize));
	sizeBuf.append(" ");
	sizeBuf.append(unit);
	return sizeBuf.toString();
    }
}
