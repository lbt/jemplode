/* IXMLValidator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.util.Properties;

public interface IXMLValidator
{
    public void setParameterEntityResolver
	(IXMLEntityResolver ixmlentityresolver);
    
    public IXMLEntityResolver getParameterEntityResolver();
    
    public void parseDTD(String string, IXMLReader ixmlreader,
			 IXMLEntityResolver ixmlentityresolver,
			 boolean bool) throws Exception;
    
    public void elementStarted(String string, String string_0_, int i)
	throws Exception;
    
    public void elementEnded(String string, String string_1_, int i)
	throws Exception;
    
    public void attributeAdded(String string, String string_2_,
			       String string_3_, int i) throws Exception;
    
    public void elementAttributesProcessed
	(String string, Properties properties, String string_4_, int i)
	throws Exception;
    
    public void PCDataAdded(String string, int i) throws Exception;
}
