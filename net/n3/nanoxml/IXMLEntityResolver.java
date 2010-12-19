/* IXMLEntityResolver - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.Reader;

public interface IXMLEntityResolver
{
    public void addInternalEntity(String string, String string_0_);
    
    public void addExternalEntity(String string, String string_1_,
				  String string_2_);
    
    public Reader getEntity(IXMLReader ixmlreader, String string)
	throws XMLParseException;
    
    public boolean isExternalEntity(String string);
}
