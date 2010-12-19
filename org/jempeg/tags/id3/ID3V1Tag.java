/* ID3V1Tag - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags.id3;
import java.io.IOException;

import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT8;
import com.inzyme.util.ReflectionUtils;

public class ID3V1Tag
{
    private static final int ID3V1_FIELD_LENGTH = 30;
    private CharArray mySignature = new CharArray(3);
    private CharArray myTitle = new CharArray(30);
    private CharArray myArtist = new CharArray(30);
    private CharArray myAlbum = new CharArray(30);
    private CharArray myYear = new CharArray(4);
    private CharArray myComment = new CharArray(30);
    private UINT8 myGenre = new UINT8();
    
    public CharArray getSignature() {
	return mySignature;
    }
    
    public CharArray getTitle() {
	return myTitle;
    }
    
    public CharArray getArtist() {
	return myArtist;
    }
    
    public CharArray getAlbum() {
	return myAlbum;
    }
    
    public CharArray getYear() {
	return myYear;
    }
    
    public CharArray getComment() {
	return myComment;
    }
    
    public UINT8 getGenre() {
	return myGenre;
    }
    
    public int getLength() {
	return (mySignature.getLength() + myTitle.getLength()
		+ myArtist.getLength() + myAlbum.getLength()
		+ myYear.getLength() + myComment.getLength()
		+ myGenre.getLength());
    }
    
    public void read(LittleEndianInputStream _is) throws IOException {
	mySignature.read(_is);
	myTitle.read(_is);
	myArtist.read(_is);
	myAlbum.read(_is);
	myYear.read(_is);
	myComment.read(_is);
	myGenre.read(_is);
    }
    
    public void write(LittleEndianOutputStream _os) throws IOException {
	mySignature.write(_os);
	myTitle.write(_os);
	myArtist.write(_os);
	myAlbum.write(_os);
	myYear.write(_os);
	myComment.write(_os);
	myGenre.write(_os);
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
