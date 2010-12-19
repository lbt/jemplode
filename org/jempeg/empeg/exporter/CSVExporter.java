/* CSVExporter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.exporter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;

import com.inzyme.text.StringUtils;

import org.jempeg.nodestore.FIDConstants;
import org.jempeg.nodestore.FIDNodeMap;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.exporter.IExporter;

public class CSVExporter implements IExporter
{
    public void write(PlayerDatabase _db, OutputStream _outputStream)
	throws IOException {
	PrintWriter pw = new PrintWriter(new OutputStreamWriter(_outputStream,
								"ISO-8859-1"));
	pw.println
	    ("\"fid\",\"type\",\"length\",\"title\",\"artist\",\"bitrate\",\"codec\",\"duration\",\"genre\",\"offset\",\"samplerate\",\"source\",\"tracknr\",\"year\",\"comment\",\"refs\",\"contents\"");
	FIDNodeMap nodeMap = _db.getNodeMap();
	Enumeration keys = nodeMap.keys();
	while (keys.hasMoreElements()) {
	    Long fid = (Long) keys.nextElement();
	    IFIDNode node = nodeMap.getNode(fid.longValue());
	    write(_db, pw, node);
	}
	pw.flush();
    }
    
    protected void write(PlayerDatabase _db, PrintWriter _writer,
			 IFIDNode _node) throws IOException {
	boolean isplaylist = _node instanceof FIDPlaylist;
	write(_writer,
	      String.valueOf(FIDConstants.getFIDNumber((int) _node.getFID())),
	      false);
	NodeTags tags = _node.getTags();
	write(_writer, tags.getValue("type"));
	write(_writer, tags.getValue("length"));
	write(_writer, tags.getValue("title"));
	write(_writer, tags.getValue("artist"));
	write(_writer, tags.getValue("bitrate"));
	write(_writer, tags.getValue("codec"));
	write(_writer, tags.getValue("duration"));
	write(_writer, tags.getValue("genre"));
	write(_writer, tags.getValue("offset"));
	write(_writer, tags.getValue("samplerate"));
	write(_writer, tags.getValue("source"));
	write(_writer, tags.getValue("tracknr"));
	write(_writer, tags.getValue("year"));
	write(_writer, tags.getValue("comment"));
	write(_writer, String.valueOf(_node.getReferenceCount()), false);
	_writer.print("\"");
	if (isplaylist) {
	    FIDPlaylist playlist = (FIDPlaylist) _node;
	    int size = playlist.size();
	    for (int i = 0; i < size; i++) {
		long fid = playlist.getFIDAt(i);
		_writer.print(FIDConstants.getFIDNumber((int) fid));
		if (i != size - 1)
		    _writer.print(",");
	    }
	}
	_writer.println("\"");
    }
    
    protected void write(PrintWriter _writer, String _str) throws IOException {
	write(_writer, _str, true);
    }
    
    protected void write(PrintWriter _writer, String _str, boolean _quotes)
	throws IOException {
	_str = StringUtils.replace(_str, "\"", "\\\"");
	if (_quotes) {
	    _writer.print("\"");
	    _writer.print(_str);
	    _writer.print("\"");
	} else
	    _writer.print(_str);
	_writer.print(",");
    }
}
