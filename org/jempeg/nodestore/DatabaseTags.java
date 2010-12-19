/* DatabaseTags - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;

import Acme.IntHashtable;

import com.inzyme.util.Debug;

public class DatabaseTags implements Serializable
{
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
    public static final String[] CORE_TAGS
	= { "artist", "comment", "genre", "source", "title", "tracknr",
	    "year" };
    private TagsByNumber myTagsByNumber = new TagsByNumber();
    private TagsByName myTagsByName = new TagsByName();
    /*synthetic*/ static Class class$0;
    
    protected static class TagsByNumber implements Serializable
    {
	static final long serialVersionUID = 3407586807465969252L;
	private IntHashtable myTagToName = new IntHashtable();
	
	public TagsByNumber() {
	    /* empty */
	}
	
	public String getName(int _tag) {
	    return (String) myTagToName.get(_tag);
	}
	
	public void setName(int _tag, String _name) {
	    myTagToName.put(_tag, _name);
	}
	
	public int findFree() {
	    int i;
	    for (i = 0; getName(i) != null; i++) {
		/* empty */
	    }
	    return i;
	}
	
	public void clear() {
	    myTagToName.clear();
	}
    }
    
    protected static class TagsByName implements Serializable
    {
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
	    if (_name == "title")
		value = myTitleTag;
	    else if (_name == "type")
		value = myTypeTag;
	    else if (_name == "length")
		value = myLengthTag;
	    else {
		Integer num = (Integer) myNameToTag.get(_name);
		if (num == null)
		    value = -1;
		else
		    value = num.intValue();
	    }
	    return value;
	}
	
	public void setNumber(String _name, int _tag) {
	    String constantName = getConstant(_name);
	    myNameToTag.put(constantName, new Integer(_tag));
	    if (constantName == "type")
		myTypeTag = _tag;
	    else if (constantName == "title")
		myTitleTag = _tag;
	    else if (constantName == "length")
		myLengthTag = _tag;
	}
	
	public int getTypeTag() {
	    if (myTypeTag == -1)
		throw new RuntimeException("-1 type tag.");
	    return myTypeTag;
	}
	
	public int getTitleTag() {
	    if (myTitleTag == -1)
		throw new RuntimeException("-1 title tag.");
	    return myTitleTag;
	}
	
	public int getLengthTag() {
	    if (myLengthTag == -1)
		throw new RuntimeException("-1 length tag.");
	    return myLengthTag;
	}
	
	public void clear() {
	    myTypeTag = -1;
	    myTitleTag = -1;
	    myLengthTag = -1;
	    myNameToTag.clear();
	}
    }
    
    public DatabaseTags() {
	clear();
    }
    
    public static boolean isDatabaseTag(String _name) {
	boolean isDatabaseTag = false;
	Class var_class = class$0;
	if (var_class == null) {
	    Class var_class_0_;
	    try {
		var_class_0_
		    = Class.forName("org.jempeg.nodestore.DatabaseTags");
	    } catch (ClassNotFoundException classnotfoundexception) {
		NoClassDefFoundError noclassdeffounderror
		    = new NoClassDefFoundError;
		((UNCONSTRUCTED)noclassdeffounderror)
		    .NoClassDefFoundError(classnotfoundexception.getMessage());
		throw noclassdeffounderror;
	    }
	    var_class = class$0 = var_class_0_;
	}
	Field[] fields = var_class.getFields();
	for (int i = 0; !isDatabaseTag && i < fields.length; i++) {
	    int modifiers = fields[i].getModifiers();
	    try {
		isDatabaseTag = (Modifier.isPublic(modifiers)
				 && Modifier.isStatic(modifiers)
				 && fields[i].getName().endsWith("_TAG")
				 && _name.equals(fields[i].get(null)));
	    } catch (Throwable t) {
		Debug.println(t);
	    }
	}
	return isDatabaseTag;
    }
    
    public static String getConstant(String _name) {
	String constant;
	if ("title".equals(_name))
	    constant = "title";
	else if ("artist".equals(_name))
	    constant = "artist";
	else if ("source".equals(_name))
	    constant = "source";
	else if ("genre".equals(_name))
	    constant = "genre";
	else if ("year".equals(_name))
	    constant = "year";
	else if ("marked".equals(_name))
	    constant = "marked";
	else if ("drive".equals(_name))
	    constant = "drive";
	else if ("play_count".equals(_name))
	    constant = "play_count";
	else if ("skip_count".equals(_name))
	    constant = "skip_count";
	else if ("play_last".equals(_name))
	    constant = "play_last";
	else if ("ctime".equals(_name))
	    constant = "ctime";
	else if ("length".equals(_name))
	    constant = "length";
	else if ("type".equals(_name))
	    constant = "type";
	else
	    constant = _name;
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
	if (num == -1)
	    num = addTag(_name);
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
	getNumber("type");
	getNumber("length");
	getNumber("title");
    }
    
    public int addTag(String _name) {
	int num = myTagsByNumber.findFree();
	setName(num, _name);
	return num;
    }
    
    public Enumeration getTagNames() {
	return myTagsByName.keys();
    }
}
