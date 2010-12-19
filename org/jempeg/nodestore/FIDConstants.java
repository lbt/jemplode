/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.nodestore;

/**
* FIDConstants defines a set of constants
* that are used to reference various specific FID
* numbers on the Empeg.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class FIDConstants {
  /* Predefined fids */
	public static final int FID_VERSION          = 0;
	public static final int FID_OWNER            = 1;
	public static final int FID_ID               = 1;
	public static final int FID_TAGINDEX         = 2;
	public static final int FID_STATICDATABASE   = 3;
	public static final int FID_DYNAMICDATABASE  = 4;
	public static final int FID_PLAYLISTDATABASE = 5;
	public static final int FID_CONFIGFILE       = 6;
  public static final int FID_PLAYERTYPE       = 7;
  public static final int FID_VISUALLIST       = 8;

  /* Internal FIDs - these are for the use of
   * emplode - they should never appear on the player
   */
  public static final int FID_INTERNAL_CLIPBOARD     = 0x70;
  public static final int FID_INTERNAL_TRASHCAN      = 0x80;
  public static final int FID_INTERNAL_SEARCHRESULTS = 0x90;

  /* Reserved FIDs that are used on the player
   */
	public static final int FID_ROOTPLAYLIST     = 0x100;
	
	public static final int FID_UNATTACHED_ITEMS = 0x110;
	
  /* Convenient definition to find the first normal item
   * on the player.
   */
  public static final int FID_FIRSTNORMAL      = 0x120;

	public static final int FIDTYPE_TUNE = 0;
	public static final int FIDTYPE_TAGS = 1;
	public static final int FIDTYPE_LOWBITRATE = 2;
	public static final int FIDTYPE_MASK = 0xF;
	
// Options flags: this is a 32-bit bitset, which defines options for a tune or
// playlists. The default options setting is zero.

  // Randomise playlist?
  public static final int PLAYLIST_OPTION_RANDOMISE       = 0x00000008;

  // Automatically loop playlist
  public static final int PLAYLIST_OPTION_LOOP            = 0x00000010;
  
  // Don't treat as part of parent
  public static final int PLAYLIST_OPTION_IGNOREASCHILD   = 0x00000020;
  
  // This flag set when the fid information has been fully completed from cd info (or edited manually)
  public static final int PLAYLIST_OPTION_CDINFO_RESOLVED = 0x00000040;
  
  // Is there a copyright on this fid
  public static final int PLAYLIST_OPTION_COPYRIGHT       = 0x00000080;
  
  // Is this fid marked as a copy
  public static final int PLAYLIST_OPTION_COPY            = 0x00000100;

  // Do we want to bleed the stereo (eg Beatles via headphones)
  public static final int PLAYLIST_OPTION_STEREO_BLEED    = 0x00000200;

	public static final int getFIDNumber(int _fid) {
		return (_fid >> 4);
	}
	
	public static final int getFIDType(int _fid) {
		return (_fid & 0xf);
	}
	
	public static final long makeFID(int _index, int _type) {
		return (_index << 4) | (_type & 0xF);
	}
}

