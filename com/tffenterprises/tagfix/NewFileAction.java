/* NewFileAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v1;
import com.tffenterprises.music.tag.ID3v2;
import com.tffenterprises.music.tag.id3v2.frame.TextFrame;

public class NewFileAction implements FileAction
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
	    if (file.hasID3v2() && !file.hasID3v1()) {
		ID3v2 tag = file.getID3v2();
		file.close();
		if (tag != null) {
		    addFrames(tag);
		    act(f, tag);
		    return true;
		}
	    } else
		file.close();
	}
	return false;
    }
    
    public void addFrames(ID3v2 tag) {
	TextFrame tf = (TextFrame) tag.getNewFrameOfType("TSSE");
	tf.setText("AudioCatalyst 2.1 for MacOS, VBR (normal/high)");
	tag.addFrame(tf);
    }
    
    public void act(File f, ID3v2 tag) throws IOException {
	boolean debug = false;
	String track = Byte.toString(tag.getTrackNumber());
	String title = tag.getTitle();
	if (track.length() < 2)
	    track = "0" + track;
	String suffix = f.getName().substring(f.getName().lastIndexOf('.'));
	StringTokenizer st
	    = new StringTokenizer(track + " " + title + suffix, "/");
	StringBuffer sb = new StringBuffer(st.nextToken());
	while (st.hasMoreTokens())
	    sb.append(":").append(st.nextToken());
	System.out.print(sb);
	long tic = System.currentTimeMillis();
	TaggedFile tf = new TaggedFile(f, TaggedFile.ReadWrite);
	tf.writeID3v2(tag, 3965);
	ID3v1 oldtag = new ID3v1(tag);
	tf.writeID3v1(oldtag);
	long toc = System.currentTimeMillis();
	System.out.println(" written in " + (toc - tic) + " ms.");
	tf.close();
	tf = null;
	counter++;
	File newFile = new File(f.getParent(), sb.toString());
	f.renameTo(newFile);
    }
}
