/* IdentificationOggHeader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.ogg;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.util.Assertions;
import com.inzyme.util.ReflectionUtils;

public class IdentificationOggHeader extends AbstractOggHeader
{
    private long myVersion;
    private int myAudioChannels;
    private long mySampleRate;
    private int myBitrateMaximum;
    private int myBitrateNominal;
    private int myBitrateMinimum;
    private int myBlockSize0;
    private int myBlockSize1;
    
    public IdentificationOggHeader(OggHeaderID _headerID) {
	super(_headerID);
    }
    
    public void read(LittleEndianInputStream _is) throws IOException {
	myVersion = _is.readUnsigned32();
	myAudioChannels = _is.readUnsigned8();
	mySampleRate = _is.readUnsigned32();
	myBitrateMaximum = (int) _is.readUnsigned32();
	myBitrateNominal = (int) _is.readUnsigned32();
	myBitrateMinimum = (int) _is.readUnsigned32();
	int blockSizes = _is.read();
	myBlockSize0 = 1 << (blockSizes & 0xf);
	myBlockSize1 = 1 << (blockSizes >> 4);
	int framingFlag = _is.readUnsigned8();
	Assertions.assertEquals("version", myVersion, 0L);
	Assertions.assertGreaterThan("audio channels", (long) myAudioChannels,
				     0L);
	Assertions.assertGreaterThan("samplerate", mySampleRate, 0L);
	Assertions.assertEquals("blockSize0", (long) myBlockSize0,
				new long[] { 64L, 128L, 256L, 512L, 1024L,
					     2048L, 4096L, 8192L });
	Assertions.assertEquals("blockSize1", (long) myBlockSize1,
				new long[] { 64L, 128L, 256L, 512L, 1024L,
					     2048L, 4096L, 8192L });
	Assertions.assertLessThanOrEqualTo("blockSize0", (long) myBlockSize0,
					   (long) myBlockSize1);
	Assertions.assertNotEquals("framing bit", (long) framingFlag, 0L);
    }
    
    public int getAudioChannels() {
	return myAudioChannels;
    }
    
    public int getBitrateMaximum() {
	return myBitrateMaximum;
    }
    
    public int getBitrateMinimum() {
	return myBitrateMinimum;
    }
    
    public int getBitrateNominal() {
	return myBitrateNominal;
    }
    
    public int getBitrate() {
	int bitrate;
	if (myBitrateNominal > 0)
	    bitrate = myBitrateNominal;
	else if (myBitrateMaximum > 0) {
	    if (myBitrateMinimum > 0)
		bitrate = (myBitrateMinimum + myBitrateMaximum) / 2;
	    else
		bitrate = myBitrateMaximum;
	} else
	    bitrate = 0;
	return bitrate;
    }
    
    public int getBlockSize0() {
	return myBlockSize0;
    }
    
    public int getBlockSize1() {
	return myBlockSize1;
    }
    
    public long getSampleRate() {
	return mySampleRate;
    }
    
    public long getVersion() {
	return myVersion;
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
