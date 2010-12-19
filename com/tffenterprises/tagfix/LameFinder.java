/* LameFinder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;
import com.tffenterprises.music.tag.id3v2.frame.TextFrame;

public class LameFinder implements FileAction
{
    int counter = 0;
    
    public void finish() {
	System.out
	    .println(Integer.toString(counter) + " lame files were found!");
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
	    if (!file.hasID3v2() || file.hasID3v1()) {
		file.close();
		return false;
	    }
	    ID3v2 tag = file.getID3v2();
	    file.close();
	    if (isLame(tag)) {
		System.out.println(f.getName());
		counter++;
		return true;
	    }
	    return false;
	}
	return false;
    }
    
    public boolean isLame(ID3v2 tag) {
	if (tag != null) {
	    com.tffenterprises.music.tag.id3v2.Frame f
		= tag.getFrameOfType("COMM");
	    if (f instanceof TextFrame)
		return ((TextFrame) f).getText().startsWith("LAME");
	}
	return false;
    }
}
