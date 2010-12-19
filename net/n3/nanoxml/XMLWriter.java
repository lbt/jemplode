/* XMLWriter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

public class XMLWriter
{
    private PrintWriter writer;
    
    public XMLWriter(Writer writer) {
	if (writer instanceof PrintWriter)
	    this.writer = (PrintWriter) writer;
	else
	    this.writer = new PrintWriter(writer);
    }
    
    public XMLWriter(OutputStream stream) {
	writer = new PrintWriter(stream);
    }
    
    protected void finalize() throws Throwable {
	writer = null;
	super.finalize();
    }
    
    public void write(IXMLElement xml) throws IOException {
	write(xml, false, 0, true);
    }
    
    public void write(IXMLElement xml, boolean prettyPrint)
	throws IOException {
	write(xml, prettyPrint, 0, true);
    }
    
    public void write(IXMLElement xml, boolean prettyPrint, int indent)
	throws IOException {
	write(xml, prettyPrint, indent, true);
    }
    
    public void write(IXMLElement xml, boolean prettyPrint, int indent,
		      boolean collapseEmptyElements) throws IOException {
	if (prettyPrint) {
	    for (int i = 0; i < indent; i++)
		writer.print(' ');
	}
	if (xml.getName() == null) {
	    if (xml.getContent() != null) {
		if (prettyPrint) {
		    writeEncoded(xml.getContent().trim());
		    writer.println();
		} else
		    writeEncoded(xml.getContent());
	    }
	} else {
	    writer.print('<');
	    writer.print(xml.getFullName());
	    Vector nsprefixes = new Vector();
	    if (xml.getNamespace() != null) {
		if (xml.getName().equals(xml.getFullName()))
		    writer.print(" xmlns=\"" + xml.getNamespace() + '\"');
		else {
		    String prefix = xml.getFullName();
		    prefix = prefix.substring(0, prefix.indexOf(':'));
		    nsprefixes.addElement(prefix);
		    writer.print(" xmlns:" + prefix);
		    writer.print("=\"" + xml.getNamespace() + "\"");
		}
	    }
	    Enumeration enum = xml.enumerateAttributeNames();
	    while (enum.hasMoreElements()) {
		String key = (String) enum.nextElement();
		int index = key.indexOf(':');
		if (index >= 0) {
		    String namespace = xml.getAttributeNamespace(key);
		    if (namespace != null) {
			String prefix = key.substring(0, index);
			if (!nsprefixes.contains(prefix)) {
			    writer.print(" xmlns:" + prefix);
			    writer.print("=\"" + namespace + '\"');
			    nsprefixes.addElement(prefix);
			}
		    }
		}
	    }
	    enum = xml.enumerateAttributeNames();
	    while (enum.hasMoreElements()) {
		String key = (String) enum.nextElement();
		String value = xml.getAttribute(key, (String) null);
		writer.print(" " + key + "=\"");
		writeEncoded(value);
		writer.print('\"');
	    }
	    if (xml.getContent() != null && xml.getContent().length() > 0) {
		writer.print('>');
		writeEncoded(xml.getContent());
		writer.print("</" + xml.getFullName() + '>');
		if (prettyPrint)
		    writer.println();
	    } else if (xml.hasChildren() || !collapseEmptyElements) {
		writer.print('>');
		if (prettyPrint)
		    writer.println();
		enum = xml.enumerateChildren();
		while (enum.hasMoreElements()) {
		    IXMLElement child = (IXMLElement) enum.nextElement();
		    write(child, prettyPrint, indent + 4,
			  collapseEmptyElements);
		}
		if (prettyPrint) {
		    for (int i = 0; i < indent; i++)
			writer.print(' ');
		}
		writer.print("</" + xml.getFullName() + ">");
		if (prettyPrint)
		    writer.println();
	    } else {
		writer.print("/>");
		if (prettyPrint)
		    writer.println();
	    }
	}
	writer.flush();
    }
    
    private void writeEncoded(String str) {
	for (int i = 0; i < str.length(); i++) {
	    char c = str.charAt(i);
	    switch (c) {
	    case '\n':
		writer.print(c);
		break;
	    case '<':
		writer.print("&lt;");
		break;
	    case '>':
		writer.print("&gt;");
		break;
	    case '&':
		writer.print("&amp;");
		break;
	    case '\'':
		writer.print("&apos;");
		break;
	    case '\"':
		writer.print("&quot;");
		break;
	    default:
		if (c < ' ' || c > '~') {
		    writer.print("&#x");
		    writer.print(Integer.toString(c, 16));
		    writer.print(';');
		} else
		    writer.print(c);
	    }
	}
    }
}
