/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.nodestore;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;

import Acme.IntHashtable;

import com.inzyme.util.Debug;

/**
* DatabaseTags is a doublely-hashed datastructure
* that maps Empeg MP3 tag names to Empeg MP3 tag
* IDs (and vice-versa).  There is one DatabaseTags
* per PlayerDatabase.
*
* @author Mike Schrag
* @version $Revision: 1.10 $
*/
public class DatabaseTags implements Serializable {
	public static final String ARTIST_TAG = "artist";
	public static final String BITRATE_TAG = "bitrate";
	public static final String CODEC_TAG = "codec";
	public static final String COLORED_TAG = "colored";
	public static final String COMMENT_TAG = "comment";
	public static final String COPY_TAG = "copy";
	public static final String COPYRIGHT_TAG = "copyright";
	public static final String CTIME_TAG = "ctime";
	public static final String DECADE_TAG = "decade";
	public static final String DIRTY_TAG = "dirty";
	public static final String DRIVE_TAG = "drive";
	public static final String DURATION_TAG = "duration";
	public static final String EXTENSION_TAG = "ext";
	public static final String FID_TAG = "fid";
	public static final String GENERATION_TAG = "fid_generation";
	public static final String GENRE_TAG = "genre";
	public static final String ICON_TYPE_TAG = "iconType";
	public static final String LENGTH_TAG = "length";
	public static final String MARKED_TAG = "marked";
	public static final String OFFSET_TAG = "offset";
	public static final String OPTIONS_TAG = "options";
	public static final String PICKN_TAG = "pickn";
	public static final String PICKPERCENT_TAG = "pickpercent";
	public static final String PIN_TAG = "pin";
	public static final String PLAY_COUNT_TAG = "play_count";
	public static final String PLAY_LAST_TAG = "play_last";
	public static final String PLAYLIST_TAG = "playlist";
	public static final String POS_TAG = "pos";
	public static final String PROFILE_TAG = "profile";
	public static final String REFS_TAG = "refs";
	public static final String RID_TAG = "rid";
	public static final String SAMPLERATE_TAG = "samplerate";
	public static final String PLAYLIST_SIZE_TAG = "size";
	public static final String SKIP_COUNT_TAG = "skip_count";
  public static final String SORT_TAG = "sort";
	public static final String SOUP_TAG = "soup";
	public static final String SOURCE_TAG = "source";
	public static final String STEREO_TAG = "stereo";
	public static final String TITLE_TAG = "title";
	public static final String TRACKNR_TAG = "tracknr";
	public static final String TRACKNR_OR_POS_TAG = "tracknrpos";
	public static final String TRACKS_TAG = "tracks";
	public static final String TRAILER_TAG = "trailer";
	public static final String TYPE_TAG = "type";
	public static final String WENDY_TAG = "wendy";
	public static final String YEAR_TAG = "year";
	
	public static final String JEMPLODE_TEMPORARY_TAG = "jemplode_temporary";
	public static final String JEMPLODE_IS_SOUP_TAG = "jemplode_issoup";

	public static final String TYPE_TUNE = "tune";
	public static final String TYPE_TAXI = "taxi";
	public static final String TYPE_PLAYLIST = "playlist";
	public static final String TYPE_ILLEGAL = "illegal";
	
	public static final String CODEC_MP3 = "mp3";
	public static final String CODEC_OGG = "vorbis";
	public static final String CODEC_FLAC = "flac";
	public static final String CODEC_WMA = "wma";
	public static final String CODEC_TAXI = "taxi";
	
	public static final String[] CORE_TAGS = new String[] { DatabaseTags.ARTIST_TAG, DatabaseTags.COMMENT_TAG, DatabaseTags.GENRE_TAG, DatabaseTags.SOURCE_TAG, DatabaseTags.TITLE_TAG, DatabaseTags.TRACKNR_TAG, DatabaseTags.YEAR_TAG };
	 
	private TagsByNumber myTagsByNumber;
	private TagsByName myTagsByName;

	public DatabaseTags() {
		myTagsByNumber = new TagsByNumber();
		myTagsByName = new TagsByName();
		clear();
	}
	
	public static boolean isDatabaseTag(String _name) {
		boolean isDatabaseTag = false;
		Field[] fields = DatabaseTags.class.getFields();
		for (int i = 0; !isDatabaseTag && i < fields.length; i ++) {
			int modifiers = fields[i].getModifiers();
			try {
				isDatabaseTag = Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && fields[i].getName().endsWith("_TAG") && _name.equals(fields[i].get(null));
			}
			catch (Throwable t) {
				Debug.println(t);
			}
		}
		return isDatabaseTag;
	}
	
