/* ValidatorPlugin - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.util.Properties;

public class ValidatorPlugin implements IXMLValidator
{
    private IXMLValidator delegate = null;
    
    protected void finalize() throws Throwable {
	delegate = null;
	super.finalize();
    }
    
    public IXMLValidator getDelegate() {
	return delegate;
    }
    
    public void setDelegate(IXMLValidator delegate) {
	this.delegate = delegate;
    }
    
    public void setParameterEntityResolver(IXMLEntityResolver resolver) {
	delegate.setParameterEntityResolver(resolver);
    }
    
    public IXMLEntityResolver getParameterEntityResolver() {
	return delegate.getParameterEntityResolver();
    }
    
    public void parseDTD(String publicID, IXMLReader reader,
			 IXMLEntityResolver entityResolver,
			 boolean external) throws Exception {
	delegate.parseDTD(publicID, reader, entityResolver, external);
    }
    
    public void elementStarted(String name, String systemId, int lineNr)
	throws Exception {
	delegate.elementStarted(name, systemId, lineNr);
    }
    
    public void elementEnded(String name, String systemId, int lineNr)
	throws Exception {
	delegate.elementEnded(name, systemId, lineNr);
    }
    
    public void elementAttributesProcessed
	(String name, Properties extraAttributes, String systemId, int lineNr)
	throws Exception {
	delegate.elementAttributesProcessed(name, extraAttributes, systemId,
					    lineNr);
    }
    
    public void attributeAdded(String key, String value, String systemId,
			       int lineNr) throws Exception {
	delegate.attributeAdded(key, value, systemId, lineNr);
    }
    
    public void PCDataAdded(String systemId, int lineNr) throws Exception {
	delegate.PCDataAdded(systemId, lineNr);
    }
    
    public void missingElement
	(String systemID, int lineNr, String parentElementName,
	 String missingElementName)
	throws XMLValidationException {
	XMLUtil.errorMissingElement(systemID, lineNr, parentElementName,
				    missingElementName);
    }
    
    public void unexpectedElement
	(String systemID, int lineNr, String parentElementName,
	 String unexpectedElementName)
	throws XMLValidationException {
	XMLUtil.errorUnexpectedElement(systemID, lineNr, parentElementName,
				       unexpectedElementName);
    }
    
    public void missingAttribute
	(String systemID, int lineNr, String elementName, String attributeName)
	throws XMLValidationException {
	XMLUtil.errorMissingAttribute(systemID, lineNr, elementName,
				      attributeName);
    }
    
    public void unexpectedAttribute
	(String systemID, int lineNr, String elementName, String attributeName)
	throws XMLValidationException {
	XMLUtil.errorUnexpectedAttribute(systemID, lineNr, elementName,
					 attributeName);
    }
    
    public void invalidAttributeValue
	(String systemID, int lineNr, String elementName, String attributeName,
	 String attributeValue)
	throws XMLValidationException {
	XMLUtil.errorInvalidAttributeValue(systemID, lineNr, elementName,
					   attributeName, attributeValue);
    }
    
    public void missingPCData
	(String systemID, int lineNr, String parentElementName)
	throws XMLValidationException {
	XMLUtil.errorMissingPCData(systemID, lineNr, parentElementName);
    }
    
    public void unexpectedPCData
	(String systemID, int lineNr, String parentElementName)
	throws XMLValidationException {
	XMLUtil.errorUnexpectedPCData(systemID, lineNr, parentElementName);
    }
    
    public void validationError
	(String systemID, int lineNr, String message, String elementName,
	 String attributeName, String attributeValue)
	throws XMLValidationException {
	XMLUtil.validationError(systemID, lineNr, message, elementName,
				attributeName, attributeValue);
    }
}
