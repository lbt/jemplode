/* AddTENC - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;
import com.tffenterprises.music.tag.id3v2.frame.TextFrame;

public class AddTENC implements FileAction
{
    private String tencString = "";
    
    public AddTENC() {
	ResourceBundle bundle = ResourceBundle.getBundle("personal");
	tencString = bundle.getString("TENC");
    }
    
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
		if (tag != null
		    && ID3v2.GetTagLength(file.getRAFile()) > 3500) {
		    TextFrame tf = (TextFrame) tag.getFrameOfType("TENC");
		    if (tf == null) {
			tf = (TextFrame) tag.getNewFrameOfType("TENC");
			tag.addFrame(tf);
		    }
		    tf.setText(tencString);
		    System.out.println(f.getName());
		    file.writeID3v2(tag);
		    result = true;
		}
	    }
	    file.close();
	}
	return result;
    }
}
