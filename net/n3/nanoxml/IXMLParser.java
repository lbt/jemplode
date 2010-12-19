/* IXMLParser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;

public interface IXMLParser
{
    public void setReader(IXMLReader ixmlreader);
    
    public IXMLReader getReader();
    
    public void setBuilder(IXMLBuilder ixmlbuilder);
    
    public IXMLBuilder getBuilder();
    
    public void setValidator(IXMLValidator ixmlvalidator);
    
    public IXMLValidator getValidator();
    
    public void setResolver(IXMLEntityResolver ixmlentityresolver);
    
    public IXMLEntityResolver getResolver();
    
    public Object parse() throws XMLException;
}
