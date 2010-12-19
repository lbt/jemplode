/* RenameActionv1 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v1;

public class RenameActionv1 implements FileAction
{
    public void finish() {
	/* empty */
    }
    
    public boolean performAction(File f) throws IOException {
	if (f.isFile() && f.getName().indexOf(".mp3") != -1) {
	    TaggedFile file = null;
	    try {
		file = new TaggedFile(f, TaggedFile.ReadOnly);
	    } catch (IOException e) {
		System.out.println("\t(failed to be opened)");
		return false;
	    }
	    if (file.hasID3v1()) {
		ID3v1 tag = file.getID3v1();
		file.close();
		if (tag != null) {
		    String title = tag.getTitle();
		    if (title.length() < 29) {
			System.out.print(f.getName());
			String newName
			    = f.getName().substring(0, 3) + title + ".mp3";
			File nf = new File(f.getParent(), newName);
			if (f.renameTo(nf)) {
			    System.out
				.println(" was renamed to: " + nf.getName());
			    return true;
			}
			System.out.println("Failure to rename");
			return false;
		    }
		}
	    }
	}
	return false;
    }
}
