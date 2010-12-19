/* FIDConstants - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;

public class FIDConstants
{
    public static final int FID_VERSION = 0;
    public static final int FID_OWNER = 1;
    public static final int FID_ID = 1;
    public static final int FID_TAGINDEX = 2;
    public static final int FID_STATICDATABASE = 3;
    public static final int FID_DYNAMICDATABASE = 4;
    public static final int FID_PLAYLISTDATABASE = 5;
    public static final int FID_CONFIGFILE = 6;
    public static final int FID_PLAYERTYPE = 7;
    public static final int FID_VISUALLIST = 8;
    public static final int FID_INTERNAL_CLIPBOARD = 112;
    public static final int FID_INTERNAL_TRASHCAN = 128;
    public static final int FID_INTERNAL_SEARCHRESULTS = 144;
    public static final int FID_ROOTPLAYLIST = 256;
    public static final int FID_UNATTACHED_ITEMS = 272;
    public static final int FID_FIRSTNORMAL = 288;
    public static final int FIDTYPE_TUNE = 0;
    public static final int FIDTYPE_TAGS = 1;
    public static final int FIDTYPE_LOWBITRATE = 2;
    public static final int FIDTYPE_MASK = 15;
    public static final int PLAYLIST_OPTION_RANDOMISE = 8;
    public static final int PLAYLIST_OPTION_LOOP = 16;
    public static final int PLAYLIST_OPTION_IGNOREASCHILD = 32;
    public static final int PLAYLIST_OPTION_CDINFO_RESOLVED = 64;
    public static final int PLAYLIST_OPTION_COPYRIGHT = 128;
    public static final int PLAYLIST_OPTION_COPY = 256;
    public static final int PLAYLIST_OPTION_STEREO_BLEED = 512;
    
    public static final int getFIDNumber(int _fid) {
	return _fid >> 4;
    }
    
    public static final int getFIDType(int _fid) {
	return _fid & 0xf;
    }
    
    public static final long makeFID(int _index, int _type) {
	return (long) (_index << 4 | _type & 0xf);
    }
}
