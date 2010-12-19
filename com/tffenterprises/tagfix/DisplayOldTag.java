/* DisplayOldTag - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.tffenterprises.music.tag.ID3v1;

public class DisplayOldTag implements FileAction
{
    public boolean performAction(File f) throws IOException {
	System.out.println(f.getPath());
	long offset = ID3v1.GetTagOffset(new RandomAccessFile(f, "r"));
	FileInputStream fis = new FileInputStream(f);
	fis.skip(offset);
	ID3v1 tag = new ID3v1(fis);
	if (tag != null) {
	    System.out.println(tag);
	    return true;
	}
	return false;
    }
    
    public void finish() {
	/* empty */
    }
}
