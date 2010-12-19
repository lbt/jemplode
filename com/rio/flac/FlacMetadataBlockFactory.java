/* FlacMetadataBlockFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.flac;
import java.io.IOException;
import java.io.InputStream;

public class FlacMetadataBlockFactory
{
    public static final int STREAMINFO = 0;
    public static final int PADDING = 1;
    public static final int APPLICATION = 2;
    public static final int SEEKTABLE = 3;
    public static final int VORBIS_COMMENT = 4;
    public static final int CUESHEET = 5;
    
    public static IFlacMetadataBlock readMetadataBlock(InputStream _is)
	throws IOException {
	FlacMetadataBlockHeader header = new FlacMetadataBlockHeader();
	header.read(_is);
	int blockType = header.getBlockType();
	IFlacMetadataBlock metadataBlock;
	switch (blockType) {
	case 0:
	    metadataBlock = new StreamInfoMetadataBlock(header);
	    break;
	case 1:
	    metadataBlock = new PaddingMetadataBlock(header);
	    break;
	case 2:
	    metadataBlock = new ApplicationMetadataBlock(header);
	    break;
	case 4:
	    metadataBlock = new VorbisCommentMetadataBlock(header);
	    break;
	default:
	    metadataBlock = new UnknownMetadataBlock(header);
	}
	metadataBlock.read(_is);
	return metadataBlock;
    }
}
