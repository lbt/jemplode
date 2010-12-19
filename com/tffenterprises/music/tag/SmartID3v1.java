/* SmartID3v1 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag;
import java.io.Serializable;

public class SmartID3v1 extends ID3v1 implements ID3Tag, Serializable
{
    public SmartID3v1(byte[] byteArray) throws IllegalArgumentException {
	super(byteArray);
	if (super.getGenre() != -1)
	    super.setGenre((byte) -1);
    }
    
    public SmartID3v1() {
	/* empty */
    }
    
    public SmartID3v1(ID3Tag oldTag) {
	super.setTitle(oldTag.getTitle());
	super.setArtist(oldTag.getArtist());
	super.setAlbum(oldTag.getAlbum());
	super.setComment(oldTag.getComment());
	super.setYear(oldTag.getYear());
	super.setTrackNumber(oldTag.getTrackNumber());
	super.setGenre((byte) -1);
	if (oldTag.getGenre() != -1)
	    super.setChanged(true);
	else
	    super.setChanged(false);
    }
    
    public SmartID3v1(ID3v1 oldTag) {
	this((ID3Tag) oldTag);
    }
    
    public byte getGenre() {
	return (byte) -1;
    }
    
    public void setGenre(byte genre) {
	super.setGenre((byte) -1);
    }
}
