/* IFlacMetadataBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.flac;
import java.io.IOException;
import java.io.InputStream;

public interface IFlacMetadataBlock
{
    public FlacMetadataBlockHeader getHeader();
    
    public void read(InputStream inputstream) throws IOException;
}
