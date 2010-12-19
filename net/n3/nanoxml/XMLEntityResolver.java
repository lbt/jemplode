/* XMLEntityResolver - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;

public class XMLEntityResolver implements IXMLEntityResolver
{
    private Hashtable entities = new Hashtable();
    
    public XMLEntityResolver() {
	entities.put("amp", "&#38;");
	entities.put("quot", "&#34;");
	entities.put("apos", "&#39;");
	entities.put("lt", "&#60;");
	entities.put("gt", "&#62;");
    }
    
    protected void finalize() throws Throwable {
	entities.clear();
	entities = null;
	super.finalize();
    }
    
    public void addInternalEntity(String name, String value) {
	if (!entities.containsKey(name))
	    entities.put(name, value);
    }
    
    public void addExternalEntity(String name, String publicID,
				  String systemID) {
	if (!entities.containsKey(name))
	    entities.put(name, new String[] { publicID, systemID });
    }
    
    public Reader getEntity(IXMLReader xmlReader, String name)
	throws XMLParseException {
	Object obj = entities.get(name);
	if (obj == null)
	    return null;
	if (obj instanceof String)
	    return new StringReader((String) obj);
	String[] id = (String[]) obj;
	return openExternalEntity(xmlReader, id[0], id[1]);
    }
    
    public boolean isExternalEntity(String name) {
	Object obj = entities.get(name);
	return !(obj instanceof String);
    }
    
    protected Reader openExternalEntity
	(IXMLReader xmlReader, String publicID, String systemID)
	throws XMLParseException {
	String parentSystemID = xmlReader.getSystemID();
	try {
	    return xmlReader.openStream(publicID, systemID);
	} catch (Exception e) {
	    throw new XMLParseException
		      (parentSystemID, xmlReader.getLineNr(),
		       ("Could not open external entity at system ID: "
			+ systemID));
	}
    }
}
