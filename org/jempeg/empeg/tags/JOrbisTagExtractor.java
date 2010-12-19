/* JOrbisTagExtractor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.tags;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.ChainedIOException;
import com.inzyme.io.SeekableInputStream;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.JOrbisException;
import com.jcraft.jorbis.VorbisFile;

import org.jempeg.tags.EmpegBitrate;
import org.jempeg.tags.ITagExtractor;
import org.jempeg.tags.ITagExtractorListener;
import org.jempeg.tags.PropertiesTagExtractorListener;
import org.jempeg.tags.RID;
import org.jempeg.tags.id3.ID3TagExtractor;

public class JOrbisTagExtractor implements ITagExtractor
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
    
    public void extractTags
	(SeekableInputStream _is, ITagExtractorListener _listener)
	throws IOException {
	try {
	    ID3TagExtractor id3TagExtractor = new ID3TagExtractor();
	    PropertiesTagExtractorListener tagExtractorListener
		= new PropertiesTagExtractorListener();
	    ID3TagExtractor.TagOffsets offsets
		= id3TagExtractor
		      .extractTagsWithoutFrameInfo(_is, tagExtractorListener);
	    Properties id3Tags = tagExtractorListener.getTags();
	    org.jempeg.tags.id3.ID3V1Tag id3V1Tag
		= id3TagExtractor.getID3V1Tag();
	    org.jempeg.tags.id3.ID3V2Tag id3V2Tag
		= id3TagExtractor.getID3V2Tag();
	    _is.seek((long) offsets.getOffset());
	    if (id3V1Tag != null || id3V2Tag != null) {
		id3Tags.put("codec", "vorbis");
		Enumeration tagNames = id3Tags.keys();
		while (tagNames.hasMoreElements()) {
		    String tagName = (String) tagNames.nextElement();
		    String tagValue = id3Tags.getProperty(tagName);
		    _listener.tagExtracted(tagName, tagValue);
		}
	    } else
		_listener.tagExtracted("codec", "vorbis");
	    VorbisFile oggFile = new VorbisFile(_is, null, 0);
	    Comment[] comments = oggFile.getComment();
	    for (int i = 0; i < comments.length; i++) {
		_listener.tagExtracted("type", "tune");
		_listener.tagExtracted("title", comments[i].query("title"));
		_listener.tagExtracted("artist", comments[i].query("artist"));
		_listener.tagExtracted("source", comments[i].query("album"));
		_listener.tagExtracted("genre", comments[i].query("genre"));
		_listener.tagExtracted("comment",
				       comments[i].query("comment"));
		_listener.tagExtracted("tracknr",
				       comments[i].query("tracknumber"));
		_listener.tagExtracted("year", comments[i].query("date"));
	    }
	    EmpegBitrate eb = new EmpegBitrate();
	    Info[] info = oggFile.getInfo();
	    for (int i = 0; i < info.length; i++) {
		if (info[i].bitrate_upper == info[i].bitrate_lower
		    && info[i].bitrate_upper > 0)
		    eb.setVBR(false);
		else
		    eb.setVBR(true);
		eb.setChannels(info[i].channels);
		eb.setBitsPerSecond(info[i].getBitrate());
		_listener.tagExtracted("bitrate", eb.toString());
		_listener.tagExtracted("samplerate", info[i].rate);
		_listener.tagExtracted("duration",
				       (int) (oggFile.time_total(-1)
					      * 1000.0F));
	    }
	    String rid = RID.calculateRid(_is, 0L, _is.length());
	    _listener.tagExtracted("rid", rid);
	} catch (JOrbisException e) {
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
