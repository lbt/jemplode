/* StdXMLBuilder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

public class StdXMLBuilder implements IXMLBuilder
{
    private Stack stack = null;
    private IXMLElement root = null;
    private IXMLElement prototype;
    
    public StdXMLBuilder() {
	this(new XMLElement());
    }
    
    public StdXMLBuilder(IXMLElement prototype) {
	this.prototype = prototype;
    }
    
    protected void finalize() throws Throwable {
	prototype = null;
	root = null;
	stack.clear();
	stack = null;
	super.finalize();
    }
    
    public void startBuilding(String systemID, int lineNr) {
	stack = new Stack();
	root = null;
    }
    
    public void newProcessingInstruction(String target, Reader reader) {
	/* empty */
    }
    
    public void startElement(String name, String nsPrefix, String nsURI,
			     String systemID, int lineNr) {
	String fullName = name;
	if (nsPrefix != null)
	    fullName = nsPrefix + ':' + name;
	IXMLElement elt
	    = prototype.createElement(fullName, nsURI, systemID, lineNr);
	if (stack.empty())
	    root = elt;
	else {
	    IXMLElement top = (IXMLElement) stack.peek();
	    top.addChild(elt);
	}
	stack.push(elt);
    }
    
    public void elementAttributesProcessed(String name, String nsPrefix,
					   String nsURI) {
	/* empty */
    }
    
    public void endElement(String name, String nsPrefix, String nsURI) {
	IXMLElement elt = (IXMLElement) stack.pop();
	if (elt.getChildrenCount() == 1) {
	    IXMLElement child = elt.getChildAtIndex(0);
	    if (child.getName() == null) {
		elt.setContent(child.getContent());
		elt.removeChildAtIndex(0);
	    }
	}
    }
    
    public void addAttribute(String key, String nsPrefix, String nsURI,
			     String value, String type) throws Exception {
	String fullName = key;
	if (nsPrefix != null)
	    fullName = nsPrefix + ':' + key;
	IXMLElement top = (IXMLElement) stack.peek();
	if (top.hasAttribute(fullName))
	    throw new XMLParseException(top.getSystemID(), top.getLineNr(),
					"Duplicate attribute: " + key);
	if (nsPrefix != null)
	    top.setAttribute(fullName, nsURI, value);
	else
	    top.setAttribute(fullName, value);
    }
    
    public void addPCData(Reader reader, String systemID, int lineNr) {
	int bufSize = 2048;
	int sizeRead = 0;
	StringBuffer str = new StringBuffer();
	char[] buf = new char[bufSize];
	for (;;) {
	    int size;
	    try {
		size = reader.read(buf);
	    } catch (IOException e) {
		break;
	    }
	    if (size < 0)
		break;
	    str.append(buf, 0, size);
	    sizeRead += size;
	}
	IXMLElement elt = prototype.createElement(null, systemID, lineNr);
	elt.setContent(str.toString());
	if (!stack.empty()) {
	    IXMLElement top = (IXMLElement) stack.peek();
	    top.addChild(elt);
	}
    }
    
    public Object getResult() {
	return root;
    }
}
