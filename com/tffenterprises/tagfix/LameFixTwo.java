/* LameFixTwo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v1;

public class LameFixTwo implements FileAction
{
    int counter = 0;
    RenameActionv2 renamer = new RenameActionv2();
    AddID3v1 adder = new AddID3v1();
    
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
	    ID3v1 tag = null;
	    if (file.hasID3v2() && file.hasID3v1())
		tag = file.getID3v1();
	    file.close();
	    if (tag == null)
		return false;
	    if (isLame(tag)) {
		System.out.println(f.getName() + " is lame!!");
		counter++;
		return true;
	    }
	}
	return false;
    }
    
    public boolean isLame(ID3v1 tag) {
	if (tag.getTitle().length() < 1 && tag.getArtist().length() < 1
	    && tag.getComment().length() > 0)
	    return true;
	return false;
    }
}
