/* FilenameFix - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;

public class FilenameFix implements FileAction
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
	if (!file.hasID3v2())
	    return false;
	ID3v2 tag = file.getID3v2();
	file.close();
	if (tag == null)
	    return false;
	String filename = tag.getFrameTextForID("TOFN");
	if (filename == null || filename.equals(""))
	    return false;
	return PerformOSXRename(f, filename);
    }
    
    public static boolean PerformOSXRename(File f, String filename) {
	String hfsName = GetHFSFilename(filename);
	if (hfsName.equals(f.getName()))
	    return false;
	File nf = new File(f.getParent(), hfsName);
	if (f.renameTo(nf)) {
	    System.out
		.println(f.getName() + " was renamed to: " + nf.getName());
	    return true;
	}
	System.out.println("Failure to rename " + f.getName());
	return false;
    }
    
    public static String GetHFSFilename(String filename) {
	StringTokenizer st = new StringTokenizer(filename, ":");
	StringBuffer sb = new StringBuffer(st.nextToken());
	while (st.hasMoreTokens())
	    sb.append("-").append(st.nextToken());
	st = new StringTokenizer(sb.toString(), "/");
	sb = new StringBuffer(st.nextToken());
	while (st.hasMoreTokens())
	    sb.append(":").append(st.nextToken());
	return sb.toString();
    }
}
