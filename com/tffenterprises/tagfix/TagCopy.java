/* TagCopy - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;
import com.tffenterprises.music.tag.id3v2.Frame;
import com.tffenterprises.tagfix.io.MP3Selector;

public class TagCopy
{
    private static String[] preservedFrameTypes = { "TSSE", "ASPI", "TENC" };
    
    public static void main(String[] files) {
	if (files.length == 2) {
	    File source = new File(files[0]);
	    File dest = new File(files[1]);
	    if (!source.equals(dest)) {
		if (source.isFile() && dest.isFile())
		    CopyTag(source, dest);
		else if (source.isDirectory() && dest.isDirectory())
		    TwoDirectoryTagCopy(source, dest);
	    }
	}
    }
    
    public static void TwoDirectoryTagCopy(File d1, File d2) {
	File[] list1 = d1.listFiles(new MP3Selector());
	File[] list2 = d2.listFiles(new MP3Selector());
	if (list1.length == list2.length) {
	    Arrays.sort(list1);
	    Arrays.sort(list2);
	    for (int i = 0; i < list1.length; i++) {
		System.out.println(list1[i]);
		System.out.println(list2[i]);
		CopyTag(list1[i], list2[i]);
	    }
	}
    }
    
    public static void CopyTag(File source, File dest) {
	try {
	    TaggedFile from = new TaggedFile(source, TaggedFile.ReadOnly);
	    TaggedFile to = new TaggedFile(dest, TaggedFile.ReadWrite);
	    ID3v2 original = from.getID3v2();
	    ID3v2 overwritten = to.getID3v2();
	    RemovePreservedFrames(original);
	    PreservePreservedFrames(overwritten, original);
	    String suf
		= dest.getName().substring(dest.getName().lastIndexOf('.'));
	    String newName
		= RenameActionv2.MakeFilenameFromID3v2(original, suf);
	    String hfsName = FilenameFix.GetHFSFilename(newName);
	    if (!hfsName.equals(newName))
		original.setFrameTextForID("TOFN", newName);
	    try {
		int len = ID3v2.GetTagLength(from.getRAFile());
		to.writeID3v2(original, len + "ID3".length());
	    } catch (IOException ioe) {
		to.writeID3v2(original);
	    }
	    ID3v2 written = to.getID3v2();
	    from.close();
	    to.close();
	    System.out.println(written);
	    File nf = new File(dest.getParent(), hfsName);
	    if (dest.renameTo(nf))
		System.out
		    .println(dest.getName() + " renamed as " + nf.getName());
	} catch (Exception e) {
	    System.err.println(e + ": " + e.getMessage());
	    e.printStackTrace(System.err);
	}
    }
    
    public static void RemovePreservedFrames(ID3v2 tag) {
	for (int i = 0; i < preservedFrameTypes.length; i++) {
	    Frame oldf = tag.getFrameOfType(preservedFrameTypes[i]);
	    if (oldf != null)
		tag.removeFrame(oldf);
	}
    }
    
    public static void PreservePreservedFrames(ID3v2 overwritten,
					       ID3v2 original) {
	if (overwritten != null) {
	    for (int i = 0; i < preservedFrameTypes.length; i++) {
		Frame newf
		    = overwritten.getFrameOfType(preservedFrameTypes[i]);
		if (newf != null)
		    original.addFrame(newf);
	    }
	}
    }
}
