/* CountFormats - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;
import com.tffenterprises.music.tag.id3v2.frame.CommentFrame;
import com.tffenterprises.music.tag.id3v2.frame.Container;

public class CountFormats implements FileAction
{
    int fileCounter = 0;
    Vector shortv2Tags = new Vector();
    Vector longv2Tags = new Vector();
    Vector badv2List = new Vector();
    Vector nov2List = new Vector();
    Vector areLame = new Vector();
    Vector areMonoLame = new Vector();
    Vector areXing = new Vector();
    Vector noTSSE = new Vector();
    Vector hasComment = new Vector();
    Vector badComment = new Vector();
    Vector multiComment = new Vector();
    Vector unsynchronized = new Vector();
    
    public void finish() {
	System.out.println(String.valueOf(fileCounter) + " files.");
	if (nov2List.size() > 0) {
	    System.out.println(String.valueOf(nov2List.size())
			       + " have no ID3v2.x tag.");
	    Enumeration e = nov2List.elements();
	    while (e.hasMoreElements())
		System.out.println(e.nextElement());
	}
	if (badv2List.size() > 0) {
	    System.out.println(String.valueOf(badv2List.size())
			       + " have obsolete ID3v2.x tags.");
	    Enumeration e = badv2List.elements();
	    while (e.hasMoreElements())
		System.out.println(e.nextElement());
	}
	if (shortv2Tags.size() > 0) {
	    System.out.println(String.valueOf(shortv2Tags.size())
			       + " have short ID3v2.x tags.");
	    Enumeration e = shortv2Tags.elements();
	    while (e.hasMoreElements())
		System.out.println(e.nextElement());
	}
	if (longv2Tags.size() > 0)
	    System.out.println(String.valueOf(longv2Tags.size())
			       + " have long ID3v2.x tags.");
	if (hasComment.size() > 0)
	    System.out.println(String.valueOf(hasComment.size())
			       + " have a comment.");
	if (unsynchronized.size() > 0) {
	    System.out.println(String.valueOf(unsynchronized.size())
			       + " are unsynchronized.");
	    Enumeration e = unsynchronized.elements();
	    while (e.hasMoreElements())
		System.out.println(e.nextElement());
	}
	if (badComment.size() > 0)
	    System.out.println(String.valueOf(badComment.size())
			       + " have a malformed comment.");
	if (multiComment.size() > 0)
	    System.out.println(String.valueOf(multiComment.size())
			       + " have multiple comments.");
	if (areLame.size() > 0)
	    System.out.println(String.valueOf(areLame.size())
			       + " were encoded using LAME.");
	if (areMonoLame.size() > 0) {
	    System.out.println(String.valueOf(areMonoLame.size())
			       + " are mono files.");
	    Enumeration enumeration = areMonoLame.elements();
	}
	if (areXing.size() > 0)
	    System.out.println(String.valueOf(areXing.size())
			       + " were encoded with AudioCatalyst.");
	if (noTSSE.size() > 0)
	    System.out.println(String.valueOf(noTSSE.size())
			       + " have no encoder settings info.");
    }
    
    public boolean performAction(File f) {
	try {
	    if (f.isFile() && f.getName().indexOf(".mp3") != -1) {
		TaggedFile file = null;
		try {
		    file = new TaggedFile(f, TaggedFile.ReadOnly);
		} catch (IOException e) {
		    System.out.println(f.getPath() + " (failed to be opened)");
		    if (file != null)
			file.close();
		    return false;
		}
		fileCounter++;
		byte[] version
		    = ID3v2.CheckFileForTagVersion(file.getRAFile());
		if (version != null) {
		    if (version[0] != 3)
			badv2List.addElement(f.getPath());
		    if (ID3v2.GetTagLength(file.getRAFile()) < 2045)
			shortv2Tags.addElement(f.getPath());
		    else if (ID3v2.GetTagLength(file.getRAFile()) > 2045
			     && ID3v2.GetTagLength(file.getRAFile()) < 4100)
			longv2Tags.addElement(f.getPath());
		    ID3v2 tag = null;
		    try {
			tag = file.getID3v2();
		    } catch (Exception e) {
			System.out.println(f.getPath());
			return false;
		    }
		    if (tag.getFlags().usesUnsynchronization())
			unsynchronized.addElement(f.getPath());
		    String encoder = tag.getFrameTextForID("TSSE");
		    if (encoder.indexOf("LAME") == 0
			|| encoder.indexOf("lame") == 0) {
			areLame.addElement(f.getPath());
			if (encoder.indexOf("-m m") >= 0)
			    areMonoLame.addElement(f.getPath());
		    } else if (encoder.indexOf("AudioCatalyst") == 0)
			areXing.addElement(f.getPath());
		    else
			noTSSE.addElement(f.getPath());
		    com.tffenterprises.music.tag.id3v2.Frame cf
			= tag.getFrameOfType("COMM");
		    if (cf != null) {
			hasComment.addElement(f.getPath());
			CommentFrame tcf = null;
			if (cf instanceof Container) {
			    Container ccf = (Container) cf;
			    if (ccf.size() > 1)
				multiComment.addElement(f.getPath());
			    Enumeration frames = ccf.frames();
			    if (frames.hasMoreElements())
				tcf = (CommentFrame) frames.nextElement();
			} else if (cf instanceof CommentFrame)
			    tcf = (CommentFrame) cf;
			if (tcf.getValue() == "")
			    badComment.addElement(f.getPath());
		    }
		} else
		    nov2List.addElement(f.getPath());
		file.close();
		return true;
	    }
	    return false;
	} catch (Exception e) {
	    System.err.println("Error while reading file " + f.getPath());
	    e.printStackTrace(System.err);
	    return false;
	}
    }
}
