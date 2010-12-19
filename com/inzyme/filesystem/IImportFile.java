/* IImportFile - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.filesystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.inzyme.io.SeekableInputStream;

public interface IImportFile
{
    public Properties getTags() throws IOException;
    
    public Object getID() throws IOException;
    
    public String getName();
    
    public String getLocation();
    
    public long getLength() throws IOException;
    
    public InputStream getInputStream() throws IOException;
    
    public SeekableInputStream getSeekableInputStream() throws IOException;
}
