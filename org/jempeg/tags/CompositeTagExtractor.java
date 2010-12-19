package org.jempeg.tags;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.util.Debug;

public class CompositeTagExtractor implements ITagExtractor {
	private Vector myTagExtractors;
	private boolean myAllowUnknownTypes;

	public CompositeTagExtractor(Vector _tagExtractors, boolean _allowUnknownTypes) {
		myTagExtractors = _tagExtractors;
		myAllowUnknownTypes = _allowUnknownTypes;
	}

	public int isTagExtractorFor(String _name, byte[] _header) {
		return ITagExtractor.MAYBE;
	}

	public void extractTags(SeekableInputStream _is, ITagExtractorListener _listener) throws IOException {
		boolean tagsExtracted = false;

		Enumeration tagExtractorsEnum = myTagExtractors.elements();
		while (!tagsExtracted && tagExtractorsEnum.hasMoreElements()) {
			PropertiesTagExtractorListener ptel = new PropertiesTagExtractorListener();
			ITagExtractor tagExtractor = (ITagExtractor) tagExtractorsEnum.nextElement();
			try {
				_is.seek(0);
				tagExtractor.extractTags(_is, ptel);
				refireExtractedTags(ptel, _listener);
				tagsExtracted = true;
			}
			catch (Throwable t) {
				Debug.println(Debug.INFORMATIVE, t);
			}
		}

		if (!tagsExtracted) {
			lastResort(null, _listener);
		}
	}

	public void extractTags(IImportFile _file, ITagExtractorListener _listener) throws IOException {
		boolean tagsExtracted = false;

		Enumeration tagExtractorsEnum = myTagExtractors.elements();
		while (!tagsExtracted && tagExtractorsEnum.hasMoreElements()) {
			PropertiesTagExtractorListener ptel = new PropertiesTagExtractorListener();
			ITagExtractor tagExtractor = (ITagExtractor) tagExtractorsEnum.nextElement();
			try {
				tagExtractor.extractTags(_file, ptel);
				refireExtractedTags(ptel, _listener);
				tagsExtracted = true;
			}
			catch (Throwable t) {
				Debug.println(Debug.INFORMATIVE, t);
			}
		}

		if (!tagsExtracted) {
			lastResort(_file, _listener);
		}
	}
	
	private void lastResort(IImportFile _file, ITagExtractorListener _listener) throws IOException {
		if (myAllowUnknownTypes) {
			new NoopTagExtractor(_file).extractTags(_file, _listener);
		}
		else {
			throw new UnknownFileFormatException("Failed to identify " + _file + ".");
		}
	}

	private void refireExtractedTags(PropertiesTagExtractorListener _source, ITagExtractorListener _target) {
		Properties tagProps = _source.getTags();
		Enumeration tagPropsEnum = tagProps.keys();
		while (tagPropsEnum.hasMoreElements()) {
			String tagName = (String) tagPropsEnum.nextElement();
			String tagValue = tagProps.getProperty(tagName);
			_target.tagExtracted(tagName, tagValue);
		}
	}

}
