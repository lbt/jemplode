/* TagExtractorFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.util.Debug;

public class TagExtractorFactory
{
    private static Vector TAGEXTRACTORS = new Vector();
    
    public static void removeAllTagExtractors() {
	TAGEXTRACTORS.removeAllElements();
    }
    
    public static void addTagExtractor(ITagExtractor _tagExtractor) {
	TAGEXTRACTORS.addElement(_tagExtractor);
    }
    
    public static ITagExtractor createTagExtractor
	(IImportFile _file, boolean _allowUnknownTypes) throws IOException {
	ITagExtractor matchingTagExtractor = null;
	Vector possibleMatchingTagExtractorsVec = null;
	int minLength = 12;
	long length = _file.getLength();
	if (length >= (long) minLength) {
	    InputStream is = _file.getInputStream();
	    try {
		byte[] header = new byte[minLength];
		is.read(header);
		Enumeration tagExtractorsEnum = TAGEXTRACTORS.elements();
		while (matchingTagExtractor == null) {
		    if (!tagExtractorsEnum.hasMoreElements())
			break;
		    ITagExtractor tagExtractor
			= (ITagExtractor) tagExtractorsEnum.nextElement();
		    int isTagExtractor
			= tagExtractor.isTagExtractorFor(_file.getName(),
							 header);
		    if (isTagExtractor == 1)
			matchingTagExtractor = tagExtractor;
		    else if (isTagExtractor == 0) {
			if (possibleMatchingTagExtractorsVec == null)
			    possibleMatchingTagExtractorsVec = new Vector();
			possibleMatchingTagExtractorsVec
			    .addElement(tagExtractor);
		    }
		}
	    } catch (Object object) {
		is.close();
		throw object;
	    }
	    is.close();
	}
	if (matchingTagExtractor == null) {
	    if (possibleMatchingTagExtractorsVec == null) {
		if (_allowUnknownTypes) {
		    Debug.println(8, ("Unknown file format in file " + _file
				      + "."));
		    matchingTagExtractor = new NoopTagExtractor(_file);
		} else
		    throw new UnknownFileFormatException
			      ("Unknown file format in file " + _file + ".");
	    } else
		matchingTagExtractor
		    = (new CompositeTagExtractor
		       (possibleMatchingTagExtractorsVec, _allowUnknownTypes));
	}
	return matchingTagExtractor;
    }
}
