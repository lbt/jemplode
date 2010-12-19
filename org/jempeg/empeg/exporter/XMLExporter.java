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
import org.jempeg.nodestore.FIDNodeMap;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.exporter.AbstractXMLExporter;

/**
* XM<L Exporter can produce an XML 
* document from a PlayerDatabase that contains
* all of the FID's in the database.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class XMLExporter extends AbstractXMLExporter {
	public XMLExporter() {
	}

	public void write(PlayerDatabase _db, OutputStream _outputStream) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(_outputStream, "ISO-8859-1"));
    pw.println("<?xml version=\"1.0\"?>");

    IDeviceSettings configFile = _db.getDeviceSettings();
    pw.println("<nodestore player_name=\"" + configFile.getName() + "\">");
    
		FIDNodeMap nodeMap = _db.getNodeMap();
		Enumeration keys = nodeMap.keys();
		while (keys.hasMoreElements()) {
			Long fid = (Long)keys.nextElement();
			IFIDNode node = nodeMap.getNode(fid.longValue());
			write(_db, pw, node);
		}
    pw.println("</nodestore>");
    pw.flush();
	}

	protected void write(PlayerDatabase _db, PrintWriter _writer, IFIDNode _node) throws IOException {
		boolean isplaylist = (_node instanceof FIDPlaylist);
    String type = _node.getTags().getValue(DatabaseTags.TYPE_TAG);
    String fidStr = Long.toHexString(_node.getFID());
    _writer.println("  <" + type + " fid=\"0x" + fidStr + "\">");

    NodeTags tags = _node.getTags();
		Enumeration tagNames = tags.getTagNames();
		while (tagNames.hasMoreElements()) {
			String tagName = (String)tagNames.nextElement();
			String tagValue = encode(tags.getValue(tagName));
      _writer.println("    <" + tagName + ">" + tagValue + "</" + tagName + ">");
    }
    if (isplaylist) {
			FIDPlaylist playlist = (FIDPlaylist)_node;
      _writer.println("    <children>");
			int size = playlist.size();
			for (int i = 0; i < size; i ++) {
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

