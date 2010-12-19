/* NonValidator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;

public class NonValidator implements IXMLValidator
{
    protected IXMLEntityResolver parameterEntityResolver;
    protected Hashtable attributeDefaultValues = new Hashtable();
    protected Stack currentElements = new Stack();
    
    public NonValidator() {
	parameterEntityResolver = new XMLEntityResolver();
    }
    
    protected void finalize() throws Throwable {
	parameterEntityResolver = null;
	attributeDefaultValues.clear();
	attributeDefaultValues = null;
	currentElements.clear();
	currentElements = null;
	super.finalize();
    }
    
    public void setParameterEntityResolver(IXMLEntityResolver resolver) {
	parameterEntityResolver = resolver;
    }
    
    public IXMLEntityResolver getParameterEntityResolver() {
	return parameterEntityResolver;
    }
    
    public void parseDTD(String publicID, IXMLReader reader,
			 IXMLEntityResolver entityResolver,
			 boolean external) throws Exception {
	XMLUtil.skipWhitespace(reader, null);
	int origLevel = reader.getStreamLevel();
	for (;;) {
	    String str = XMLUtil.read(reader, '%');
	    char ch = str.charAt(0);
	    if (ch == '%')
		XMLUtil.processEntity(str, reader, parameterEntityResolver);
	    else {
		if (ch == '<')
		    processElement(reader, entityResolver);
		else {
		    if (ch == ']')
			break;
		    XMLUtil.errorInvalidInput(reader.getSystemID(),
					      reader.getLineNr(), str);
		}
		do {
		    ch = reader.read();
		    if (external && reader.getStreamLevel() < origLevel) {
			reader.unread(ch);
			return;
		    }
		} while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r');
		reader.unread(ch);
	    }
	}
    }
    
    protected void processElement
	(IXMLReader reader, IXMLEntityResolver entityResolver)
	throws Exception {
	String str = XMLUtil.read(reader, '%');
	char ch = str.charAt(0);
	if (ch != '!')
	    XMLUtil.skipTag(reader);
	else {
	    str = XMLUtil.read(reader, '%');
	    ch = str.charAt(0);
	    switch (ch) {
	    case '-':
		XMLUtil.skipComment(reader);
		break;
	    case '[':
		processConditionalSection(reader, entityResolver);
		break;
	    case 'E':
		processEntity(reader, entityResolver);
		break;
	    case 'A':
		processAttList(reader, entityResolver);
		break;
	    default:
		XMLUtil.skipTag(reader);
	    }
	}
    }
    
    protected void processConditionalSection
	(IXMLReader reader, IXMLEntityResolver entityResolver)
	throws Exception {
	XMLUtil.skipWhitespace(reader, null);
	String str = XMLUtil.read(reader, '%');
	char ch = str.charAt(0);
	if (ch != 'I')
	    XMLUtil.skipTag(reader);
	else {
	    str = XMLUtil.read(reader, '%');
	    ch = str.charAt(0);
	    switch (ch) {
	    case 'G':
		processIgnoreSection(reader, entityResolver);
		return;
	    case 'N':
		break;
	    default:
		XMLUtil.skipTag(reader);
		return;
	    }
	    if (!XMLUtil.checkLiteral(reader, "CLUDE"))
		XMLUtil.skipTag(reader);
	    else {
		XMLUtil.skipWhitespace(reader, null);
		str = XMLUtil.read(reader, '%');
		ch = str.charAt(0);
		if (ch != '[')
		    XMLUtil.skipTag(reader);
		else {
		    java.io.Reader subreader = new CDATAReader(reader);
		    StringBuffer buf = new StringBuffer(1024);
		    for (;;) {
			int ch2 = subreader.read();
			if (ch2 < 0)
			    break;
			buf.append((char) ch2);
		    }
		    subreader.close();
		    reader.startNewStream(new StringReader(buf.toString()));
		}
	    }
	}
    }
    
    protected void processIgnoreSection
	(IXMLReader reader, IXMLEntityResolver entityResolver)
	throws Exception {
	if (!XMLUtil.checkLiteral(reader, "NORE"))
	    XMLUtil.skipTag(reader);
	else {
	    XMLUtil.skipWhitespace(reader, null);
	    String str = XMLUtil.read(reader, '%');
	    char ch = str.charAt(0);
	    if (ch != '[')
		XMLUtil.skipTag(reader);
	    else {
		java.io.Reader subreader = new CDATAReader(reader);
		subreader.close();
	    }
	}
    }
    
    protected void processAttList
	(IXMLReader reader, IXMLEntityResolver entityResolver)
	throws Exception {
	if (!XMLUtil.checkLiteral(reader, "TTLIST"))
	    XMLUtil.skipTag(reader);
	else {
	    XMLUtil.skipWhitespace(reader, null);
	    String str = XMLUtil.read(reader, '%');
	    char ch;
	    for (ch = str.charAt(0); ch == '%'; ch = str.charAt(0)) {
		XMLUtil.processEntity(str, reader, parameterEntityResolver);
		str = XMLUtil.read(reader, '%');
	    }
	    reader.unread(ch);
	    String elementName = XMLUtil.scanIdentifier(reader);
	    XMLUtil.skipWhitespace(reader, null);
	    str = XMLUtil.read(reader, '%');
	    for (ch = str.charAt(0); ch == '%'; ch = str.charAt(0)) {
		XMLUtil.processEntity(str, reader, parameterEntityResolver);
		str = XMLUtil.read(reader, '%');
	    }
	    Properties props = new Properties();
	    while (ch != '>') {
		reader.unread(ch);
		String attName = XMLUtil.scanIdentifier(reader);
		XMLUtil.skipWhitespace(reader, null);
		str = XMLUtil.read(reader, '%');
		for (ch = str.charAt(0); ch == '%'; ch = str.charAt(0)) {
		    XMLUtil.processEntity(str, reader,
					  parameterEntityResolver);
		    str = XMLUtil.read(reader, '%');
		}
		if (ch == '(') {
		    while (ch != ')') {
			str = XMLUtil.read(reader, '%');
			for (ch = str.charAt(0); ch == '%';
			     ch = str.charAt(0)) {
			    XMLUtil.processEntity(str, reader,
						  parameterEntityResolver);
			    str = XMLUtil.read(reader, '%');
			}
		    }
		} else {
		    reader.unread(ch);
		    XMLUtil.scanIdentifier(reader);
		}
		XMLUtil.skipWhitespace(reader, null);
		str = XMLUtil.read(reader, '%');
		for (ch = str.charAt(0); ch == '%'; ch = str.charAt(0)) {
		    XMLUtil.processEntity(str, reader,
					  parameterEntityResolver);
		    str = XMLUtil.read(reader, '%');
		}
		if (ch == '#') {
		    str = XMLUtil.scanIdentifier(reader);
		    XMLUtil.skipWhitespace(reader, null);
		    if (!str.equals("FIXED")) {
			XMLUtil.skipWhitespace(reader, null);
			str = XMLUtil.read(reader, '%');
			for (ch = str.charAt(0); ch == '%';
			     ch = str.charAt(0)) {
			    XMLUtil.processEntity(str, reader,
						  parameterEntityResolver);
			    str = XMLUtil.read(reader, '%');
			}
			continue;
		    }
		} else
		    reader.unread(ch);
		String value
		    = XMLUtil.scanString(reader, '%', parameterEntityResolver);
		props.put(attName, value);
		XMLUtil.skipWhitespace(reader, null);
		str = XMLUtil.read(reader, '%');
		for (ch = str.charAt(0); ch == '%'; ch = str.charAt(0)) {
		    XMLUtil.processEntity(str, reader,
					  parameterEntityResolver);
		    str = XMLUtil.read(reader, '%');
		}
	    }
	    if (!props.isEmpty())
		attributeDefaultValues.put(elementName, props);
	}
    }
    
    protected void processEntity
	(IXMLReader reader, IXMLEntityResolver entityResolver)
	throws Exception {
	if (!XMLUtil.checkLiteral(reader, "NTITY"))
	    XMLUtil.skipTag(reader);
	else {
	    XMLUtil.skipWhitespace(reader, null);
	    char ch = XMLUtil.readChar(reader, '\0');
	    if (ch == '%') {
		XMLUtil.skipWhitespace(reader, null);
		entityResolver = parameterEntityResolver;
	    } else
		reader.unread(ch);
	    String key = XMLUtil.scanIdentifier(reader);
	    XMLUtil.skipWhitespace(reader, null);
	    ch = XMLUtil.readChar(reader, '%');
	    String systemID = null;
	    String publicID = null;
	    switch (ch) {
	    case 'P':
		if (!XMLUtil.checkLiteral(reader, "UBLIC"))
		    XMLUtil.skipTag(reader);
		else {
		    XMLUtil.skipWhitespace(reader, null);
		    publicID = XMLUtil.scanString(reader, '%',
						  parameterEntityResolver);
		    XMLUtil.skipWhitespace(reader, null);
		    systemID = XMLUtil.scanString(reader, '%',
						  parameterEntityResolver);
		    XMLUtil.skipWhitespace(reader, null);
		    XMLUtil.readChar(reader, '%');
		    break;
		}
		return;
	    case 'S':
		if (!XMLUtil.checkLiteral(reader, "YSTEM"))
		    XMLUtil.skipTag(reader);
		else {
		    XMLUtil.skipWhitespace(reader, null);
		    systemID = XMLUtil.scanString(reader, '%',
						  parameterEntityResolver);
		    XMLUtil.skipWhitespace(reader, null);
		    XMLUtil.readChar(reader, '%');
		    break;
		}
		return;
	    case '\"':
	    case '\'': {
		reader.unread(ch);
		String value
		    = XMLUtil.scanString(reader, '%', parameterEntityResolver);
		entityResolver.addInternalEntity(key, value);
		XMLUtil.skipWhitespace(reader, null);
		XMLUtil.readChar(reader, '%');
		break;
	    }
	    default:
		XMLUtil.skipTag(reader);
	    }
	    if (systemID != null)
		entityResolver.addExternalEntity(key, publicID, systemID);
	}
    }
    
    public void elementStarted(String name, String systemId, int lineNr) {
	Properties attribs = (Properties) attributeDefaultValues.get(name);
	if (attribs == null)
	    attribs = new Properties();
	else
	    attribs = (Properties) attribs.clone();
	currentElements.push(attribs);
    }
    
    public void elementEnded(String name, String systemId, int lineNr) {
	/* empty */
    }
    
    public void elementAttributesProcessed(String name,
					   Properties extraAttributes,
					   String systemId, int lineNr) {
	Properties props = (Properties) currentElements.pop();
	Enumeration keysEnum = props.keys();
	while (keysEnum.hasMoreElements()) {
	    String key = (String) keysEnum.nextElement();
	    extraAttributes.put(key, props.get(key));
	}
    }
    
    public void attributeAdded(String key, String value, String systemId,
			       int lineNr) {
	Properties props = (Properties) currentElements.peek();
	if (props.containsKey(key))
	    props.remove(key);
    }
    
    public void PCDataAdded(String systemId, int lineNr) {
	/* empty */
    }
}
