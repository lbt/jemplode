/* TagUpdate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3Tag;
import com.tffenterprises.music.tag.ID3v2;
import com.tffenterprises.music.tag.id3v2.frame.TrackFrame;
import com.tffenterprises.tagfix.io.MP3Selector;

public class TagUpdate implements FileAction
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
	    ID3Tag tag = null;
	    if (file.hasID3v2())
		tag = file.getID3v2();
	    else if (file.hasID3v1())
		tag = file.getID3v1();
	    file.close();
	    if (tag != null) {
		if (tag instanceof ID3v2) {
		    ID3v2 t = (ID3v2) tag;
		    if (t.getVersion() > 767)
			return false;
		}
		System.out.println(f.getName());
		System.out.println(tag);
		ID3v2 newtag = new ID3v2(tag);
		if (newtag != null) {
		    act(f, newtag);
		    System.out.println(newtag);
		    return true;
		}
	    }
	}
	return false;
    }
    
    public void act(File f, ID3v2 tag) throws IOException {
	tag.setGenre((byte) -1);
	if (f.getParent() != null) {
	    String[] list = new File(f.getParent()).list(new MP3Selector());
	    TrackFrame frame = (TrackFrame) tag.getFrameOfType("TRCK");
	    if (frame != null) {
		try {
		    byte track = Byte.parseByte(f.getName().substring(0, 2));
		    frame.setTrack(track);
		    frame.setTotal((short) list.length);
		} catch (NumberFormatException numberformatexception) {
		    /* empty */
		}
	    }
	}
	long tic = System.currentTimeMillis();
	TaggedFile tf = new TaggedFile(f, TaggedFile.ReadWrite);
	tf.writeID3v2(tag, 3965);
	long toc = System.currentTimeMillis();
	tf.close();
	tf = null;
	counter++;
    }
}
