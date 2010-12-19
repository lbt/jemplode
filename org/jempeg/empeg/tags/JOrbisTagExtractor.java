package org.jempeg.empeg.tags;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.tags.EmpegBitrate;
import org.jempeg.tags.ITagExtractor;
import org.jempeg.tags.ITagExtractorListener;
import org.jempeg.tags.PropertiesTagExtractorListener;
import org.jempeg.tags.RID;
import org.jempeg.tags.id3.ID3TagExtractor;
import org.jempeg.tags.id3.ID3V1Tag;
import org.jempeg.tags.id3.ID3V2Tag;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.ChainedIOException;
import com.inzyme.io.SeekableInputStream;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.JOrbisException;
import com.jcraft.jorbis.VorbisFile;

public class JOrbisTagExtractor implements ITagExtractor {
	public int isTagExtractorFor(String _name, byte[] _header) {
		int isTagExtractor;
		if (_header[0] == 'O' && _header[1] == 'g' && _header[2] == 'g') {
			isTagExtractor = ITagExtractor.YES;
		}
		else if (ID3TagExtractor.isID3Tag(_header)) {
			if (_name.toLowerCase().endsWith(".ogg")) {
				isTagExtractor = ITagExtractor.YES;
			}
			else {
				isTagExtractor = ITagExtractor.MAYBE;
			}
		}
		else {
			isTagExtractor = ITagExtractor.NO;
		}
		return isTagExtractor;
	}

	public void extractTags(SeekableInputStream _is, ITagExtractorListener _listener) throws IOException {
		try {
			ID3TagExtractor id3TagExtractor = new ID3TagExtractor();
			PropertiesTagExtractorListener tagExtractorListener = new PropertiesTagExtractorListener(); 
			ID3TagExtractor.TagOffsets offsets = id3TagExtractor.extractTagsWithoutFrameInfo(_is, tagExtractorListener);
			Properties id3Tags = tagExtractorListener.getTags();
			ID3V1Tag id3V1Tag = id3TagExtractor.getID3V1Tag();
			ID3V2Tag id3V2Tag = id3TagExtractor.getID3V2Tag();
			_is.seek(offsets.getOffset());

			if (id3V1Tag != null || id3V2Tag != null) {
				id3Tags.put(DatabaseTags.CODEC_TAG, DatabaseTags.CODEC_OGG);
				Enumeration tagNames = id3Tags.keys();
				while (tagNames.hasMoreElements()) {
					String tagName = (String)tagNames.nextElement();
					String tagValue = id3Tags.getProperty(tagName);
					_listener.tagExtracted(tagName, tagValue);
				}
			} else {
				_listener.tagExtracted(DatabaseTags.CODEC_TAG, DatabaseTags.CODEC_OGG);
			}
			
			VorbisFile oggFile = new VorbisFile(_is, null, 0);

			Comment[] comments = oggFile.getComment();
			for (int i = 0; i < comments.length; i++) {
				_listener.tagExtracted(DatabaseTags.TYPE_TAG, DatabaseTags.TYPE_TUNE);
				_listener.tagExtracted(DatabaseTags.TITLE_TAG, comments[i].query("title"));
				_listener.tagExtracted(DatabaseTags.ARTIST_TAG, comments[i].query("artist"));
				_listener.tagExtracted(DatabaseTags.SOURCE_TAG, comments[i].query("album"));
				_listener.tagExtracted(DatabaseTags.GENRE_TAG, comments[i].query("genre"));
				_listener.tagExtracted(DatabaseTags.COMMENT_TAG, comments[i].query("comment"));
				_listener.tagExtracted(DatabaseTags.TRACKNR_TAG, comments[i].query("tracknumber"));
				_listener.tagExtracted(DatabaseTags.YEAR_TAG, comments[i].query("date"));

			}
			EmpegBitrate eb = new EmpegBitrate();
			Info[] info = oggFile.getInfo();
			for (int i = 0; i < info.length; i++) {
				if (info[i].bitrate_upper == info[i].bitrate_lower && info[i].bitrate_upper > 0) {
					eb.setVBR(false);
				}
				else {
					eb.setVBR(true);
				}
				eb.setChannels(info[i].channels);
				eb.setBitsPerSecond(info[i].getBitrate());
				_listener.tagExtracted(DatabaseTags.BITRATE_TAG, eb.toString());
				_listener.tagExtracted(DatabaseTags.SAMPLERATE_TAG, info[i].rate);
				_listener.tagExtracted(DatabaseTags.DURATION_TAG, (int)(oggFile.time_total(-1) * 1000));
			}

			String rid = RID.calculateRid(_is, 0, _is.length());
			_listener.tagExtracted(DatabaseTags.RID_TAG, rid);
		}
		catch (JOrbisException e) {
			throw new ChainedIOException("Failed to parse ogg file.", e);
		}
	}

	public void extractTags(IImportFile _file, ITagExtractorListener _listener) throws IOException {
		_listener.tagExtracted(DatabaseTags.CODEC_TAG, DatabaseTags.CODEC_OGG);

		SeekableInputStream stream = _file.getSeekableInputStream();
		try {
			extractTags(stream, _listener);
		}
		finally {
			stream.close();
		}
	}
}
