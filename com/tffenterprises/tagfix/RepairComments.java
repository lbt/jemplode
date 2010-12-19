/* RepairComments - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;
import com.tffenterprises.music.tag.id3v2.frame.CommentFrame;
import com.tffenterprises.music.tag.id3v2.frame.KeyedContainer;

public class RepairComments implements FileAction
{
    public void finish() {
	/* empty */
    }
    
    public boolean performAction(File f) throws IOException {
	boolean result = false;
	if (f.isFile() && f.getName().indexOf(".mp3") != -1) {
	    TaggedFile file = null;
	    try {
		file = new TaggedFile(f, TaggedFile.ReadWrite);
	    } catch (IOException e) {
		System.out
		    .println("(" + f.getName() + " failed to be opened)");
		return false;
	    }
	    if (file.hasID3v2()) {
		ID3v2 tag = file.getID3v2();
		System.out.println("Original tag:\n" + tag);
		com.tffenterprises.music.tag.id3v2.Frame frame
		    = tag.getFrameOfType("COMM");
		KeyedContainer container = null;
		CommentFrame commentFrame = null;
		if (frame instanceof KeyedContainer) {
		    container = (KeyedContainer) frame;
		    Enumeration frameEnum = container.frames();
		    while (frameEnum.hasMoreElements())
			fixCommentFrame((CommentFrame)
					frameEnum.nextElement());
		} else if (frame instanceof CommentFrame)
		    fixCommentFrame((CommentFrame) frame);
		System.out.println("New tag:\n" + tag);
		file.writeID3v2(tag);
		result = true;
	    }
	    file.close();
	}
	return result;
    }
    
    public void fixCommentFrame(CommentFrame frame) {
	if (frame != null) {
	    String description = frame.getDescription();
	    String value = frame.getValue();
	    if (description.length() > 0) {
		String newValue = description + value;
		frame.setDescription("");
		frame.setValue(newValue);
	    }
	}
    }
}
