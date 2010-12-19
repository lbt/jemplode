/* IXMLBuilder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.Reader;

public interface IXMLBuilder
{
    public void startBuilding(String string, int i) throws Exception;
    
    public void newProcessingInstruction(String string, Reader reader)
	throws Exception;
    
    public void startElement(String string, String string_0_, String string_1_,
			     String string_2_, int i) throws Exception;
    
    public void addAttribute(String string, String string_3_, String string_4_,
			     String string_5_,
			     String string_6_) throws Exception;
    
    public void elementAttributesProcessed
	(String string, String string_7_, String string_8_) throws Exception;
    
    public void endElement
	(String string, String string_9_, String string_10_) throws Exception;
    
    public void addPCData(Reader reader, String string, int i)
	throws Exception;
    
    public Object getResult() throws Exception;
}
