/* TrackFrame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2.frame;
import java.io.Serializable;

public class TrackFrame extends FractionFrame
    implements Serializable, Cloneable
{
    public static final String MY_ID = "TRCK";
    public static final String PART_NAME = "track";
    
    public TrackFrame() {
	super.getHeader().setFrameID("TRCK");
    }
    
    public TrackFrame(FrameHeader header) {
	super(header);
	header.setFrameID("TRCK");
    }
    
    public TrackFrame(FrameHeader header, short track, short total)
	throws IllegalArgumentException {
	super(header, track, total);
	header.setFrameID("TRCK");
    }
    
    public short getTrack() {
	return getItem();
    }
    
    public void setTrack(short t) {
	setItem(t);
    }
    
    public String getPartName() {
	return "track";
    }
}
