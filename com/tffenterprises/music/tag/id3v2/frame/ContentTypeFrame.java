/* ContentTypeFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.Serializable;

public class ContentTypeFrame extends TextFrame
    implements Serializable, Cloneable
{
    public static final String MY_ID = "TCON";
    public static final String REMIX_CODE = "RX";
    public static final String COVER_CODE = "CR";
    private byte genre = -1;
    private boolean isACover = false;
    private boolean isARemix = false;
    
    ContentTypeFrame() {
	super.getHeader().setFrameID("TCON");
    }
    
    public ContentTypeFrame(FrameHeader header) {
	super(header);
	header.setFrameID("TCON");
    }
    
    public synchronized Object clone() {
	ContentTypeFrame newFrame = (ContentTypeFrame) super.clone();
	if (newFrame != null) {
	    newFrame.genre = genre;
	    newFrame.isARemix = isARemix;
	    newFrame.isACover = isACover;
	}
	return newFrame;
    }
    
    public boolean equals(Object other) {
	if (!super.equals(other))
	    return false;
	ContentTypeFrame otherFrame = (ContentTypeFrame) other;
	if (otherFrame.genre != genre || otherFrame.isARemix != isARemix
	    || otherFrame.isACover != isACover)
	    return false;
	return true;
    }
    
    public void setText(String genreString) {
	super.setText(genreString);
	int openParenIndex = genreString.indexOf('(');
	int closeParenIndex = genreString.indexOf(')');
	if (openParenIndex != -1 && closeParenIndex != -1) {
	    try {
		String genreNumber = genreString.substring(openParenIndex + 1,
							   closeParenIndex);
		genre = Byte.parseByte(genreNumber);
	    } catch (NumberFormatException e) {
		genre = (byte) -1;
	    }
	}
	isARemix = genreString.indexOf("RX") > -1;
	isACover = genreString.indexOf("CR") > -1;
    }
    
    public byte getGenreAsByte() {
	return genre;
    }
    
    public void setGenreAsByte(byte newGenre) {
	genre = newGenre;
	updateString();
    }
    
    public boolean isContentRemix() {
	return isARemix;
    }
    
    public void setContentRemix(boolean remix) {
	isARemix = remix;
	updateString();
    }
    
    public boolean isContentCover() {
	return isACover;
    }
    
    public void setContentCover(boolean cover) {
	isACover = cover;
	updateString();
    }
    
    private void updateString() {
	StringBuffer sb = new StringBuffer();
	if (genre != -1)
	    sb.append("(" + Byte.toString(genre) + ")");
	if (isARemix)
	    sb.append("(RX)");
	if (isACover)
	    sb.append("(CR)");
	super.setText(sb.toString());
    }
}
