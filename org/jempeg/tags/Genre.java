package org.jempeg.tags;

import com.inzyme.sort.QuickSort;
import com.inzyme.text.CollationKeyCache;

/**
* Provides access to Genre information.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class Genre {
	public static final String[] GENRES = new String[] {
		"Blues",                // 0
		"Classic Rock",
		"Country",
		"Dance",
		"Disco",
		"Funk",
		"Grunge",
		"Hip-Hop",
		"Jazz",
		"Metal",

		"New Age",              // 10
		"Oldies",
		"Other",
		"Pop",
		"R&B",
		"Rap",
		"Reggae",
		"Rock",
		"Techno",
		"Industrial",

		"Alternative",  // 20
		"Ska",
		"Death Metal",
		"Pranks",
		"Soundtrack",
		"Euro-Techno",
		"Ambient",
		"Trip-Hop",
		"Vocal",
		"Jazz+Funk",

		"Fusion",               // 30
		"Trance",
		"Classical",
		"Instrumental",
		"Acid",
		"House",
		"Game",
		"Sound Clip",
		"Gospel",
		"Noise",

		"AlternRock",   // 40
		"Bass",
		"Soul",
		"Punk",
		"Space",
		"Meditative",
		"Instrumental Pop",
		"Instrumental Rock",
		"Ethnic",
		"Gothic",

		"Darkwave",             // 50
		"Techno-Industrial",
		"Electronic",
		"Pop-Folk",
		"Eurodance",
		"Dream",
		"Southern Rock",
		"Comedy",
		"Cult",
		"Gangsta",

		"Top 40",               // 60
		"Christian Rap",
		"Pop/Funk",
		"Jungle",
		"Native American",
		"Cabaret",
		"New Wave",
		"Psychadelic",
		"Rave",
		"Showtunes",

		"Trailer",              // 70
		"Lo-Fi",
		"Tribal",
		"Acid Punk",
		"Acid Jazz",
		"Polka",
		"Retro",
		"Musical",
		"Rock & Roll",
		"Hard Rock",

		"Folk",         // 80
		"Folk-Rock",
		"National Folk",
		"Swing",
		"Fast Fusion",
		"Bebob",
		"Latin",
		"Revival",
		"Celtic",
		"Bluegrass",

		"Avantgarde",   // 90
		"Gothic Rock",
		"Progressive Rock",
		"Psychedelic Rock",
		"Symphonic Rock",
		"Slow Rock",
		"Big Band",
		"Chorus",
		"Easy Listening",
		"Acoustic",

		"Humour",               // 100
		"Speech",
		"Chanson",
		"Opera",
		"Chamber Music",

		"Sonata",
		"Symphony",
		"Booty Bass",
		"Primus",
		"Porn Groove",

		"Satire",               // 110
		"Slow Jam",
		"Club",
		"Tango",
		"Samba",

		"Folklore",
		"Ballad",
		"Power Ballad",
		"Rhythmic Soul",
		"Freestyle",

		"Duet",         // 120
		"Punk Rock",
		"Drum Solo",
		"Acapella",
		"Euro-House",

		"Dance Hall",
		"Goa",          // 126
		"Drum & Bass",
		"Club-House",
		"Hardcore",

		"Terror",               // 130
		"Indie",
		"Britpop",
		"Negerpunk",
		"Polsk Punk",

		"Beat",
		"Christian Gangsta",
		"Heavy Metal",
		"Black Metal",
		"Crossover",

		"Contemporary C",       // 140
		"Christian Rock",
		"Merengue",
		"Salsa",
		"Thrash Metal",
		"Anime",
		"JPop",
		"Synth Pop",    // 147
		"Sweetcorn Dub"     // 148
	};
	
	public static int getCount() {
		return Genre.GENRES.length;
	}

	/**
	* Returns the name of the genre for the given number.
	*
	* @param _genreNum the genre number
	* @returns the name of the genre for the given number
	*/
	public static String getGenre(int _index) {
		if (_index < 0 || _index >= Genre.GENRES.length) {
			return "";
		} else {
			return Genre.GENRES[_index];
		}
	}
	/**
	* Returns the number of the genre for the given name.
	*
	* @param _genre the genre name
	* @returns the number of the genre for the given name
	*/
	public static int getGenreNum(String _genre) {
		for (int i = 0; i < Genre.GENRES.length; i++) {
			if (Genre.GENRES[i].equalsIgnoreCase(_genre)) {
				return i;
			}
		}
		return -1;
	}

	/**
	* Returns a sorted list of Genre names.
	*
	* @returns a sorted list of Genre names
	*/
	public static String[] getSortedGenres() {
		String[] genresCopy = new String[Genre.GENRES.length];
		for (int i = 0; i < genresCopy.length; i++) {
			genresCopy[i] = Genre.GENRES[i];
		}
		QuickSort qs = new QuickSort(CollationKeyCache.createDefaultCache());
		qs.sort(genresCopy);
		return genresCopy;
	}

	/**
	* Returns the Genre string that corresponds to the
	* passed in String.  If the genre string passed in
	* is of the format (xxx) where xxx is a number, then
	* it will return the named Genre for that number.
	* Otherwise it will just return the string that
	* was passed in.
	*
	* @param _origGenre the current genre
	* @returns the genre string that it maps to
	*/
	public static String getGenre(String _origGenre) {
		String genre = _origGenre;
		if (_origGenre != null) {
			_origGenre = _origGenre.trim();
			if (_origGenre.length() > 2 && _origGenre.charAt(0) == '(') {
        int closeParenIndex = _origGenre.indexOf(')');
        if (closeParenIndex != -1) {
  				try {
  					String genreNumStr = _origGenre.substring(1, closeParenIndex);
  					int genreNum = Integer.parseInt(genreNumStr);
  					genre = Genre.getGenre(genreNum);
  				} catch (Throwable t) {
  					// not a big deal .. we tried
  				}
        }
			}
		}
		return genre;
	}
}