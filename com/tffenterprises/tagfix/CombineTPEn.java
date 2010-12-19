/* CombineTPEn - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;

public class CombineTPEn implements FileAction
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
	String artist = tag.getFrameTextForID("TPE1");
	String orchestra = tag.getFrameTextForID("TPE2");
	String conductor = tag.getFrameTextForID("TPE3");
	String remix = tag.getFrameTextForID("TPE4");
	if (orchestra.trim().length() != 0)
	    orchestra = ". " + orchestra;
	if (conductor.trim().length() != 0)
	    conductor = ", " + conductor;
	if (remix.trim().length() != 0)
	    remix = " (" + remix + ") ";
	String long_artist = artist + orchestra + conductor + remix;
	if (artist.endsWith(long_artist))
	    return false;
	tag.setArtist(long_artist);
	file = new TaggedFile(f, TaggedFile.ReadWrite);
	file.writeID3v2(tag);
	file.close();
	return true;
    }
}
