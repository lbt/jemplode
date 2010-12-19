/* VorbisCommentMetadataBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.flac;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.inzyme.io.LimitedInputStream;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.util.ReflectionUtils;
import com.rio.ogg.CommentOggHeader;
import com.rio.ogg.OggHeaderID;

public class VorbisCommentMetadataBlock implements IFlacMetadataBlock
{
    private FlacMetadataBlockHeader myHeader;
    private CommentOggHeader myCommentOggHeader;
    
    public VorbisCommentMetadataBlock(FlacMetadataBlockHeader _header) {
	myHeader = _header;
    }
    
    public FlacMetadataBlockHeader getHeader() {
	return myHeader;
    }
    
    public CommentOggHeader getCommentOggHeader() {
	return myCommentOggHeader;
    }
    
    public Properties toProperties() {
	return myCommentOggHeader.toProperties();
    }
    
    public void read(InputStream _is) throws IOException {
	LimitedInputStream lis
	    = new LimitedInputStream(_is, (long) myHeader.getLength());
	LittleEndianInputStream leis = new LittleEndianInputStream(lis);
	myCommentOggHeader = new CommentOggHeader(new OggHeaderID(3));
	myCommentOggHeader.read(leis);
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
