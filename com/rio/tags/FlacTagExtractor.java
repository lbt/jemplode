/* FlacTagExtractor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.tags;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.SeekableInputStream;
import com.rio.flac.FlacFile;
import com.rio.flac.StreamInfoMetadataBlock;

import org.jempeg.tags.EmpegBitrate;
import org.jempeg.tags.ITagExtractor;
import org.jempeg.tags.ITagExtractorListener;
import org.jempeg.tags.id3.ID3TagExtractor;

public class FlacTagExtractor implements ITagExtractor
{
    public int isTagExtractorFor(String _name, byte[] _header) {
	int isTagExtractor;
	if (FlacFile.isFlacMagicNumber(_header))
	    isTagExtractor = 1;
	else if (ID3TagExtractor.isID3Tag(_header)) {
	    if (_name.toLowerCase().endsWith(".flac"))
		isTagExtractor = 1;
	    else
		isTagExtractor = 0;
	} else
	    isTagExtractor = -1;
	return isTagExtractor;
    }
    
    public void extractTags
	(SeekableInputStream _is, ITagExtractorListener _listener)
	throws IOException {
	FlacFile flacFile = new FlacFile(_is);
	if (flacFile.hasID3Tags()) {
	    Properties id3Tags = flacFile.getExtractedID3Tags();
	    Enumeration tagNames = id3Tags.keys();
	    while (tagNames.hasMoreElements()) {
		String tagName = (String) tagNames.nextElement();
		String tagValue = id3Tags.getProperty(tagName);
		_listener.tagExtracted(tagName, tagValue);
	    }
	} else {
	    Properties vorbisTags = flacFile.getExtractedVorbisTags();
	    OggTagExtractor.extractCommentTags(vorbisTags, _listener);
	}
	StreamInfoMetadataBlock streamInfo = flacFile.getStreamInfo();
	_listener.tagExtracted("samplerate", streamInfo.getSampleRate());
	long durationMs = (streamInfo.getTotalSamples() * 1000L
			   / (long) streamInfo.getSampleRate());
	_listener.tagExtracted("duration", durationMs);
	long length = _is.length();
	_listener.tagExtracted("length", length);
	EmpegBitrate ebr = new EmpegBitrate();
	ebr.setVBR(true);
	ebr.setChannels(streamInfo.getNumChannels());
	long bitsPerSec = length * 8L * 1000L / durationMs;
	ebr.setBitsPerSecond((int) bitsPerSec);
	_listener.tagExtracted("bitrate", ebr.toString());
	_listener.tagExtracted("codec", "flac");
	_listener.tagExtracted("type", "tune");
    }
    
    public void extractTags
	(IImportFile _file, ITagExtractorListener _listener)
	throws IOException {
	SeekableInputStream stream = _file.getSeekableInputStream();
	try {
	    extractTags(stream, _listener);
	} catch (Object object) {
	    stream.close();
	    throw object;
	}
	stream.close();
    }
}
