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
package org.jempeg.nodestore.exporter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDNodeMap;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.PlayerDatabase;

/**
* XML Exporter can produce an XML document from a PlayerDatabase that
* contains all of the FIDs in the database.
*
* @author Toby Speight
* @version $Revision: 1.4 $
*/
public class TMSXMLExporter extends AbstractXMLExporter {
	private String myEncoding;
	
	public TMSXMLExporter() {
		this("ISO-8859-1");
	}

	public TMSXMLExporter(String _encoding) {
		myEncoding = _encoding;
	}

	public void write(PlayerDatabase _db, OutputStream _outputStream) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(_outputStream, myEncoding));
		pw.println(prolog);
		
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

	protected void write(PlayerDatabase _db, PrintWriter _writer, IFIDNode _node) throws IOException {
		boolean isplaylist = (_node instanceof FIDPlaylist);
		String type = _node.getTags().getValue(DatabaseTags.TYPE_TAG);
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
		}
		else {
			// is a tune
			_writer.println("  <" + type + " id=\"x" + fidStr + "\">");
		}

		NodeTags tags = _node.getTags();
		Enumeration tagNames = tags.getTagNames();
		while (tagNames.hasMoreElements()) {
			String tagName = (String)tagNames.nextElement();
			String tagValue = encode(tags.getValue(tagName));
			_writer.println("    <attribute name=\"" + tagName + "\">" + tagValue + "</attribute>");
		}
		_writer.println("  </" + type + ">");
		_writer.println();
	}

	private static final String prolog = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + "<!DOCTYPE player [\n" + "\n" + "<!ELEMENT player (playlist|tune)* >\n" + "<!ATTLIST  player\n" + "             name       CDATA   #IMPLIED  >\n" + "\n" + "<!ELEMENT  playlist (attribute*) >\n" + "<!ATTLIST  playlist\n" + "             id         ID      #REQUIRED\n" + "             content    IDREFS  #IMPLIED  >\n" + "\n" + "<!ELEMENT  tune (attribute*) >\n" + "<!ATTLIST  tune\n" + "             id         ID      #REQUIRED >\n" + "\n" + "<!ELEMENT  attribute (#PCDATA) >\n" + "<!ATTLIST  attribute\n" + "             name       CDATA   #REQUIRED >\n" + "\n" + "<!ENTITY lt     \"&#38;#60;\">\n" + "<!ENTITY gt     \"&#62;\">\n" + "<!ENTITY amp    \"&#38;#38;\">\n" + "<!ENTITY apos   \"&#39;\">\n" + "<!ENTITY quot   \"&#34;\">\n" + "\n" + "]>\n" + "\n";
}
