/* XMLUtil - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.IOException;
import java.io.Reader;

class XMLUtil
{
    static void skipComment(IXMLReader reader)
	throws IOException, XMLParseException {
	if (reader.read() != '-')
	    errorExpectedInput(reader.getSystemID(), reader.getLineNr(),
			       "<!--");
	int dashesRead = 0;
	for (;;) {
	    char ch = reader.read();
	    switch (ch) {
	    case '-':
		dashesRead++;
		break;
	    case '>':
		if (dashesRead == 2)
		    return;
		/* fall through */
	    default:
		dashesRead = 0;
	    }
	}
    }
    
    static void skipTag(IXMLReader reader)
	throws IOException, XMLParseException {
	int level = 1;
	while (level > 0) {
	    char ch = reader.read();
	    switch (ch) {
	    case '<':
		level++;
		break;
	    case '>':
		level--;
		break;
	    }
	}
    }
    
    static String scanPublicID(StringBuffer publicID, IXMLReader reader)
	throws IOException, XMLParseException {
	if (!checkLiteral(reader, "UBLIC"))
	    return null;
	skipWhitespace(reader, null);
	publicID.append(scanString(reader, '\0', null));
	skipWhitespace(reader, null);
	return scanString(reader, '\0', null);
    }
    
    static String scanSystemID(IXMLReader reader)
	throws IOException, XMLParseException {
	if (!checkLiteral(reader, "YSTEM"))
	    return null;
	skipWhitespace(reader, null);
	return scanString(reader, '\0', null);
    }
    
    static String scanIdentifier(IXMLReader reader)
	throws IOException, XMLParseException {
	StringBuffer result = new StringBuffer();
	for (;;) {
	    char ch = reader.read();
	    if (ch == '_' || ch == ':' || ch == '-' || ch == '.'
		|| ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z'
		|| ch >= '0' && ch <= '9' || ch > '~')
		result.append(ch);
	    else {
		reader.unread(ch);
		break;
	    }
	}
	return result.toString();
    }
    
    static String scanString
	(IXMLReader reader, char entityChar, IXMLEntityResolver entityResolver)
	throws IOException, XMLParseException {
	StringBuffer result = new StringBuffer();
	int startingLevel = reader.getStreamLevel();
	char delim = reader.read();
	if (delim != '\'' && delim != '\"')
	    errorExpectedInput(reader.getSystemID(), reader.getLineNr(),
			       "delimited string");
	for (;;) {
	    String str = read(reader, entityChar);
	    char ch = str.charAt(0);
	    if (ch == entityChar) {
		if (str.charAt(1) == '#')
		    result.append(processCharLiteral(str));
		else
		    processEntity(str, reader, entityResolver);
	    } else if (ch == '&') {
		reader.unread(ch);
		str = read(reader, '&');
		if (str.charAt(1) == '#')
		    result.append(processCharLiteral(str));
		else
		    result.append(str);
	    } else if (reader.getStreamLevel() == startingLevel) {
		if (ch == delim)
		    break;
		if (ch == '\t' || ch == '\n' || ch == '\r')
		    result.append(' ');
		else
		    result.append(ch);
	    } else
		result.append(ch);
	}
	return result.toString();
    }
    
    static void processEntity
	(String entity, IXMLReader reader, IXMLEntityResolver entityResolver)
	throws IOException, XMLParseException {
	entity = entity.substring(1, entity.length() - 1);
	Reader entityReader = entityResolver.getEntity(reader, entity);
	if (entityReader == null)
	    errorInvalidEntity(reader.getSystemID(), reader.getLineNr(),
			       entity);
	boolean externalEntity = entityResolver.isExternalEntity(entity);
	reader.startNewStream(entityReader, !externalEntity);
    }
    
    static char processCharLiteral(String entity)
	throws IOException, XMLParseException {
	if (entity.charAt(2) == 'x') {
	    entity = entity.substring(3, entity.length() - 1);
	    return (char) Integer.parseInt(entity, 16);
	}
	entity = entity.substring(2, entity.length() - 1);
	return (char) Integer.parseInt(entity, 10);
    }
    
    static void skipWhitespace(IXMLReader reader, StringBuffer buffer)
	throws IOException {
	char ch;
	if (buffer == null) {
	    do
		ch = reader.read();
	    while (ch == ' ' || ch == '\t' || ch == '\n');
	} else {
	    for (;;) {
		ch = reader.read();
		if (ch != ' ' && ch != '\t' && ch != '\n')
		    break;
		if (ch == '\n')
		    buffer.append('\n');
		else
		    buffer.append(' ');
	    }
	}
	reader.unread(ch);
    }
    
    static String read(IXMLReader reader, char entityChar)
	throws IOException, XMLParseException {
	char ch = reader.read();
	StringBuffer buf = new StringBuffer();
	buf.append(ch);
	if (ch == entityChar) {
	    while (ch != ';') {
		ch = reader.read();
		buf.append(ch);
	    }
	}
	return buf.toString();
    }
    
    static char readChar(IXMLReader reader, char entityChar)
	throws IOException, XMLParseException {
	String str = read(reader, entityChar);
	char ch = str.charAt(0);
	if (ch == entityChar)
	    errorUnexpectedEntity(reader.getSystemID(), reader.getLineNr(),
				  str);
	return ch;
    }
    
    static boolean checkLiteral(IXMLReader reader, String literal)
	throws IOException, XMLParseException {
	for (int i = 0; i < literal.length(); i++) {
	    if (reader.read() != literal.charAt(i))
		return false;
	}
	return true;
    }
    
    static void errorExpectedInput
	(String systemID, int lineNr, String expectedString)
	throws XMLParseException {
	throw new XMLParseException(systemID, lineNr,
				    "Expected: " + expectedString);
    }
    
    static void errorInvalidEntity
	(String systemID, int lineNr, String entity) throws XMLParseException {
	throw new XMLParseException(systemID, lineNr,
				    "Invalid entity: `&" + entity + ";'");
    }
    
    static void errorUnexpectedEntity
	(String systemID, int lineNr, String entity) throws XMLParseException {
	throw new XMLParseException(systemID, lineNr,
				    ("No entity reference is expected here ("
				     + entity + ")"));
    }
    
    static void errorUnexpectedCDATA(String systemID, int lineNr)
	throws XMLParseException {
	throw new XMLParseException(systemID, lineNr,
				    "No CDATA section is expected here");
    }
    
    static void errorInvalidInput
	(String systemID, int lineNr, String unexpectedString)
	throws XMLParseException {
	throw new XMLParseException(systemID, lineNr,
				    "Invalid input: " + unexpectedString);
    }
    
    static void errorWrongClosingTag
	(String systemID, int lineNr, String expectedName, String wrongName)
	throws XMLParseException {
	throw new XMLParseException
		  (systemID, lineNr,
		   ("Closing tag does not match opening tag: `" + wrongName
		    + "' != `" + expectedName + "'"));
    }
    
    static void errorClosingTagNotEmpty(String systemID, int lineNr)
	throws XMLParseException {
	throw new XMLParseException(systemID, lineNr,
				    "Closing tag must be empty");
    }
    
    static void errorMissingElement
	(String systemID, int lineNr, String parentElementName,
	 String missingElementName)
	throws XMLValidationException {
	throw new XMLValidationException(1, systemID, lineNr,
					 missingElementName, null, null,
					 ("Element " + parentElementName
					  + " expects to have a "
					  + missingElementName));
    }
    
    static void errorUnexpectedElement
	(String systemID, int lineNr, String parentElementName,
	 String unexpectedElementName)
	throws XMLValidationException {
	throw new XMLValidationException(2, systemID, lineNr,
					 unexpectedElementName, null, null,
					 ("Unexpected " + unexpectedElementName
					  + " in a " + parentElementName));
    }
    
    static void errorMissingAttribute
	(String systemID, int lineNr, String elementName, String attributeName)
	throws XMLValidationException {
	throw new XMLValidationException(3, systemID, lineNr, elementName,
					 attributeName, null,
					 ("Element " + elementName
					  + " expects an attribute named "
					  + attributeName));
    }
    
    static void errorUnexpectedAttribute
	(String systemID, int lineNr, String elementName, String attributeName)
	throws XMLValidationException {
	throw new XMLValidationException(4, systemID, lineNr, elementName,
					 attributeName, null,
					 ("Element " + elementName
					  + " did not expect an attribute "
					  + "named " + attributeName));
    }
    
    static void errorInvalidAttributeValue
	(String systemID, int lineNr, String elementName, String attributeName,
	 String attributeValue)
	throws XMLValidationException {
	throw new XMLValidationException(5, systemID, lineNr, elementName,
					 attributeName, attributeValue,
					 ("Invalid value for attribute "
					  + attributeName));
    }
    
    static void errorMissingPCData
	(String systemID, int lineNr, String parentElementName)
	throws XMLValidationException {
	throw new XMLValidationException(6, systemID, lineNr, null, null, null,
					 ("Missing #PCDATA in element "
					  + parentElementName));
    }
    
    static void errorUnexpectedPCData
	(String systemID, int lineNr, String parentElementName)
	throws XMLValidationException {
	throw new XMLValidationException(7, systemID, lineNr, null, null, null,
					 ("Unexpected #PCDATA in element "
					  + parentElementName));
    }
    
    static void validationError
	(String systemID, int lineNr, String message, String elementName,
	 String attributeName, String attributeValue)
	throws XMLValidationException {
	throw new XMLValidationException(0, systemID, lineNr, elementName,
					 attributeName, attributeValue,
					 message);
    }
}
