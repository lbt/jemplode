/* XMLParseException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;

public class XMLParseException extends XMLException
{
    public XMLParseException(String msg) {
	super(msg);
    }
    
    public XMLParseException(String systemID, int lineNr, String msg) {
	super(systemID, lineNr, null, msg, true);
    }
}
