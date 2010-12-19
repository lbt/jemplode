/* TMSXMLExporter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.exporter;
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

public class TMSXMLExporter extends AbstractXMLExporter
{
    private String myEncoding;
    private static final String prolog
	= "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<!DOCTYPE player [\n\n<!ELEMENT player (playlist|tune)* >\n<!ATTLIST  player\n             name       CDATA   #IMPLIED  >\n\n<!ELEMENT  playlist (attribute*) >\n<!ATTLIST  playlist\n             id         ID      #REQUIRED\n             content    IDREFS  #IMPLIED  >\n\n<!ELEMENT  tune (attribute*) >\n<!ATTLIST  tune\n             id         ID      #REQUIRED >\n\n<!ELEMENT  attribute (#PCDATA) >\n<!ATTLIST  attribute\n             name       CDATA   #REQUIRED >\n\n<!ENTITY lt     \"&#38;#60;\">\n<!ENTITY gt     \"&#62;\">\n<!ENTITY amp    \"&#38;#38;\">\n<!ENTITY apos   \"&#39;\">\n<!ENTITY quot   \"&#34;\">\n\n]>\n\n";
    
    public TMSXMLExporter() {
	this("ISO-8859-1");
    }
    
    public TMSXMLExporter(String _encoding) {
	myEncoding = _encoding;
    }
    
    public void write(PlayerDatabase _db, OutputStream _outputStream)
	throws IOException {
	PrintWriter pw = new PrintWriter(new OutputStreamWriter(_outputStream,
								myEncoding));
	pw.println
	    ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<!DOCTYPE player [\n\n<!ELEMENT player (playlist|tune)* >\n<!ATTLIST  player\n             name       CDATA   #IMPLIED  >\n\n<!ELEMENT  playlist (attribute*) >\n<!ATTLIST  playlist\n             id         ID      #REQUIRED\n             content    IDREFS  #IMPLIED  >\n\n<!ELEMENT  tune (attribute*) >\n<!ATTLIST  tune\n             id         ID      #REQUIRED >\n\n<!ELEMENT  attribute (#PCDATA) >\n<!ATTLIST  attribute\n             name       CDATA   #REQUIRED >\n\n<!ENTITY lt     \"&#38;#60;\">\n<!ENTITY gt     \"&#62;\">\n<!ENTITY amp    \"&#38;#38;\">\n<!ENTITY apos   \"&#39;\">\n<!ENTITY quot   \"&#34;\">\n\n]>\n\n");
	IDeviceSettings configFile = _db.getDeviceSettings();
	String name = configFile.getName();
	pw.println("<player name=\"" + name + "\">");
	FIDNodeMap nodeMap = _db.getNodeMap();
	Enumeration keys = nodeMap.keys();
	while (keys.hasMoreElements()) {
	    Long fid = (Long) keys.nextElement();
	    IFIDNode node = nodeMap.getNode(fid.longValue());
	    write(_db, pw, node);
	}
	pw.println("</player>");
	pw.flush();
    }
    
    protected void write(PlayerDatabase _db, PrintWriter _writer,
			 IFIDNode _node) throws IOException {
	boolean isplaylist = _node instanceof FIDPlaylist;
	String type = _node.getTags().getValue("type");
	String fidStr = Long.toHexString(_node.getFID());
	if (isplaylist) {
	    _writer.print("  <" + type + " id=\"x" + fidStr + "\"");
	    FIDPlaylist playlist = (FIDPlaylist) _node;
	    int size = playlist.size();
	    if (size > 0) {
		_writer.print(" content=\"");
		for (int i = 0; i < size; i++) {
		    long childFid = playlist.getFIDAt(i);
		    String childFidStr = Long.toHexString(childFid);
		    _writer.print((i == 0 ? "x" : " x") + childFidStr);
		}
		_writer.print("\"");
	    }
	    _writer.println(">");
	} else
	    _writer.println("  <" + type + " id=\"x" + fidStr + "\">");
	NodeTags tags = _node.getTags();
	Enumeration tagNames = tags.getTagNames();
	while (tagNames.hasMoreElements()) {
	    String tagName = (String) tagNames.nextElement();
	    String tagValue = encode(tags.getValue(tagName));
	    _writer.println("    <attribute name=\"" + tagName + "\">"
			    + tagValue + "</attribute>");
	}
	_writer.println("  </" + type + ">");
	_writer.println();
    }
}
