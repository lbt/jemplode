/* TXXXFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import com.tffenterprises.io.ByteArrayInputStream;
import com.tffenterprises.io.ByteArrayOutputStream;

public class TXXXFrame extends TextBasedFrame
    implements KeyedFrame, Serializable, Cloneable
{
    private static final String FRAME_ID = "TXXX";
    private String frameDescription = "";
    private String frameValue = "";
    
    public TXXXFrame() {
	super.getHeader().setFrameID("TXXX");
    }
    
    public TXXXFrame(FrameHeader header) throws IllegalArgumentException {
	super(header);
	header.setFrameID("TXXX");
    }
    
    public TXXXFrame(FrameHeader header, String frameDescription,
		     String frameValue) throws IllegalArgumentException {
	this(header);
	if (frameDescription == null || frameValue == null)
	    throw new IllegalArgumentException
		      ("A null argument was passed to the TXXXFrame constructor.");
	this.frameDescription = frameDescription;
	this.frameValue = frameValue;
    }
    
    public synchronized Object clone() {
	TXXXFrame newFrame = (TXXXFrame) super.clone();
	newFrame.frameDescription = frameDescription;
	newFrame.frameValue = frameValue;
	return newFrame;
    }
    
    public boolean equals(Object other) {
	if (!super.equals(other))
	    return false;
	TXXXFrame otherFrame = (TXXXFrame) other;
	if (otherFrame.frameDescription == null
	    || !otherFrame.frameDescription.equals(frameDescription)
	    || otherFrame.frameValue == null
	    || !otherFrame.frameValue.equals(frameValue))
	    return false;
	return true;
    }
    
    public String toString() {
	String superstring = super.toString();
	return (superstring + "\n" + "  Description = " + frameDescription
		+ "\n" + "  Value       = " + frameValue);
    }
    
    public byte[] getRawData() {
	if (frameDescription.length() + frameValue.length() > 0) {
	    String encoding = "UnicodeBig";
	    if (is8859String(frameDescription) && is8859String(frameValue))
		encoding = "ISO8859_1";
	    int len
		= 20 + 2 * frameDescription.length() + 2 * frameValue.length();
	    ByteArrayOutputStream bs = new ByteArrayOutputStream(len);
	    try {
		bs.write(getTextEncoding(encoding));
		if (frameDescription.length() > 0) {
		    bs.write(frameDescription.getBytes(encoding));
		    bs.write(getNullBytes(encoding));
		} else
		    bs.write(NULL_CHAR_STRING.getBytes(encoding));
		if (frameValue.length() > 0) {
		    bs.write(frameValue.getBytes(encoding));
		    bs.write(getNullBytes(encoding));
		} else
		    bs.write(NULL_CHAR_STRING.getBytes(encoding));
	    } catch (UnsupportedEncodingException uee) {
		throw new RuntimeException
			  ("There is something quite wrong with your java VM: it does not support the "
			   + encoding + " encoding");
	    } catch (FrameDataFormatException fdfe) {
		fdfe.printStackTrace(System.err);
		return new byte[1];
	    }
	    return bs.toByteArray();
	}
	return null;
    }
    
    public void setRawData(byte[] rawData)
	throws FrameDataFormatException, IllegalArgumentException {
	ByteArrayInputStream raw = new ByteArrayInputStream(rawData);
	String encoding = getTextEncoding(raw);
	String fullFrame = readString(raw, encoding);
	StringTokenizer st
	    = new StringTokenizer(fullFrame, NULL_BOM_STRING, true);
	if (st.hasMoreTokens()) {
	    String token = st.nextToken();
	    if (!token.equals(NULL_CHAR_STRING)
		&& !token.equals(NULL_BOM_STRING))
		setDescription(token);
	    while (st.hasMoreTokens()) {
		token = st.nextToken();
		if (!token.equals(NULL_CHAR_STRING)
		    && !token.equals(NULL_BOM_STRING))
		    setValue(token);
	    }
	}
    }
    
    public boolean isOfRepeatableType() {
	return true;
    }
    
    public String getDescription() {
	return frameDescription;
    }
    
    public void setDescription(String frameDescription) {
	if (frameDescription == null)
	    throw new IllegalArgumentException
		      ("null text passed to TXXXFrame.setDescription()");
	this.frameDescription = frameDescription;
	setChanged(true);
    }
    
    public String getValue() {
	return frameValue;
    }
    
    public void setValue(String frameValue) {
	if (frameValue == null)
	    throw new IllegalArgumentException
		      ("null text passed to TXXXFrame.setValue()");
	this.frameValue = frameValue;
	setChanged(true);
    }
}
