/* AddID3v1 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v1;

public class AddID3v1 implements FileAction
{
    int numcomm = 0;
    int counter = 0;
    
    public void finish() {
	System.out.println("Fixed " + counter + " files.");
    }
    
    public boolean performAction(File f) throws IOException {
	boolean returned = false;
	if (f.isFile() && f.getName().indexOf(".mp3") != -1) {
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
	    if (file.hasID3v2()) {
		System.out.println(f.getPath());
		com.tffenterprises.music.tag.ID3v2 tag = file.getID3v2();
		file.writeID3v1(new ID3v1(tag));
		counter++;
		returned = true;
	    }
	    file.close();
	}
	return returned;
    }
}
