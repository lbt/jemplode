/* ID3Genres - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

import com.tffenterprises.util.StringMergeSort;

public abstract class ID3Genres
{
    private static final String GENRE_NONE = "None";
    private static String[] GENRE_STRINGS;
    private static Hashtable GENRE_INDICES;
    private static String[] SORTED_GENRE_STRINGS = null;
    
    static {
	String bundleName = "com.tffenterprises.music.tag.GenreList";
	ResourceBundle genresRB = ResourceBundle.getBundle(bundleName);
	GENRE_INDICES = new Hashtable();
	Enumeration e = genresRB.getKeys();
	while (e.hasMoreElements()) {
	    try {
		String key = (String) e.nextElement();
		GENRE_INDICES.put(genresRB.getString(key), Short.valueOf(key));
	    } catch (NumberFormatException numberformatexception) {
		/* empty */
	    }
	}
	GENRE_STRINGS = new String[GENRE_INDICES.size()];
	e = GENRE_INDICES.keys();
	while (e.hasMoreElements()) {
	    String g = (String) e.nextElement();
	    Short s = (Short) GENRE_INDICES.get(g);
	    GENRE_STRINGS[s.shortValue()] = g;
	}
    }
    
    public static String ByteToString(byte genre) {
	try {
	    return GENRE_STRINGS[genre & 0xff];
	} catch (ArrayIndexOutOfBoundsException e) {
	    return "None";
	}
    }
    
    public static byte StringToByte(String genre) {
	Short s = (Short) GENRE_INDICES.get(genre);
	if (s == null)
	    return (byte) -1;
	return s.byteValue();
    }
    
    public static Enumeration Enumerate() {
	class GenreEnumeration implements Enumeration
	{
	    int current = 0;
	    
	    public boolean hasMoreElements() {
		if (current < ID3Genres.GENRE_STRINGS.length)
		    return true;
		return false;
	    }
	    
	    public Object nextElement() {
		return ID3Genres.GENRE_STRINGS[current++];
	    }
	};
	return new GenreEnumeration();
    }
    
    public static synchronized String[] getSortedGenres() {
	if (SORTED_GENRE_STRINGS == null) {
	    int arrayLength = GENRE_STRINGS.length;
	    StringMergeSort mergeSort = new StringMergeSort();
	    SORTED_GENRE_STRINGS = new String[arrayLength];
	    System.arraycopy(GENRE_STRINGS, 0, SORTED_GENRE_STRINGS, 0,
			     GENRE_STRINGS.length);
	    mergeSort.sort(SORTED_GENRE_STRINGS);
	}
	String[] newSortedGenres = new String[SORTED_GENRE_STRINGS.length];
	System.arraycopy(SORTED_GENRE_STRINGS, 0, newSortedGenres, 0,
			 SORTED_GENRE_STRINGS.length);
	return newSortedGenres;
    }
}
