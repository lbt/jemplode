/* DisplayAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.tffenterprises.music.tag.ID3v1;
import com.tffenterprises.music.tag.ID3v2;
import com.tffenterprises.music.tag.TagDataFormatException;
import com.tffenterprises.tagfix.io.MP3Selector;

public class DisplayAction implements FileAction
{
    public boolean performAction(File f) throws IOException {
	System.out.println(f.getPath());
	FileInputStream fis = new FileInputStream(f);
	try {
	    if (fis.read() == 73 && fis.read() == 68 && fis.read() == 51) {
		ID3v2 tag = new ID3v2(fis);
		System.out.println(tag);
		return true;
	    }
	} catch (TagDataFormatException e) {
	    System.out.println("\t(invalid ID3v2 tag)");
	}
	fis.skip((long) (fis.available() - 128));
	if (fis.read() == 84 && fis.read() == 65 && fis.read() == 71) {
	    ID3v1 old = new ID3v1(fis);
	    System.out.println(old);
	    return true;
	}
	return false;
    }
    
    public void finish() {
	/* empty */
    }
    
    public static void main(String[] argv) {
	Recurse.filter = new MP3Selector();
	Recurse.actions.add(new DisplayAction());
	try {
	    for (int i = 0; i < argv.length; i++)
		Recurse.Recurse(new File(argv[i]));
	} catch (Exception e) {
	    System.err.println(e + ": " + e.getMessage());
	    e.printStackTrace(System.err);
	}
    }
}
