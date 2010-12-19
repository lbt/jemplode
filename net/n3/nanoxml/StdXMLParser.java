/* StdXMLParser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class StdXMLParser implements IXMLParser
{
    private IXMLBuilder builder = null;
    private IXMLReader reader;
    private IXMLEntityResolver entityResolver;
    private IXMLValidator validator = null;
    
    public StdXMLParser() {
	reader = null;
	entityResolver = new XMLEntityResolver();
    }
    
    protected void finalize() throws Throwable {
	builder = null;
	reader = null;
	entityResolver = null;
	validator = null;
	super.finalize();
    }
    
    public void setBuilder(IXMLBuilder builder) {
	this.builder = builder;
    }
    
    public IXMLBuilder getBuilder() {
	return builder;
    }
    
    public void setValidator(IXMLValidator validator) {
	this.validator = validator;
    }
    
    public IXMLValidator getValidator() {
	return validator;
    }
    
    public void setResolver(IXMLEntityResolver resolver) {
	entityResolver = resolver;
    }
    
    public IXMLEntityResolver getResolver() {
	return entityResolver;
    }
    
    public void setReader(IXMLReader reader) {
	this.reader = reader;
    }
    
    public IXMLReader getReader() {
	return reader;
    }
    
    public Object parse() throws XMLException {
	try {
	    builder.startBuilding(reader.getSystemID(), reader.getLineNr());
	    scanData();
	    return builder.getResult();
	} catch (XMLException e) {
	    throw e;
	} catch (Exception e) {
	    throw new XMLException(e);
	}
    }
    
    protected void scanData() throws Exception {
	while (!reader.atEOF() && builder.getResult() == null) {
	    String str = XMLUtil.read(reader, '&');
	    char ch = str.charAt(0);
	    if (ch == '&')
		XMLUtil.processEntity(str, reader, entityResolver);
	    else {
		switch (ch) {
		case '<':
		    scanSomeTag(false, null, new Properties());
		    break;
		case '\t':
		case '\n':
		case '\r':
		case ' ':
		    break;
		default:
		    XMLUtil.errorInvalidInput(reader.getSystemID(),
					      reader.getLineNr(),
					      ("`" + ch + "' (0x"
					       + Integer.toHexString(ch)
					       + ')'));
		}
	    }
	}
    }
    
    protected void scanSomeTag(boolean allowCDATA, String defaultNamespace,
			       Properties namespaces) throws Exception {
	String str = XMLUtil.read(reader, '&');
	char ch = str.charAt(0);
	if (ch == '&')
	    XMLUtil.errorUnexpectedEntity(reader.getSystemID(),
					  reader.getLineNr(), str);
	switch (ch) {
	case '?':
	    processPI();
	    break;
	case '!':
	    processSpecialTag(allowCDATA);
	    break;
	default:
	    reader.unread(ch);
	    processElement(defaultNamespace, namespaces);
	}
    }
    
    protected void processPI() throws Exception {
	XMLUtil.skipWhitespace(this.reader, null);
	String target = XMLUtil.scanIdentifier(this.reader);
	XMLUtil.skipWhitespace(this.reader, null);
	Reader reader = new PIReader(this.reader);
	if (!target.equalsIgnoreCase("xml"))
	    builder.newProcessingInstruction(target, reader);
	reader.close();
    }
    
    protected void processSpecialTag(boolean allowCDATA) throws Exception {
	String str = XMLUtil.read(reader, '&');
	char ch = str.charAt(0);
	if (ch == '&')
	    XMLUtil.errorUnexpectedEntity(reader.getSystemID(),
					  reader.getLineNr(), str);
	switch (ch) {
	case '[':
	    if (allowCDATA)
		processCDATA();
	    else
		XMLUtil.errorUnexpectedCDATA(reader.getSystemID(),
					     reader.getLineNr());
	    break;
	case 'D':
	    processDocType();
	    break;
	case '-':
	    XMLUtil.skipComment(reader);
	    break;
	}
    }
    
    protected void processCDATA() throws Exception {
	if (!XMLUtil.checkLiteral(this.reader, "CDATA["))
	    XMLUtil.errorExpectedInput(this.reader.getSystemID(),
				       this.reader.getLineNr(), "<![[CDATA[");
	validator.PCDataAdded(this.reader.getSystemID(),
			      this.reader.getLineNr());
	Reader reader = new CDATAReader(this.reader);
	builder.addPCData(reader, this.reader.getSystemID(),
			  this.reader.getLineNr());
	reader.close();
    }
    
    protected void processDocType() throws Exception {
	if (!XMLUtil.checkLiteral(this.reader, "OCTYPE"))
	    XMLUtil.errorExpectedInput(this.reader.getSystemID(),
				       this.reader.getLineNr(), "<!DOCTYPE");
	else {
	    XMLUtil.skipWhitespace(this.reader, null);
	    String systemID = null;
	    StringBuffer publicID = new StringBuffer();
	    String rootElement = XMLUtil.scanIdentifier(this.reader);
	    XMLUtil.skipWhitespace(this.reader, null);
	    char ch = this.reader.read();
	    if (ch == 'P') {
		systemID = XMLUtil.scanPublicID(publicID, this.reader);
		XMLUtil.skipWhitespace(this.reader, null);
		ch = this.reader.read();
	    } else if (ch == 'S') {
		systemID = XMLUtil.scanSystemID(this.reader);
		XMLUtil.skipWhitespace(this.reader, null);
		ch = this.reader.read();
	    }
	    if (ch == '[') {
		validator.parseDTD(publicID.toString(), this.reader,
				   entityResolver, false);
		XMLUtil.skipWhitespace(this.reader, null);
		ch = this.reader.read();
	    }
	    if (ch != '>')
		XMLUtil.errorExpectedInput(this.reader.getSystemID(),
					   this.reader.getLineNr(), "`>'");
	    if (systemID != null) {
		Reader reader
		    = this.reader.openStream(publicID.toString(), systemID);
		this.reader.startNewStream(reader);
		this.reader.setSystemID(systemID);
		this.reader.setPublicID(publicID.toString());
		validator.parseDTD(publicID.toString(), this.reader,
				   entityResolver, true);
	    }
	}
    }
    
    protected void processElement
	(String defaultNamespace, Properties namespaces) throws Exception {
	String fullName = XMLUtil.scanIdentifier(reader);
	String name = fullName;
	XMLUtil.skipWhitespace(reader, null);
	String prefix = null;
	int colonIndex = name.indexOf(':');
	if (colonIndex > 0) {
	    prefix = name.substring(0, colonIndex);
	    name = name.substring(colonIndex + 1);
	}
	Vector attrNames = new Vector();
	Vector attrValues = new Vector();
	Vector attrTypes = new Vector();
	validator.elementStarted(fullName, reader.getSystemID(),
				 reader.getLineNr());
	char ch;
	for (;;) {
	    ch = reader.read();
	    if (ch == '/' || ch == '>')
		break;
	    reader.unread(ch);
	    processAttribute(attrNames, attrValues, attrTypes);
	    XMLUtil.skipWhitespace(reader, null);
	}
	Properties extraAttributes = new Properties();
	validator.elementAttributesProcessed(fullName, extraAttributes,
					     reader.getSystemID(),
					     reader.getLineNr());
	Enumeration enum = extraAttributes.keys();
	while (enum.hasMoreElements()) {
	    String key = (String) enum.nextElement();
	    String value = extraAttributes.getProperty(key);
	    attrNames.addElement(key);
	    attrValues.addElement(value);
	    attrTypes.addElement("CDATA");
	}
	for (int i = 0; i < attrNames.size(); i++) {
	    String key = (String) attrNames.elementAt(i);
	    String value = (String) attrValues.elementAt(i);
	    String type = (String) attrTypes.elementAt(i);
	    if (key.equals("xmlns"))
		defaultNamespace = value;
	    else if (key.startsWith("xmlns:"))
		namespaces.put(key.substring(6), value);
	}
	if (prefix == null)
	    builder.startElement(name, prefix, defaultNamespace,
				 reader.getSystemID(), reader.getLineNr());
	else
	    builder.startElement(name, prefix, namespaces.getProperty(prefix),
				 reader.getSystemID(), reader.getLineNr());
	for (int i = 0; i < attrNames.size(); i++) {
	    String key = (String) attrNames.elementAt(i);
	    if (!key.startsWith("xmlns")) {
		String value = (String) attrValues.elementAt(i);
		String type = (String) attrTypes.elementAt(i);
		colonIndex = key.indexOf(':');
		if (colonIndex > 0) {
		    String attPrefix = key.substring(0, colonIndex);
		    key = key.substring(colonIndex + 1);
		    builder.addAttribute(key, attPrefix,
					 namespaces.getProperty(attPrefix),
					 value, type);
		} else
		    builder.addAttribute(key, null, null, value, type);
	    }
	}
	if (prefix == null)
	    builder.elementAttributesProcessed(name, prefix, defaultNamespace);
	else
	    builder.elementAttributesProcessed(name, prefix,
					       namespaces.getProperty(prefix));
	if (ch == '/') {
	    if (reader.read() != '>')
		XMLUtil.errorExpectedInput(reader.getSystemID(),
					   reader.getLineNr(), "`>'");
	    validator.elementEnded(name, reader.getSystemID(),
				   reader.getLineNr());
	    if (prefix == null)
		builder.endElement(name, prefix, defaultNamespace);
	    else
		builder.endElement(name, prefix,
				   namespaces.getProperty(prefix));
	} else {
	    StringBuffer buffer = new StringBuffer(16);
	    for (;;) {
		buffer.setLength(0);
		String str;
		for (;;) {
		    XMLUtil.skipWhitespace(reader, buffer);
		    str = XMLUtil.read(reader, '&');
		    if (str.charAt(0) != '&' || str.charAt(1) == '#')
			break;
		    XMLUtil.processEntity(str, reader, entityResolver);
		}
		if (str.charAt(0) == '<') {
		    str = XMLUtil.read(reader, '\0');
		    if (str.charAt(0) == '/') {
			XMLUtil.skipWhitespace(reader, null);
			str = XMLUtil.scanIdentifier(reader);
			if (!str.equals(fullName))
			    XMLUtil.errorWrongClosingTag(reader.getSystemID(),
							 reader.getLineNr(),
							 name, str);
			XMLUtil.skipWhitespace(reader, null);
			if (reader.read() != '>')
			    XMLUtil.errorClosingTagNotEmpty(reader
								.getSystemID(),
							    reader
								.getLineNr());
			validator.elementEnded(fullName, reader.getSystemID(),
					       reader.getLineNr());
			if (prefix == null)
			    builder.endElement(name, prefix, defaultNamespace);
			else
			    builder.endElement(name, prefix,
					       namespaces.getProperty(prefix));
			break;
		    }
		    reader.unread(str.charAt(0));
		    scanSomeTag(true, defaultNamespace,
				(Properties) namespaces.clone());
		} else {
		    if (str.charAt(0) == '&') {
			ch = XMLUtil.processCharLiteral(str);
			buffer.append(ch);
		    } else
			reader.unread(str.charAt(0));
		    validator.PCDataAdded(reader.getSystemID(),
					  reader.getLineNr());
		    Reader r = new ContentReader(reader, entityResolver,
						 buffer.toString());
		    builder.addPCData(r, reader.getSystemID(),
				      reader.getLineNr());
		    r.close();
		}
	    }
	}
    }
    
    protected void processAttribute(Vector attrNames, Vector attrValues,
				    Vector attrTypes) throws Exception {
	String key = XMLUtil.scanIdentifier(reader);
	XMLUtil.skipWhitespace(reader, null);
	if (!XMLUtil.read(reader, '&').equals("="))
	    XMLUtil.errorExpectedInput(reader.getSystemID(),
				       reader.getLineNr(), "`='");
	XMLUtil.skipWhitespace(reader, null);
	String value = XMLUtil.scanString(reader, '&', entityResolver);
	attrNames.addElement(key);
	attrValues.addElement(value);
	attrTypes.addElement("CDATA");
	validator.attributeAdded(key, value, reader.getSystemID(),
				 reader.getLineNr());
    }
}
