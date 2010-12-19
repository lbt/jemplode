/* SynchSafeInteger - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2;

public class SynchSafeInteger
{
    public static int Decode(int encoded) throws IllegalArgumentException {
	if ((encoded & ~0x7f7f7f7f) != 0)
	    throw new IllegalArgumentException
		      ("The synch-safe integer passed to ID3v2.DecodeSizeIndicator() is malformed!");
	return ((encoded & 0x7f000000) >> 3 | (encoded & 0x7f0000) >> 2
		| (encoded & 0x7f00) >> 1 | encoded & 0x7f);
    }
    
    public static int Encode(int integer) throws IllegalArgumentException {
	if ((integer & ~0xfffffff) != 0)
	    throw new IllegalArgumentException
		      ("length in ID3v2.EncodeSizeIndicator() is larger than 28 bits!");
	return ((integer & 0xfe00000) << 3 | (integer & 0x1fc000) << 2
		| (integer & 0x3f80) << 1 | integer & 0x7f);
    }
}
