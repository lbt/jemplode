/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package org.jempeg.empeg.exporter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDConstants;
import org.jempeg.nodestore.FIDNodeMap;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.exporter.IExporter;

import com.inzyme.text.StringUtils;

/**
* CSV Exporter can produce a Comma-Separated-Values
* document from a PlayerDatabase that contains
* all of the FID's in the database.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class CSVExporter implements IExporter {
	public CSVExporter() {
	}

	public void write(PlayerDatabase _db, OutputStream _outputStream) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(_outputStream, "ISO-8859-1"));
		pw.println("\"fid\",\"type\",\"length\",\"title\",\"artist\",\"bitrate\",\"codec\",\"duration\",\"genre\",\"offset\",\"samplerate\",\"source\",\"tracknr\",\"year\",\"comment\",\"refs\",\"contents\"");
		
		FIDNodeMap nodeMap = _db.getNodeMap();
		Enumeration keys = nodeMap.keys();
		while (keys.hasMoreElements()) {
			Long fid = (Long)keys.nextElement();
			IFIDNode node = nodeMap.getNode(fid.longValue());
			write(_db, pw, node);
		}
    
    pw.flush();
	}

	protected void write(PlayerDatabase _db, PrintWriter _writer, IFIDNode _node) throws IOException {
		boolean isplaylist = (_node instanceof FIDPlaylist);
		write(_writer, String.valueOf(FIDConstants.getFIDNumber((int)_node.getFID())), false); // casting :(
		
		NodeTags tags = _node.getTags();
		write(_writer, tags.getValue(DatabaseTags.TYPE_TAG));
		write(_writer, tags.getValue(DatabaseTags.LENGTH_TAG));
		write(_writer, tags.getValue(DatabaseTags.TITLE_TAG));
		write(_writer, tags.getValue(DatabaseTags.ARTIST_TAG));
		write(_writer, tags.getValue(DatabaseTags.BITRATE_TAG));
		write(_writer, tags.getValue(DatabaseTags.CODEC_TAG));
		write(_writer, tags.getValue(DatabaseTags.DURATION_TAG));
		write(_writer, tags.getValue(DatabaseTags.GENRE_TAG));
		write(_writer, tags.getValue(DatabaseTags.OFFSET_TAG));
		write(_writer, tags.getValue(DatabaseTags.SAMPLERATE_TAG));
		write(_writer, tags.getValue(DatabaseTags.SOURCE_TAG));
		write(_writer, tags.getValue(DatabaseTags.TRACKNR_TAG));
		write(_writer, tags.getValue(DatabaseTags.YEAR_TAG));
		write(_writer, tags.getValue(DatabaseTags.COMMENT_TAG));
		write(_writer, String.valueOf(_node.getReferenceCount()), false);
		_writer.print("\"");
		if (isplaylist) {
			FIDPlaylist playlist = (FIDPlaylist)_node;
			int size = playlist.size();
			for (int i = 0; i < size; i ++) {
				long fid = playlist.getFIDAt(i);
				_writer.print(FIDConstants.getFIDNumber((int)fid)); // casting :(
				if (i != size - 1) {
					_writer.print(",");
				}
			}
		}
		_writer.println("\"");
	}

	protected void write(PrintWriter _writer, String _str) throws IOException {
		write(_writer, _str, true);
	}
	
	protected void write(PrintWriter _writer, String _str, boolean _quotes) throws IOException {
    _str = StringUtils.replace(_str, "\"", "\\\"");
		if (_quotes) {
			_writer.print("\"");
			_writer.print(_str);
			_writer.print("\"");
		} else {
			_writer.print(_str);
		}
		_writer.print(",");
	}
}

