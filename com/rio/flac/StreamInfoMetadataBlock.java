/* StreamInfoMetadataBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.flac;
import java.io.IOException;
import java.io.InputStream;

import com.inzyme.typeconv.TypeConversionUtils;
import com.inzyme.util.ReflectionUtils;

public class StreamInfoMetadataBlock implements IFlacMetadataBlock
{
    private FlacMetadataBlockHeader myHeader;
    private int myMinimumBlockSize;
    private int myMaximumBlockSize;
    private int myMinimumFrameSize;
    private int myMaximumFrameSize;
    private int mySampleRate;
    private int myNumChannels;
    private int myBitsPerSample;
    private long myTotalSamples;
    private byte[] myMD5Sum;
    
    public StreamInfoMetadataBlock(FlacMetadataBlockHeader _header) {
	myHeader = _header;
    }
    
    public FlacMetadataBlockHeader getHeader() {
	return myHeader;
    }
    
    public int getMinimumBlockSize() {
	return myMinimumBlockSize;
    }
    
    public int getMaximumBlockSize() {
	return myMaximumBlockSize;
    }
    
    public int getMinimumFrameSize() {
	return myMinimumFrameSize;
    }
    
    public int getMaximumFrameSize() {
	return myMaximumFrameSize;
    }
    
    public int getSampleRate() {
	return mySampleRate;
    }
    
    public int getNumChannels() {
	return myNumChannels;
    }
    
    public int getBitsPerSample() {
	return myBitsPerSample;
    }
    
    public long getTotalSamples() {
	return myTotalSamples;
    }
    
    public byte[] getMD5Sum() {
	return myMD5Sum;
    }
    
    public void read(InputStream _is) throws IOException {
	byte[] bytez = new byte[34];
	_is.read(bytez);
	myMinimumBlockSize = (TypeConversionUtils.toUnsigned8(bytez[0]) << 8
			      | TypeConversionUtils.toUnsigned8(bytez[1]));
	myMaximumBlockSize = (TypeConversionUtils.toUnsigned8(bytez[2]) << 8
			      | TypeConversionUtils.toUnsigned8(bytez[3]));
	myMinimumFrameSize = (TypeConversionUtils.toUnsigned8(bytez[4]) << 16
			      | TypeConversionUtils.toUnsigned8(bytez[5]) << 8
			      | TypeConversionUtils.toUnsigned8(bytez[6]));
	myMaximumFrameSize = (TypeConversionUtils.toUnsigned8(bytez[7]) << 16
			      | TypeConversionUtils.toUnsigned8(bytez[8]) << 8
			      | TypeConversionUtils.toUnsigned8(bytez[9]));
	mySampleRate = (TypeConversionUtils.toUnsigned8(bytez[10]) << 12
			| TypeConversionUtils.toUnsigned8(bytez[11]) << 4
			| TypeConversionUtils.toUnsigned8(bytez[12]) >> 4);
	myNumChannels
	    = (TypeConversionUtils.toUnsigned8(bytez[12]) >> 1 & 0x7) + 1;
	myBitsPerSample
	    = ((TypeConversionUtils.toUnsigned8(bytez[12]) & 0x1) << 4
	       | TypeConversionUtils.toUnsigned8(bytez[13]) >> 4) + 1;
	myTotalSamples
	    = (((long) TypeConversionUtils.toUnsigned8(bytez[13]) & 0xfL) << 32
	       | (long) TypeConversionUtils.toUnsigned8(bytez[14]) << 24
	       | (long) TypeConversionUtils.toUnsigned8(bytez[15]) << 16
	       | (long) TypeConversionUtils.toUnsigned8(bytez[16]) << 8
	       | (long) TypeConversionUtils.toUnsigned8(bytez[17]));
	myMD5Sum = new byte[16];
	System.arraycopy(bytez, 18, myMD5Sum, 0, myMD5Sum.length);
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
