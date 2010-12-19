/* InferID3v2 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;

public class InferID3v2 implements FileAction
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
		file = new TaggedFile(f, TaggedFile.ReadOnly);
	    } catch (IOException e) {
		System.out
		    .println("(" + f.getName() + " failed to be opened)");
		return false;
	    }
	    com.tffenterprises.music.tag.ID3v1 tag = null;
	    if (!file.hasID3v2() && file.hasID3v1())
		tag = file.getID3v1();
	    file.close();
	    if (tag != null) {
		ID3v2 newtag = new ID3v2(tag);
		if (newtag != null) {
		    act(f, newtag);
		    return true;
		}
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
