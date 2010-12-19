package org.jempeg.tags;

import java.io.File;
import java.io.IOException;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

public interface ITagWriter {
	public boolean isTagWriterFor(IFIDNode _node);
	
	public void writeTags(IFIDNode _node, FIDPlaylist _parentPlaylist, File _file) throws IOException;
}
