/* tagreadv2 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3v2;

public class tagreadv2
{
    public static void main(String[] argv) throws Exception {
	boolean copy = false;
	boolean rewrite = false;
	String openmode = TaggedFile.ReadOnly;
	boolean renumber = false;
	boolean setyear = false;
	boolean verbose = false;
	short newyear = -1;
	byte tracks = 0;
	if (argv.length >= 1) {
	    for (int i = 0; i < argv.length; i++) {
		File f = new File(argv[i]);
		System.out.println("File:\t" + f.getName());
		if (f.isDirectory()) {
		    String[] filelist = f.list();
		    for (int j = 0; j < filelist.length; j++) {
			File sub = new File(f, filelist[j]);
			if (sub.isFile())
			    System.out.println(filelist[j] + " can be opened");
			else
			    System.out
				.println(filelist[j] + " can't be opened");
		    }
		} else if (f.getName().indexOf("Icon") == -1) {
		    TaggedFile file = null;
		    try {
			file = new TaggedFile(f, openmode);
		    } catch (IOException e) {
			System.out.println("\t(failed to be opened)");
			file = null;
			continue;
		    }
		    parseXingFrame(file);
		    if (file.hasID3v2()) {
			System.out.println("The file has an ID3v2 tag:");
			long tic = System.currentTimeMillis();
			ID3v2 tag = file.getID3v2();
			long toc = System.currentTimeMillis();
			System.out.println(tag.toString());
			System.out.println("Read tag in " + (toc - tic)
					   + " milliseconds");
		    }
		    System.out.println();
		    file.close();
		}
	    }
	}
    }
    
    public static void parseXingFrame(TaggedFile file) throws IOException {
	RandomAccessFile raf = file.getRAFile();
	long cur = raf.getFilePointer();
	if (file.hasID3v2())
	    raf.seek(ID3v2.GetTagOffset(raf) + (long) ID3v2.GetTagLength(raf));
	else
	    raf.seek(0L);
	java.io.DataInput di = raf;
	if ((di.readUnsignedByte() & 0xff) == 255) {
	    int h2 = di.readUnsignedByte();
	    if ((h2 & 0xe0) == 224) {
		if ((h2 & 0x8) == 8)
		    System.out.print("MPEG-1");
		else
		    System.out.print("MPEG-2");
		int frequency = (di.readByte() & 0xc) >> 2;
		if (frequency < 4) {
		    int[] table = { 44100, 48000, 32000, 99999 };
		    System.out.print(" " + table[frequency] + " Hz, ");
		}
		int mode = (di.readByte() & 0xc0) >> 6;
		if (mode < 4) {
		    String[] table
			= { "stereo", "joint stereo", "dual channel", "mono" };
		    System.out.println(table[mode]);
		}
		if ((h2 & 0x8) == 8) {
		    if (mode != 3)
			di.skipBytes(32);
		    else
			di.skipBytes(17);
		} else if (mode != 3)
		    di.skipBytes(17);
		else
		    di.skipBytes(9);
		byte[] sbuf = new byte[4];
		di.readFully(sbuf);
		String id = new String(sbuf);
		if (id.equals("Xing")) {
		    int XingFlags = di.readInt();
		    System.out
			.println("Flags: " + Integer.toHexString(XingFlags));
		    if ((XingFlags & ~0xffffffe) == 1)
			System.out
			    .println("Number of frames: " + di.readInt());
		    if ((XingFlags & ~0xffffffd) == 2)
			System.out
			    .println("Number of bytes : " + di.readInt());
		    if ((XingFlags & ~0xffffffb) == 4) {
			System.out.print("TOC:");
			System.out.print(di.readUnsignedByte());
			for (int j = 1; j < 100; j++)
			    System.out.print(" " + di.readUnsignedByte());
			System.out.println();
		    }
		    if ((XingFlags & ~0xffffff7) == 8)
			System.out.println("VBR scale flags: "
					   + Integer
						 .toHexString(di.readInt()));
		}
	    }
	}
	int last = di.readUnsignedByte();
	System.out.print(Integer.toHexString((last & 0xf0) >> 4)
			 + Integer.toHexString(last & 0xf));
	for (int i = 0; i < 1000; i++) {
	    int dis = di.readUnsignedByte();
	    System.out.print(Integer.toHexString((dis & 0xf0) >> 4)
			     + Integer.toHexString(dis & 0xf));
	    if (last == 255 && (dis & 0xe0) == 224)
		break;
	    last = dis;
	}
	System.out.println("\nAction starts at " + (raf.getFilePointer()
						    - 2L));
	raf.seek(cur);
    }
}
