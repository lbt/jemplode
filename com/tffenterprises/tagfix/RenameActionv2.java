/* RenameActionv2 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;

public class RenameActionv2 implements FileAction
{
    private boolean renameFiles = true;
    int fileCounter = 0;
    Vector needRenaming;
    
    public RenameActionv2(boolean renameFiles) {
	needRenaming = new Vector();
	this.renameFiles = renameFiles;
    }
    
    public RenameActionv2() {
	needRenaming = new Vector();
	if (!renameFiles && needRenaming.size() > 0) {
	    System.out.println(String.valueOf(needRenaming.size())
			       + " have wierd filenames.");
	    Enumeration e = needRenaming.elements();
	    while (e.hasMoreElements())
		System.out.println(e.nextElement());
	}
    }
    
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
	String suffix = f.getName().substring(f.getName().lastIndexOf('.'));
	String filename = MakeFilenameFromID3v2(tag, suffix);
	String hfsfilename = FilenameFix.GetHFSFilename(filename);
	if (!filename.equals(hfsfilename)) {
	    tag.setFrameTextForID("TOFN", filename);
	    file = new TaggedFile(f, TaggedFile.ReadWrite);
	    file.writeID3v2(tag);
	    file.close();
	}
	return FilenameFix.PerformOSXRename(f, filename);
    }
    
    public static String MakeFilenameFromID3v2(ID3v2 tag, String suffix) {
	String track = Byte.toString(tag.getTrackNumber());
	if (track.length() < 2)
	    track = "0" + track;
	String superTitle = tag.getFrameTextForID("TIT1");
	String title = tag.getTitle();
	if (superTitle.equals("")) {
	    if (title.charAt(title.length() - 1) != '.')
		return track + " " + title + suffix;
	    return track + " " + title + " " + suffix;
	}
	if (title.charAt(title.length() - 1) != '.')
	    return track + " " + superTitle + ": " + title + suffix;
	return track + " " + superTitle + ": " + title + " " + suffix;
    }
    
    public static String MakeCompilationFilename(ID3v2 tag, String suffix) {
	String track = Byte.toString(tag.getTrackNumber());
	if (track.length() < 2)
	    track = "0" + track;
	String collectionFN = MakeCollectionFilename(tag, suffix);
	return track + " " + collectionFN;
    }
    
    public static String MakeCollectionFilename(ID3v2 tag, String suffix) {
	String artist = tag.getArtist();
	String title = tag.getTitle();
	if (title.charAt(title.length() - 1) != '.')
	    return artist + " - " + title + suffix;
	return artist + " - " + title + " " + suffix;
    }
}
