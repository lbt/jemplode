package org.jempeg.empeg.tags;

import java.io.File;
import java.io.IOException;

import org.jempeg.JEmplodeProperties;
import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.model.TagValueRetriever;
import org.jempeg.tags.ITagWriter;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;
import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3Genres;
import com.tffenterprises.music.tag.ID3v1;
import com.tffenterprises.music.tag.ID3v2;

public class TFFID3TagWriter implements ITagWriter {
	public TFFID3TagWriter() {
	}

	public boolean isTagWriterFor(IFIDNode _node) {
		return DatabaseTags.CODEC_MP3.equals(_node.getTags().getValue(DatabaseTags.CODEC_TAG));
	}

	public void writeTags(IFIDNode _node, FIDPlaylist _parentPlaylist, File _file) throws IOException {
		TaggedFile taggedFile = null;
		try {
			taggedFile = new TaggedFile(_file, TaggedFile.ReadWrite);
			ID3v2 id3v2Tag = taggedFile.getID3v2();
			
			PropertiesManager propertiesManager = PropertiesManager.getInstance();
			if (propertiesManager.getBooleanProperty(JEmplodeProperties.WRITE_ID3v2_PROPERTY)) {
				Debug.println(Debug.VERBOSE, "Preparing ID3v2 Tag");

				try {
					if (id3v2Tag == null || !propertiesManager.getBooleanProperty(JEmplodeProperties.PRESERVE_ID3v2_PROPERTY)) {
						Debug.println(Debug.VERBOSE, "Creating New ID3v2 Tag");
						id3v2Tag = new ID3v2();
					}
					else {
						Debug.println(Debug.VERBOSE, "Preserving Old ID3v2 Frames");
					}

					NodeTags nodeTags = _node.getTags();
					id3v2Tag.setTitle(nodeTags.getValue(DatabaseTags.TITLE_TAG));
					id3v2Tag.setArtist(nodeTags.getValue(DatabaseTags.ARTIST_TAG));
					id3v2Tag.setComment(nodeTags.getValue(DatabaseTags.COMMENT_TAG));
					id3v2Tag.setGenre(ID3Genres.StringToByte(nodeTags.getValue(DatabaseTags.GENRE_TAG)));
					id3v2Tag.setAlbum(nodeTags.getValue(DatabaseTags.SOURCE_TAG));

					try {
						id3v2Tag.setYear(nodeTags.getShortValue(DatabaseTags.YEAR_TAG, (short)0));
					}
					catch (Throwable e) {
					}

					try {
						String trackNumberStr = TagValueRetriever.getValue(_parentPlaylist, _parentPlaylist.getIndexOf(_node), DatabaseTags.TRACKNR_OR_POS_TAG);
						int trackNumber = Integer.parseInt(trackNumberStr);
						id3v2Tag.setTrackNumber((byte) trackNumber);
					}
					catch (Throwable e) {
					}

					Debug.println(Debug.VERBOSE, "Writing ID3v2 Tag:\n" + id3v2Tag);
					taggedFile.writeID3v2(id3v2Tag);
					//												System.out.println("Wrote: " + id3v2Tag);
				}
				catch (Throwable t) {
					t.printStackTrace();
					Debug.println(t);
				}
			}
			if (propertiesManager.getBooleanProperty(JEmplodeProperties.REMOVE_ID3v1_PROPERTY)) {
				Debug.println(Debug.VERBOSE, "Removing ID3v1 Tag (if any)");
				try {
					taggedFile.removeID3v1();
				}
				catch (Throwable t) {
					Debug.println(t);
				}
			}
			else if (taggedFile.hasID3v1() && (id3v2Tag != null)) {
				ID3v1 id3v1Tag = new ID3v1(id3v2Tag);
				if (id3v1Tag != null) {
					taggedFile.writeID3v1(id3v1Tag);
				}
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
			Debug.println(t);
		}
		finally {
			if (taggedFile != null) {
				taggedFile.close();
			}
		}
	}

}
