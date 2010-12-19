package com.inzyme.format;

import java.text.NumberFormat;

/**
 * SizeFormat is responsible for turning a size (in bytes) into a 
 * human readable format.  For instance: 1.5GB, 10.6MB, 20.4KB, 3.0B, etc.
 * 
 * @author Mike Schrag
 */
public class SizeFormat {
	private static final SizeFormat SIZE_FORMAT = new SizeFormat();
	 
	/**
	 * Constructor for SizeFormat.
	 */
	public SizeFormat() {
		super();
	}
	
	/**
	 * Returns an instance of a SizeFormat
	 */
	public static SizeFormat getInstance() {
		return SIZE_FORMAT;
	}
	
	/**
	 * Formats the given size into a "X.XX UU" where X
	 * is the human readable size and U is the human
	 * readable unit (GB, MB, etc.)
	 * 
	 * @param _sizeInBytes the size in bytes
	 * @return a human-readable size
	 */
	public String format(double _sizeInBytes) {
		String unit;
		double divSize;
		
		double gb = Math.pow(2, 30);
		if (_sizeInBytes >= gb) {
			unit = "GB";
			divSize = gb;
		} else {
			double mb = Math.pow(2, 20);
			if (_sizeInBytes >= mb) {
				unit = "MB";
				divSize = mb;
			} else {
				double kb = Math.pow(2, 10);
				if (_sizeInBytes >= kb) {
					unit = "KB";
					divSize = kb;
				} else {
					unit = "B";
					divSize = 1;
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
