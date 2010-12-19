/* FixTracks - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;
import com.tffenterprises.music.tag.id3v2.frame.TrackFrame;
import com.tffenterprises.tagfix.io.MP3Selector;

public class FixTracks implements FileAction
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
		file = new TaggedFile(f, TaggedFile.ReadWrite);
	    } catch (IOException e) {
		System.out
		    .println("(" + f.getName() + " failed to be opened)");
		return false;
	    }
	    ID3v2 tag = null;
	    if (file.hasID3v2())
		tag = file.getID3v2();
	    if (tag != null) {
		TrackFrame frame = (TrackFrame) tag.getFrameOfType("TRCK");
		if (frame == null) {
		    frame = (TrackFrame) tag.getNewFrameOfType("TRCK");
		    tag.addFrame(frame);
		}
		String[] list
		    = new File(f.getParent()).list(new MP3Selector());
		try {
		    if (frame.getTrack() == -1) {
			String substring;
			if (Character.isDigit(f.getName().charAt(1)))
			    substring = f.getName().substring(0, 2);
			else
			    substring = f.getName().substring(0, 1);
			byte track = Byte.parseByte(substring);
			frame.setTrack(track);
		    }
		    frame.setTotal((short) list.length);
		} catch (NumberFormatException e) {
		    tag.removeFrame(frame);
		    System.out.println(e);
		}
		file.writeID3v2(tag);
		file.close();
		return true;
	    }
	}
	return false;
    }
    
    public void act(File f, ID3v2 tag) throws IOException {
	tag.setGenre((byte) -1);
	long tic = System.currentTimeMillis();
	TaggedFile tf = new TaggedFile(f, TaggedFile.ReadWrite);
	tf.writeID3v2(tag, 3965);
	long toc = System.currentTimeMillis();
	tf.close();
	tf = null;
	counter++;
    }
}
