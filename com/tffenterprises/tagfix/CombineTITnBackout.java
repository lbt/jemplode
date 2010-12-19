/* CombineTITnBackout - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;

public class CombineTITnBackout implements FileAction
{
    public void finish() {
	/* empty */
    }
    
    public boolean performAction(File f) throws IOException {
	if (!f.isFile())
	    return false;
	TaggedFile file = null;
	try {
	    file = new TaggedFile(f, TaggedFile.ReadOnly);
	} catch (IOException e) {
	    System.out.println("\t(failed to be opened)");
	    return false;
	}
	ID3v2 tag = file.getID3v2();
	file.close();
	if (tag == null)
	    return false;
	String super_title = tag.getFrameTextForID("TIT1");
	String full_title = tag.getTitle();
	String sub_title = tag.getFrameTextForID("TIT3");
	if (super_title.trim().length() != 0)
	    super_title += ": ";
	if (sub_title.trim().length() != 0)
	    sub_title = ", " + sub_title;
	if (full_title.startsWith(super_title)
	    && full_title.endsWith(sub_title)) {
	    String title = full_title.substring(super_title.length(),
						(full_title.length()
						 - sub_title.length()));
	    tag.setTitle(title);
	    file = new TaggedFile(f, TaggedFile.ReadWrite);
	    file.writeID3v2(tag);
	    file.close();
	    return true;
	}
	return false;
    }
}
