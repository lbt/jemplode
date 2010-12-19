/* OggTagExtractor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.tags;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.ChainedIOException;
import com.inzyme.io.SeekableInputStream;
import com.rio.ogg.CommentOggHeader;
import com.rio.ogg.IdentificationOggHeader;
import com.rio.ogg.InvalidPageHeaderException;
import com.rio.ogg.OggFile;

import org.jempeg.tags.EmpegBitrate;
import org.jempeg.tags.ITagExtractor;
import org.jempeg.tags.ITagExtractorListener;
import org.jempeg.tags.RID;
import org.jempeg.tags.id3.ID3TagExtractor;

public class OggTagExtractor implements ITagExtractor
{
    public int isTagExtractorFor(String _name, byte[] _header) {
	int isTagExtractor;
	if (_header[0] == 79 && _header[1] == 103 && _header[2] == 103)
	    isTagExtractor = 1;
	else if (ID3TagExtractor.isID3Tag(_header)) {
	    if (_name.toLowerCase().endsWith(".ogg"))
		isTagExtractor = 1;
	    else
		isTagExtractor = 0;
	} else
	    isTagExtractor = -1;
	return isTagExtractor;
    }
    
    public static void extractCommentTags(Properties _tags,
					  ITagExtractorListener _listener) {
	_listener.tagExtracted("type", "tune");
	_listener.tagExtracted("title", _tags.getProperty("TITLE"));
	_listener.tagExtracted("artist", _tags.getProperty("ARTIST"));
	_listener.tagExtracted("source", _tags.getProperty("ALBUM"));
	_listener.tagExtracted("genre", _tags.getProperty("GENRE"));
	_listener.tagExtracted("comment", _tags.getProperty("COMMENT"));
	_listener.tagExtracted("tracknr", _tags.getProperty("TRACKNUMBER"));
	_listener.tagExtracted("year", _tags.getProperty("DATE"));
    }
    
    public void extractTags
	(SeekableInputStream _is, ITagExtractorListener _listener)
	throws IOException {
	try {
	    OggFile oggFile = new OggFile(_is);
	    IdentificationOggHeader identificationHeader
		= oggFile.getIdentificationHeader();
	    EmpegBitrate eb = new EmpegBitrate();
	    if ((identificationHeader.getBitrateMaximum()
		 == identificationHeader.getBitrateMinimum())
		&& identificationHeader.getBitrateMaximum() > 0)
		eb.setVBR(false);
	    else
		eb.setVBR(true);
	    eb.setChannels(identificationHeader.getAudioChannels());
	    eb.setBitsPerSecond(identificationHeader.getBitrate());
	    _listener.tagExtracted("bitrate", eb.toString());
	    _listener.tagExtracted("samplerate",
				   identificationHeader.getSampleRate());
	    _listener.tagExtracted("duration", oggFile.getDuration());
	    if (oggFile.hasID3Tags()) {
		Properties id3Tags = oggFile.getExtractedID3Tags();
		Enumeration tagNames = id3Tags.keys();
		while (tagNames.hasMoreElements()) {
		    String tagName = (String) tagNames.nextElement();
		    String tagValue = id3Tags.getProperty(tagName);
		    _listener.tagExtracted(tagName, tagValue);
		}
	    } else {
		CommentOggHeader commentHeader = oggFile.getCommentHeader();
		Properties tags = commentHeader.toProperties();
		extractCommentTags(tags, _listener);
	    }
	    String rid = RID.calculateRid(_is, 0L, _is.length());
	    _listener.tagExtracted("rid", rid);
	} catch (InvalidPageHeaderException e) {
	    throw new ChainedIOException("Failed to parse ogg file.", e);
	}
    }
    
    public void extractTags
	(IImportFile _file, ITagExtractorListener _listener)
	throws IOException {
	_listener.tagExtracted("codec", "vorbis");
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
