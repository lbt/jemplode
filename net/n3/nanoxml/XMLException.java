/* XMLException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.PrintStream;
import java.io.PrintWriter;

public class XMLException extends Exception
{
    private String msg;
    private String systemID;
    private int lineNr;
    private Exception encapsulatedException;
    
    public XMLException(String msg) {
	this(null, -1, null, msg, false);
    }
    
    public XMLException(Exception e) {
	this(null, -1, e, "Nested Exception", false);
    }
    
    public XMLException(String systemID, int lineNr, Exception e) {
	this(systemID, lineNr, e, "Nested Exception", true);
    }
    
    public XMLException(String systemID, int lineNr, String msg) {
	this(systemID, lineNr, null, msg, true);
    }
    
    public XMLException(String systemID, int lineNr, Exception e, String msg,
			boolean reportParams) {
	super(buildMessage(systemID, lineNr, e, msg, reportParams));
	this.systemID = systemID;
	this.lineNr = lineNr;
	encapsulatedException = e;
	this.msg = buildMessage(systemID, lineNr, e, msg, reportParams);
    }
    
    private static String buildMessage(String systemID, int lineNr,
				       Exception e, String msg,
				       boolean reportParams) {
	String str = msg;
	if (reportParams) {
	    if (systemID != null)
		str += ", SystemID='" + (String) systemID + "'";
	    if (lineNr >= 0)
		str += ", Line=" + lineNr;
	    if (e != null)
		str += ", Exception: " + (Object) e;
	}
	return str;
    }
    
    protected void finalize() throws Throwable {
	systemID = null;
	encapsulatedException = null;
	super.finalize();
    }
    
    public String getSystemID() {
	return systemID;
    }
    
    public int getLineNr() {
	return lineNr;
    }
    
    public Exception getException() {
	return encapsulatedException;
    }
    
    public void printStackTrace(PrintWriter writer) {
	super.printStackTrace(writer);
	if (encapsulatedException != null) {
	    writer.println("*** Nested Exception:");
	    encapsulatedException.printStackTrace(writer);
	}
    }
    
    public void printStackTrace(PrintStream stream) {
	super.printStackTrace(stream);
	if (encapsulatedException != null) {
	    stream.println("*** Nested Exception:");
	    encapsulatedException.printStackTrace(stream);
	}
    }
    
    public void printStackTrace() {
	super.printStackTrace();
	if (encapsulatedException != null) {
	    System.err.println("*** Nested Exception:");
	    encapsulatedException.printStackTrace();
	}
    }
    
    public String toString() {
	return msg;
    }
}
