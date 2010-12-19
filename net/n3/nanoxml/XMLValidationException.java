/* XMLValidationException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;

public class XMLValidationException extends XMLException
{
    public static final int MISSING_ELEMENT = 1;
    public static final int UNEXPECTED_ELEMENT = 2;
    public static final int MISSING_ATTRIBUTE = 3;
    public static final int UNEXPECTED_ATTRIBUTE = 4;
    public static final int ATTRIBUTE_WITH_INVALID_VALUE = 5;
    public static final int MISSING_PCDATA = 6;
    public static final int UNEXPECTED_PCDATA = 7;
    public static final int MISC_ERROR = 0;
    private String elementName;
    private String attributeName;
    private String attributeValue;
    
    public XMLValidationException(int errorType, String systemID, int lineNr,
				  String elementName, String attributeName,
				  String attributeValue, String msg) {
	super(systemID, lineNr, null,
	      (msg + (elementName == null ? "" : ", element=" + elementName)
	       + (attributeName == null ? "" : ", attribute=" + attributeName)
	       + (attributeValue == null ? ""
		  : ", value='" + attributeValue + "'")),
	      false);
	this.elementName = elementName;
	this.attributeName = attributeName;
	this.attributeValue = attributeValue;
    }
    
    protected void finalize() throws Throwable {
	elementName = null;
	attributeName = null;
	attributeValue = null;
	super.finalize();
    }
    
    public String getElementName() {
	return elementName;
    }
    
    public String getAttributeName() {
	return attributeName;
    }
    
    public String getAttributeValue() {
	return attributeValue;
    }
}
