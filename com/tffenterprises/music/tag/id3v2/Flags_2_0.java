/* Flags_2_0 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.tffenterprises.music.tag.TagDataFormatException;
import com.tffenterprises.music.tag.id3v2.io.UnsynchronizedInputStream;

class Flags_2_0 extends Flags implements Serializable, Cloneable
{
    public static final byte TAG_COMPRESSION_FLAG = 64;
    
    protected Flags_2_0() {
	this((byte) 0);
    }
    
    protected Flags_2_0(byte flags) {
	super(flags);
    }
    
    public boolean usesTagCompression() {
	return super.check((byte) 64);
    }
    
    public boolean isIncompatible() {
	return usesTagCompression();
    }
    
    public void checkVersion() throws TagDataFormatException {
	if ((getVersion() & 0xff00) != 512)
	    throw new TagDataFormatException
		      (this.getClass().getName()
		       + " is not compatible with protocol version "
		       + Integer.toHexString(getVersion()));
    }
    
    public InputStream processInput(InputStream in)
	throws IOException, TagDataFormatException {
	InputStream inStream = super.processInput(in);
	if (usesUnsynchronization())
	    inStream = new UnsynchronizedInputStream(inStream);
	if (usesTagCompression())
	    throw new TagDataFormatException
		      ("This ID3v2.2.x tag reports that it uses compression, which is an undefined and/or incompatible extension to ID3v2.2.x");
	return inStream;
    }
    
    public OutputStream processOutput(OutputStream out) {
	OutputStream output = super.processOutput(out);
	unset((byte) 64);
	unset((byte) -128);
	return processUnsynchronization(output);
    }
}
