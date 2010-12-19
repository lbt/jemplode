/* CommentFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.Serializable;

import com.tffenterprises.io.ByteArrayInputStream;

public class CommentFrame extends TXXXFrame
    implements KeyedFrame, Serializable, Cloneable
{
    private static final String MY_ID = "COMM";
    private String frameLanguage = "eng";
    
    public CommentFrame() {
	super.getHeader().setFrameID("COMM");
    }
    
    public CommentFrame(FrameHeader header) throws IllegalArgumentException {
	super(header);
	header.setFrameID("COMM");
    }
    
    public CommentFrame(FrameHeader header, String languageID,
			String frameDescription,
			String frameValue) throws IllegalArgumentException {
	super(header, frameDescription, frameValue);
	if (languageID == null || languageID.length() != 3)
	    throw new IllegalArgumentException
		      ("An illegal language was passed to the CommentFrame constructor.");
	frameLanguage = languageID;
    }
    
    public synchronized Object clone() {
	CommentFrame newFrame = (CommentFrame) super.clone();
	newFrame.frameLanguage = frameLanguage;
	return newFrame;
    }
    
    public boolean equals(Object other) {
	if (!super.equals(other))
	    return false;
	CommentFrame otherFrame = (CommentFrame) other;
	if (otherFrame.frameLanguage == null
	    || !otherFrame.frameLanguage.equals(frameLanguage))
	    return false;
	return true;
    }
    
    public String toString() {
	String superstring = getHeader().toString();
	return (superstring + "\n" + "  Language ID = " + frameLanguage + "\n"
		+ "  Description = " + getDescription() + "\n"
		+ "  Value       = " + getValue());
    }
    
    public String getTextEncoding(ByteArrayInputStream in)
	throws FrameDataFormatException {
	String encoding = this.getTextEncoding((byte) in.read());
	byte[] languageBytes = new byte[3];
	in.read(languageBytes);
	frameLanguage = new String(languageBytes);
	return encoding;
    }
    
    public byte[] getTextEncoding(String s) throws FrameDataFormatException {
	try {
	    byte[] enc = super.getTextEncoding(s);
	    byte[] total = new byte[4];
	    System.arraycopy(enc, 0, total, 0, enc.length);
	    System.arraycopy(frameLanguage.getBytes(), 0, total, 1, 3);
	    return total;
	} catch (ArrayIndexOutOfBoundsException aoobe) {
	    throw new FrameDataFormatException
		      ("The encoding byte of the data passed to TextFrame.getEncoding() is invalid.");
	}
    }
}
