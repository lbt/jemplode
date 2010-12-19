package org.jempeg.tags;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.util.Debug;

public class TagExtractorFactory {
	private static Vector TAGEXTRACTORS = new Vector();

	public static void removeAllTagExtractors() {
		TagExtractorFactory.TAGEXTRACTORS.removeAllElements();
	}
	
	public static void addTagExtractor(ITagExtractor _tagExtractor) {
		TagExtractorFactory.TAGEXTRACTORS.addElement(_tagExtractor);
	}

	public static ITagExtractor createTagExtractor(IImportFile _file, boolean _allowUnknownTypes) throws IOException {
		ITagExtractor matchingTagExtractor = null;
		Vector possibleMatchingTagExtractorsVec = null;
		
		int minLength = 12;
		long length = _file.getLength();
		if (length >= minLength) {
			InputStream is = _file.getInputStream();
			try {
				byte[] header = new byte[minLength];
				is.read(header);
				
				Enumeration tagExtractorsEnum = TagExtractorFactory.TAGEXTRACTORS.elements();
				while (matchingTagExtractor == null && tagExtractorsEnum.hasMoreElements()) {
					ITagExtractor tagExtractor = (ITagExtractor)tagExtractorsEnum.nextElement();
					int isTagExtractor = tagExtractor.isTagExtractorFor(_file.getName(), header);
					if (isTagExtractor == ITagExtractor.YES) {
						matchingTagExtractor = tagExtractor;
					}
					else if (isTagExtractor == ITagExtractor.MAYBE) {
						if (possibleMatchingTagExtractorsVec == null) {
							possibleMatchingTagExtractorsVec = new Vector();
						}
						possibleMatchingTagExtractorsVec.addElement(tagExtractor);
					}
				}
			} finally {
				is.close();
			}
		}
		
		if (matchingTagExtractor == null) {
			if (possibleMatchingTagExtractorsVec == null) {
				if (_allowUnknownTypes) {
					Debug.println(Debug.WARNING, "Unknown file format in file " + _file + ".");
					matchingTagExtractor = new NoopTagExtractor(_file);
				} else {
					throw new UnknownFileFormatException("Unknown file format in file " + _file + ".");
				}
			}
			else {
				matchingTagExtractor = new CompositeTagExtractor(possibleMatchingTagExtractorsVec, _allowUnknownTypes);
			}
		}
		
		return matchingTagExtractor;
	}
}
