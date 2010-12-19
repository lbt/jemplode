/* CommentFinder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;
import com.tffenterprises.music.tag.id3v2.frame.CommentFrame;
import com.tffenterprises.music.tag.id3v2.frame.Container;

public class CommentFinder implements FileAction
{
    int counter = 0;
    
    public void finish() {
	if (counter == 0)
	    System.out.print("No");
	else
	    System.out.print(Integer.toString(counter));
	System.out.println(" malformed comments were found!");
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
	    com.tffenterprises.music.tag.id3v2.Frame cf
		= tag.getFrameOfType("COMM");
	    if (cf != null) {
		CommentFrame tcf = null;
		if (cf instanceof Container) {
		    Container ccf = (Container) cf;
		    Enumeration frames = ccf.frames();
		    if (frames.hasMoreElements())
			tcf = (CommentFrame) frames.nextElement();
		} else if (cf instanceof CommentFrame)
		    tcf = (CommentFrame) cf;
		if (tcf.getValue() == "") {
		    System.out.println(f.getPath());
		    tcf.setValue(tcf.getDescription());
		    tcf.setDescription("");
		    file = new TaggedFile(f, TaggedFile.ReadWrite);
		    file.writeID3v2(tag);
		    file.close();
		    counter++;
		    return true;
		}
	    }
	    return false;
	}
	return false;
    }
}
