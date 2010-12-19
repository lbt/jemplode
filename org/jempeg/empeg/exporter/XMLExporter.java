/* XMLExporter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.exporter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;

import org.jempeg.nodestore.FIDNodeMap;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.exporter.AbstractXMLExporter;

public class XMLExporter extends AbstractXMLExporter
{
    public void write(PlayerDatabase _db, OutputStream _outputStream)
	throws IOException {
	PrintWriter pw = new PrintWriter(new OutputStreamWriter(_outputStream,
								"ISO-8859-1"));
	pw.println("<?xml version=\"1.0\"?>");
	IDeviceSettings configFile = _db.getDeviceSettings();
	pw.println("<nodestore player_name=\"" + configFile.getName() + "\">");
	FIDNodeMap nodeMap = _db.getNodeMap();
	Enumeration keys = nodeMap.keys();
	while (keys.hasMoreElements()) {
	    Long fid = (Long) keys.nextElement();
	    IFIDNode node = nodeMap.getNode(fid.longValue());
	    write(_db, pw, node);
	}
	pw.println("</nodestore>");
	pw.flush();
    }
    
    protected void write(PlayerDatabase _db, PrintWriter _writer,
			 IFIDNode _node) throws IOException {
	boolean isplaylist = _node instanceof FIDPlaylist;
	String type = _node.getTags().getValue("type");
	String fidStr = Long.toHexString(_node.getFID());
	_writer.println("  <" + type + " fid=\"0x" + fidStr + "\">");
	NodeTags tags = _node.getTags();
	Enumeration tagNames = tags.getTagNames();
	while (tagNames.hasMoreElements()) {
	    String tagName = (String) tagNames.nextElement();
	    String tagValue = encode(tags.getValue(tagName));
	    _writer.println("    <" + tagName + ">" + tagValue + "</" + tagName
			    + ">");
	}
	if (isplaylist) {
	    FIDPlaylist playlist = (FIDPlaylist) _node;
	    _writer.println("    <children>");
	    int size = playlist.size();
	    for (int i = 0; i < size; i++) {
		long childFid = playlist.getFIDAt(i);
		String childFidStr = Long.toHexString(childFid);
		_writer.println("      <child>0x" + childFidStr + "</child>");
	    }
	    _writer.println("    </children>");
	}
	_writer.println("  </" + type + ">");
	_writer.println();
    }
}
