/* Genre - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags;
import com.inzyme.sort.QuickSort;
import com.inzyme.text.CollationKeyCache;

public class Genre
{
    public static final String[] GENRES
	= { "Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk",
	    "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other",
	    "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial",
	    "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack",
	    "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk",
	    "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House",
	    "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass",
	    "Soul", "Punk", "Space", "Meditative", "Instrumental Pop",
	    "Instrumental Rock", "Ethnic", "Gothic", "Darkwave",
	    "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance",
	    "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40",
	    "Christian Rap", "Pop/Funk", "Jungle", "Native American",
	    "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes",
	    "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka",
	    "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk",
	    "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob",
	    "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde",
	    "Gothic Rock", "Progressive Rock", "Psychedelic Rock",
	    "Symphonic Rock", "Slow Rock", "Big Band", "Chorus",
	    "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson",
	    "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass",
	    "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango",
	    "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul",
	    "Freestyle", "Duet", "Punk Rock", "Drum Solo", "Acapella",
	    "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House",
	    "Hardcore", "Terror", "Indie", "Britpop", "Negerpunk",
	    "Polsk Punk", "Beat", "Christian Gangsta", "Heavy Metal",
	    "Black Metal", "Crossover", "Contemporary C", "Christian Rock",
	    "Merengue", "Salsa", "Thrash Metal", "Anime", "JPop", "Synth Pop",
	    "Sweetcorn Dub" };
    
    public static int getCount() {
	return GENRES.length;
    }
    
    public static String getGenre(int _index) {
	if (_index < 0 || _index >= GENRES.length)
	    return "";
	return GENRES[_index];
    }
    
    public static int getGenreNum(String _genre) {
	for (int i = 0; i < GENRES.length; i++) {
	    if (GENRES[i].equalsIgnoreCase(_genre))
		return i;
	}
	return -1;
    }
    
    public static String[] getSortedGenres() {
	String[] genresCopy = new String[GENRES.length];
	for (int i = 0; i < genresCopy.length; i++)
	    genresCopy[i] = GENRES[i];
	QuickSort qs = new QuickSort(CollationKeyCache.createDefaultCache());
	qs.sort(genresCopy);
	return genresCopy;
    }
    
    public static String getGenre(String _origGenre) {
	String genre = _origGenre;
	if (_origGenre != null) {
	    _origGenre = _origGenre.trim();
	    if (_origGenre.length() > 2 && _origGenre.charAt(0) == '(') {
		int closeParenIndex = _origGenre.indexOf(')');
		if (closeParenIndex != -1) {
		    try {
			String genreNumStr
			    = _origGenre.substring(1, closeParenIndex);
			int genreNum = Integer.parseInt(genreNumStr);
			genre = getGenre(genreNum);
		    } catch (Throwable throwable) {
			/* empty */
		    }
		}
	    }
	}
	return genre;
    }
}
