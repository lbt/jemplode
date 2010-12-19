/* IXMLReader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package net.n3.nanoxml;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;

public interface IXMLReader
{
    public char read() throws IOException;
    
    public boolean atEOFOfCurrentStream() throws IOException;
    
    public boolean atEOF() throws IOException;
    
    public void unread(char c) throws IOException;
    
    public int getLineNr();
    
    public Reader openStream(String string, String string_0_)
	throws MalformedURLException, FileNotFoundException, IOException;
    
    public void startNewStream(Reader reader);
    
    public void startNewStream(Reader reader, boolean bool);
    
    public int getStreamLevel();
    
    public void setSystemID(String string) throws MalformedURLException;
    
    public void setPublicID(String string);
    
    public String getSystemID();
    
    public String getPublicID();
}