	/**
	 * Takes a tag name and turns it into the known
	 * constant value so that == comparisons can
	 * be used internally.
	 * 
	 * @param _name the tag name to lookup
	 * @return a String constant of the tag if it exists
	 */
	public static String getConstant(String _name) {
		String constant;
		if (DatabaseTags.TITLE_TAG.equals(_name)) {
			constant = DatabaseTags.TITLE_TAG;
		}
		else if (DatabaseTags.ARTIST_TAG.equals(_name)) {
			constant = DatabaseTags.ARTIST_TAG;
		}
		else if (DatabaseTags.SOURCE_TAG.equals(_name)) {
			constant = DatabaseTags.SOURCE_TAG;
		}
		else if (DatabaseTags.GENRE_TAG.equals(_name)) {
			constant = DatabaseTags.GENRE_TAG;
		}
		else if (DatabaseTags.YEAR_TAG.equals(_name)) {
			constant = DatabaseTags.YEAR_TAG;
		}
		else if (DatabaseTags.MARKED_TAG.equals(_name)) {
			constant = DatabaseTags.MARKED_TAG;
		}
		else if (DatabaseTags.DRIVE_TAG.equals(_name)) {
			constant = DatabaseTags.DRIVE_TAG;
		}
		else if (DatabaseTags.PLAY_COUNT_TAG.equals(_name)) {
			constant = DatabaseTags.PLAY_COUNT_TAG;
		}
		else if (DatabaseTags.SKIP_COUNT_TAG.equals(_name)) {
			constant = DatabaseTags.SKIP_COUNT_TAG;
		}
		else if (DatabaseTags.PLAY_LAST_TAG.equals(_name)) {
			constant = DatabaseTags.PLAY_LAST_TAG;
		}
		else if (DatabaseTags.CTIME_TAG.equals(_name)) {
			constant = DatabaseTags.CTIME_TAG;
		}
		else if (DatabaseTags.LENGTH_TAG.equals(_name)) {
			constant = DatabaseTags.LENGTH_TAG;
		}
		else if (DatabaseTags.TYPE_TAG.equals(_name)) {
			constant = DatabaseTags.TYPE_TAG;
		} else {
			constant = _name;
		}
		return constant;
	}

	public String getName(int _tag) {
		return myTagsByNumber.getName(_tag);
	}

	public void setName(int _tag, String _name) {
		myTagsByNumber.setName(_tag, _name);
		myTagsByName.setNumber(_name, _tag);
	}

	public int getNumber(String _name) {
		int num = myTagsByName.getNumber(_name);
		if (num == -1) {
			num = addTag(_name);
		}
		return num;
	}

	public void setNumber(String _name, int _tag) {
		setName(_tag, _name);
	}

	public int getTypeTag() {
		return myTagsByName.getTypeTag();
	}

	public int getTitleTag() {
		return myTagsByName.getTitleTag();
	}

	public int getLengthTag() {
		return myTagsByName.getLengthTag();
	}

	public synchronized void clear() {
		myTagsByNumber.clear();
		myTagsByName.clear();
		
		// ensure the basic tags are in
		getNumber(DatabaseTags.TYPE_TAG);
		getNumber(DatabaseTags.LENGTH_TAG);
		getNumber(DatabaseTags.TITLE_TAG);
	}

	public int addTag(String _name) {
		int num = myTagsByNumber.findFree();
		setName(num, _name);
		return num;
	}

	public Enumeration getTagNames() {
		return myTagsByName.keys();
	}

	protected static class TagsByNumber implements Serializable {
		static final long serialVersionUID = 3407586807465969252L;

		private IntHashtable myTagToName;

		public TagsByNumber() {
			myTagToName = new IntHashtable();
		}

		public String getName(int _tag) {
			return (String) myTagToName.get(_tag);
		}

		public void setName(int _tag, String _name) {
			myTagToName.put(_tag, _name);
		}

		public int findFree() {
			int i;
			for (i = 0;; i++) {
				if (getName(i) == null) {
					break;
				}
			}
			return i;
		}

		public void clear() {
			myTagToName.clear();
		}
	}

	protected static class TagsByName implements Serializable {
		static final long serialVersionUID = -6269949226480153641L;

		private Hashtable myNameToTag;
		private int myTypeTag = -1;
		private int myTitleTag = -1;
		private int myLengthTag = -1;

		public TagsByName() {
			myNameToTag = new Hashtable();
		}

		public Enumeration keys() {
			return myNameToTag.keys();
		}

		public int getNumber(String _name) {
			int value;
			if (_name == DatabaseTags.TITLE_TAG) {
				value = myTitleTag;
			} else if (_name == DatabaseTags.TYPE_TAG) {
				value = myTypeTag;
			} else if (_name == DatabaseTags.LENGTH_TAG) {
				value = myLengthTag;
			} else {
				Integer num = (Integer) myNameToTag.get(_name);
				if (num == null) {
					value = -1;
				}
				else {
					value = num.intValue();
				}
			}
			return value;
		}

		public void setNumber(String _name, int _tag) {
			// Turns a String into a known constant so we can == compare common tag names.  This
			// will also speed up the Hashtable access since the == comparison happens first
			String constantName = getConstant(_name);
			myNameToTag.put(constantName, new Integer(_tag));
			if (constantName == DatabaseTags.TYPE_TAG) {
				myTypeTag = _tag;
			}
			else if (constantName == DatabaseTags.TITLE_TAG) {
				myTitleTag = _tag;
			}
			else if (constantName == DatabaseTags.LENGTH_TAG) {
				myLengthTag = _tag;
			}
		}

		public int getTypeTag() {
			if (myTypeTag == -1) {
				throw new RuntimeException("-1 type tag.");
			}

			return myTypeTag;
		}

		public int getTitleTag() {
			if (myTitleTag == -1) {
				throw new RuntimeException("-1 title tag.");
			}

			return myTitleTag;
		}

		public int getLengthTag() {
			if (myLengthTag == -1) {
				throw new RuntimeException("-1 length tag.");
			}

			return myLengthTag;
		}

		public void clear() {
			myTypeTag = -1;
			myTitleTag = -1;
			myLengthTag = -1;
			myNameToTag.clear();
		}
	}
}
