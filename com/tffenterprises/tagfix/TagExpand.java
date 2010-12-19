/* TagExpand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;

public class TagExpand implements FileAction
{
    int counter = 0;
    
    public void finish() {
	/* empty */
    }
    
    public boolean performAction(File f) throws IOException {
	if (f.isFile()) {
	    TaggedFile file = null;
	    try {
		file = new TaggedFile(f, TaggedFile.ReadWrite);
	    } catch (IOException e) {
		System.out
		    .println("(" + f.getName() + " failed to be opened)");
		if (file != null)
		    file.close();
		return false;
	    }
	    ID3v2 tag = null;
	    if (file.hasID3v2())
		tag = file.getID3v2();
	    int theLength = 2038;
	    if (tag != null && tag.getVersion() == 768
		&& (tag.getFileLength() < 2038
		    || (tag.getFileLength() > 2038
			&& tag.getMinimalLength() < 2038))) {
		System.out.println(f.getPath());
		file.writeID3v2(tag, 2048);
		file.close();
		return true;
	    }
	    file.close();
	}
	return false;
    }
}
