/* LameFixOne - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;
import com.tffenterprises.music.tag.id3v2.frame.TextFrame;
import com.tffenterprises.tagfix.io.MP3Selector;

public class LameFixOne implements FileAction
{
    int numcomm = 0;
    int counter = 0;
    
    public void finish() {
	/* empty */
    }
    
    public boolean performAction(File f) throws IOException {
	if (f.isFile() && f.getName().indexOf(".mp3") != -1) {
	    TaggedFile file = null;
	    try {
		file = new TaggedFile(f, TaggedFile.ReadOnly);
	    } catch (IOException e) {
		System.out
		    .println("(" + f.getName() + " failed to be opened)");
		return false;
	    }
	    if (!file.hasID3v2()) {
		file.close();
		return false;
	    }
	    ID3v2 tag = file.getID3v2();
	    file.close();
	    if (isLame(tag)) {
		System.out.println(f.getName());
		addFrames(f, tag);
		act(f, tag);
		System.out.println(tag);
		counter++;
		return true;
	    }
	    return false;
	}
	return false;
    }
    
    public boolean isLame(ID3v2 tag) {
	if (tag != null && tag.getTitle().length() < 1
	    && tag.getArtist().length() < 1) {
	    com.tffenterprises.music.tag.id3v2.Frame f
		= tag.getFrameOfType("TSSE");
	    if (f instanceof TextFrame)
		return ((TextFrame) f).getText().startsWith("LAME");
	}
	return false;
    }
    
    public void addFrames(File f, ID3v2 tag) {
	File file = f;
	try {
	    file = new File(f.getCanonicalPath());
	} catch (IOException e) {
	    file = f;
	}
	String fname = file.getName();
	if (file.getParent() != null) {
	    String[] list = new File(file.getParent()).list(new MP3Selector());
	    try {
		String substring;
		if (Character.isDigit(fname.charAt(1)))
		    substring = fname.substring(0, 2);
		else
		    substring = fname.substring(0, 1);
		byte track = Byte.parseByte(substring);
		String trackString = (Integer.toString(track) + "/"
				      + Integer.toString(list.length));
		tag.setFrameTextForID("TRCK", trackString);
	    } catch (NumberFormatException e) {
		System.out.println(e);
	    }
	}
	if (fname.indexOf(".mp3") > 2) {
	    String title = fname.substring(2, fname.indexOf(".mp3")).trim();
	    if (title.length() > 0)
		tag.setTitle(title);
	}
	try {
	    ResourceBundle bundle = ResourceBundle.getBundle("recurse");
	    tag.setFrameTextForID("TYER", bundle.getString("year"));
	} catch (MissingResourceException missingresourceexception) {
	    /* empty */
	}
	System.out.println(f.getParent());
	File parent = new File(file.getParent());
	try {
	    parent = new File(parent.getCanonicalPath());
	} catch (IOException e) {
	    parent = new File(file.getParent());
	}
	String dname = parent.getName();
	String album = dname;
	try {
	    short number = Short.parseShort(dname.substring(0, 3));
	    album = dname.substring(4).trim();
	} catch (NumberFormatException numberformatexception) {
	    /* empty */
	}
	if (album.length() > 0)
	    tag.setAlbum(album);
	String oname = new File(parent.getParent()).getName();
	String artist = oname;
	if (artist.length() > 0)
	    tag.setArtist(artist);
	tag.setGenre((byte) -1);
    }
    
    public void act(File f, ID3v2 tag) throws IOException {
	TaggedFile tf = new TaggedFile(f, TaggedFile.ReadWrite);
	tf.writeID3v2(tag, 2048);
	tf.close();
	tf = null;
	counter++;
    }
}
