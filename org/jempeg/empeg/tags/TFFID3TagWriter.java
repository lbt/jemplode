/* TFFID3TagWriter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.tags;
import java.io.File;
import java.io.IOException;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;
import com.tffenterprises.music.io.TaggedFile;
import com.tffenterprises.music.tag.ID3Genres;
import com.tffenterprises.music.tag.ID3v1;
import com.tffenterprises.music.tag.ID3v2;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.model.TagValueRetriever;
import org.jempeg.tags.ITagWriter;

public class TFFID3TagWriter implements ITagWriter
{
    public boolean isTagWriterFor(IFIDNode _node) {
	return "mp3".equals(_node.getTags().getValue("codec"));
    }
    
    public void writeTags(IFIDNode _node, FIDPlaylist _parentPlaylist,
			  File _file) throws IOException {
	TaggedFile taggedFile = null;
	do {
	    try {
		try {
		    taggedFile = new TaggedFile(_file, TaggedFile.ReadWrite);
		    ID3v2 id3v2Tag = taggedFile.getID3v2();
		    PropertiesManager propertiesManager
			= PropertiesManager.getInstance();
		    if (propertiesManager
			    .getBooleanProperty("jempeg.writeID3v2")) {
			Debug.println(2, "Preparing ID3v2 Tag");
			try {
			    if (id3v2Tag == null
				|| !(propertiesManager.getBooleanProperty
				     ("jempeg.preserveID3v2"))) {
				Debug.println(2, "Creating New ID3v2 Tag");
				id3v2Tag = new ID3v2();
			    } else
				Debug.println(2,
					      "Preserving Old ID3v2 Frames");
			    NodeTags nodeTags = _node.getTags();
			    id3v2Tag.setTitle(nodeTags.getValue("title"));
			    id3v2Tag.setArtist(nodeTags.getValue("artist"));
			    id3v2Tag.setComment(nodeTags.getValue("comment"));
			    id3v2Tag.setGenre(ID3Genres.StringToByte
					      (nodeTags.getValue("genre")));
			    id3v2Tag.setAlbum(nodeTags.getValue("source"));
			    try {
				id3v2Tag.setYear
				    (nodeTags.getShortValue("year",
							    (short) 0));
			    } catch (Throwable throwable) {
				/* empty */
			    }
			    try {
				String trackNumberStr
				    = (TagValueRetriever.getValue
				       (_parentPlaylist,
					_parentPlaylist.getIndexOf(_node),
					"tracknrpos"));
				int trackNumber
				    = Integer.parseInt(trackNumberStr);
				id3v2Tag.setTrackNumber((byte) trackNumber);
			    } catch (Throwable throwable) {
				/* empty */
			    }
			    Debug.println(2,
					  "Writing ID3v2 Tag:\n" + id3v2Tag);
			    taggedFile.writeID3v2(id3v2Tag);
			} catch (Throwable t) {
			    t.printStackTrace();
			    Debug.println(t);
			}
		    }
		    if (propertiesManager
			    .getBooleanProperty("jempeg.removeID3v1")) {
			Debug.println(2, "Removing ID3v1 Tag (if any)");
			try {
			    taggedFile.removeID3v1();
			    break;
			} catch (Throwable t) {
			    Debug.println(t);
			    break;
			}
		    }
		    if (taggedFile.hasID3v1() && id3v2Tag != null) {
			ID3v1 id3v1Tag = new ID3v1(id3v2Tag);
			if (id3v1Tag != null)
			    taggedFile.writeID3v1(id3v1Tag);
		    }
		} catch (Throwable t) {
		    t.printStackTrace();
		    Debug.println(t);
		}
	    } catch (Object object) {
		if (taggedFile != null)
		    taggedFile.close();
		throw object;
	    }
	} while (false);
	if (taggedFile != null)
	    taggedFile.close();
    }
}
