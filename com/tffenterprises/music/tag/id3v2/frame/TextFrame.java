/* TextFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.tffenterprises.io.ByteArrayInputStream;
import com.tffenterprises.io.ByteArrayOutputStream;

public class TextFrame extends TextBasedFrame
    implements Serializable, Cloneable
{
    private String frameText = "";
    
    TextFrame() {
	super.getHeader().setFrameID("TZZZ");
    }
    
    public TextFrame(FrameHeader header) {
	super(header);
	if (!header.getFrameID().startsWith("T"))
	    header.setFrameID("TZZZ");
    }
    
    public TextFrame(FrameHeader header, String frameText)
	throws IllegalArgumentException {
	this(header);
	if (frameText == null)
	    throw new IllegalArgumentException
		      ("A null argument was passed to the TextFrame constructor.");
	this.frameText = frameText;
    }
    
    public synchronized Object clone() {
	TextFrame newFrame = (TextFrame) super.clone();
	if (newFrame != null)
	    newFrame.frameText = frameText;
	return newFrame;
    }
    
    public boolean equals(Object other) {
	if (!super.equals(other))
	    return false;
	TextFrame otherFrame = (TextFrame) other;
	if (otherFrame.frameText == null
	    || !otherFrame.frameText.equals(frameText))
	    return false;
	return true;
    }
    
    public String toString() {
	return super.toString() + ": " + frameText;
    }
    
    public byte[] getRawData() {
	String text = getText() + NULL_CHAR_STRING;
	if (text.length() > 0) {
	    String encoding = "UnicodeLittle";
	    if (is8859String(text))
		encoding = "ISO8859_1";
	    int len = 1 + 2 * text.length();
	    ByteArrayOutputStream bs = new ByteArrayOutputStream(len);
	    try {
		bs.write(getTextEncoding(encoding));
		bs.write(text.getBytes(encoding));
	    } catch (UnsupportedEncodingException uee) {
		throw new RuntimeException
			  ("There is something quite wrong with your java VM: it does not support the "
			   + encoding + " encoding");
	    } catch (FrameDataFormatException fdfe) {
		fdfe.printStackTrace(System.err);
		return null;
	    }
	    return bs.toByteArray();
	}
	return null;
    }
    
    public void setRawData(byte[] rawData)
	throws FrameDataFormatException, IllegalArgumentException {
	boolean debug = false;
	ByteArrayInputStream raw = new ByteArrayInputStream(rawData);
	setText(readString(raw, getTextEncoding(raw)));
    }
    
    public boolean isOfRepeatableType() {
	return false;
    }
    
    public String getText() {
	return frameText;
    }
    
    public void setText(String frameText) {
	if (frameText == null)
	    throw new IllegalArgumentException
		      ("null text passed to TextFrame.setText()");
	if (!this.frameText.equals(frameText)) {
	    this.frameText = frameText;
	    setChanged(true);
	}
    }
}
