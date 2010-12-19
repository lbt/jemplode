/* tagfixv1 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.SmartID3v1;

public class tagfixv1
{
    static int fileCounter = 0;
    static int badV1Counter = 0;
    static int badTrackCounter = 0;
    static int id3v1Counter = 0;
    static int multiV1Counter = 0;
    static int changedCounter = 0;
    static String openmode = TaggedFile.ReadWrite;
    static boolean verbose = false;
    static boolean renumber = false;
    static boolean setyear = false;
    static short newyear = -1;
    
    public static void main(String[] argv) throws Exception {
	String fn = ParseOptions(argv);
	if (fn != null) {
	    File f = new File(fn);
	    Recurse(f);
	    System.out.println(String.valueOf(fileCounter) + " files.");
	    System.out.println(String.valueOf(id3v1Counter) + " ID3 tags.");
	    System.out.println(String.valueOf(badV1Counter)
			       + " incorrect genre bytes.");
	    System.out.println(String.valueOf(badTrackCounter)
			       + " files renumbered.");
	    System.out.println(String.valueOf(multiV1Counter)
			       + " files have multiple ID3 tags.");
	    System.out.println(String.valueOf(changedCounter)
			       + " files were changed.");
	}
    }
    
    private static String ParseOptions(String[] argv) throws Exception {
	if (argv.length < 1) {
	    System.out.println
		("Usage: java tagfixv1 [ -r ] [ -n ] [-y nnnn | -cy ] <filename(s)>\n\n-r\tRead-Only\n-n\tParse track numbers from filenames\n-y nnnn\tSet year to nnnn\n-cy\tClear year in tags\n");
	    return "";
	}
	short i = 0;
	for (i = (short) 0; i < argv.length - 1; i++) {
	    if (argv[i].equals("-r")) {
		openmode = TaggedFile.ReadOnly;
		verbose = true;
	    } else if (argv[i].equals("-n"))
		renumber = true;
	    else if (argv[i].equals("-y")) {
		setyear = true;
		i++;
		newyear = Short.parseShort(argv[i]);
	    } else if (argv[i].equals("-cy")) {
		setyear = true;
		newyear = (short) -1;
	    } else if (argv[i].equals("-v"))
		verbose = true;
	}
	return argv[i];
    }
    
    private static boolean Recurse(File f) throws Exception {
	if (f.isDirectory()) {
	    String[] list = f.list();
	    boolean hasFiles = false;
	    for (int i = 0; i < list.length; i++) {
		File sub = new File(f, list[i]);
		hasFiles = Recurse(sub) || hasFiles;
	    }
	    if (!hasFiles)
		System.out.println(f.getPath());
	    return false;
	}
	if (f.isFile() && f.getName().indexOf(".mp3") != -1) {
	    TaggedFile file = null;
	    try {
		file = new TaggedFile(f, openmode);
	    } catch (IOException e) {
		System.out.println("\t(failed to be opened)");
		return false;
	    }
	    fileCounter++;
	    if (file.hasID3v1()) {
		id3v1Counter++;
		com.tffenterprises.music.tag.ID3v1 tag
		    = new SmartID3v1(file.getID3v1());
		if (tag != null) {
		    if (tag.isChanged())
			badV1Counter++;
		    if (renumber) {
			try {
			    Byte trackNo
				= new Byte(f.getName().substring(0, 2));
			    byte newn = trackNo.byteValue();
			    if (newn < 100) {
				byte oldn = tag.getTrackNumber();
				if (oldn != newn) {
				    tag.setTrackNumber(newn);
				    badTrackCounter++;
				}
			    }
			} catch (NumberFormatException numberformatexception) {
			    /* empty */
			}
		    }
		    if (setyear)
			tag.setYear(newyear);
		    if (openmode.equals(TaggedFile.ReadWrite)
			&& tag.isChanged()) {
			file.writeID3v1(tag);
			changedCounter++;
		    }
		    if (verbose) {
			System.out.println(f.getName() + "\n");
			System.out.println(tag + "\n");
		    }
		}
	    }
	    file.close();
	    return true;
	}
	return false;
    }
}
