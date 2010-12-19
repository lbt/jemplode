/* CombineTPEnBackout - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;

public class CombineTPEnBackout implements FileAction
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
	String long_artist = tag.getArtist();
	String orchestra = tag.getFrameTextForID("TPE2");
	String conductor = tag.getFrameTextForID("TPE3");
	String remix = tag.getFrameTextForID("TPE4");
	if (orchestra.trim().length() != 0)
	    orchestra = ". " + orchestra;
	if (conductor.trim().length() != 0)
	    conductor = ", " + conductor;
	if (remix.trim().length() != 0)
	    remix = " (" + remix + ") ";
	if (long_artist.endsWith(orchestra + conductor + remix)) {
	    String artist = long_artist.substring(0, (long_artist.length()
						      - orchestra.length()
						      - conductor.length()
						      - remix.length()));
	    tag.setArtist(artist);
	    file = new TaggedFile(f, TaggedFile.ReadWrite);
	    file.writeID3v2(tag);
	    file.close();
	    return true;
	}
	return false;
    }
}
