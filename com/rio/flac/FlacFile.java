/* FlacFile - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.flac;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import com.inzyme.exception.ExceptionUtils;
import com.inzyme.io.FileSeekableInputStream;
import com.inzyme.io.SeekableInputStream;

import org.jempeg.tags.PropertiesTagExtractorListener;
import org.jempeg.tags.id3.ID3TagExtractor;
import org.jempeg.tags.id3.ID3V1Tag;
import org.jempeg.tags.id3.ID3V2Tag;

public class FlacFile
{
    private StreamInfoMetadataBlock myStreamInfo;
    private VorbisCommentMetadataBlock myVorbisComment;
    private Vector myMetadataBlocks;
    private Properties myExtractedTags;
    private ID3V1Tag myID3V1Tag;
    private ID3V2Tag myID3V2Tag;
    
    public FlacFile(SeekableInputStream _is) throws IOException {
	ID3TagExtractor id3TagExtractor = new ID3TagExtractor();
	PropertiesTagExtractorListener tagExtractorListener
	    = new PropertiesTagExtractorListener();
	ID3TagExtractor.TagOffsets offsets
	    = id3TagExtractor
		  .extractTagsWithoutFrameInfo(_is, tagExtractorListener);
	myExtractedTags = tagExtractorListener.getTags();
	myExtractedTags.put("codec", "flac");
	myID3V1Tag = id3TagExtractor.getID3V1Tag();
	myID3V2Tag = id3TagExtractor.getID3V2Tag();
	_is.seek((long) offsets.getOffset());
	byte[] magicNumber = new byte[4];
	_is.read(magicNumber);
	if (!isFlacMagicNumber(magicNumber))
	    throw new IOException("Stream does not contain a FLAC file.");
	myMetadataBlocks = new Vector();
	myStreamInfo = ((StreamInfoMetadataBlock)
			FlacMetadataBlockFactory.readMetadataBlock(_is));
	myMetadataBlocks.addElement(myStreamInfo);
	IFlacMetadataBlock metadataBlock = myStreamInfo;
	while (!metadataBlock.getHeader().isLastMetadataBlock()) {
	    metadataBlock = FlacMetadataBlockFactory.readMetadataBlock(_is);
	    if (metadataBlock instanceof VorbisCommentMetadataBlock)
		myVorbisComment = (VorbisCommentMetadataBlock) metadataBlock;
	    myMetadataBlocks.addElement(metadataBlock);
	}
    }
    
    public static boolean isFlacMagicNumber(byte[] _magicNumber) {
	if (_magicNumber[0] == 102 && _magicNumber[1] == 76
	    && _magicNumber[2] == 97 && _magicNumber[3] == 67)
	    return true;
	return false;
    }
    
    public boolean hasID3Tags() {
	if (myID3V1Tag == null && myID3V2Tag == null)
	    return false;
	return true;
    }
    
    public ID3V1Tag getID3V1Tag() {
	return myID3V1Tag;
    }
    
    public ID3V2Tag getID3V2Tag() {
	return myID3V2Tag;
    }
    
    public String getRID() {
	return myExtractedTags.getProperty("rid");
    }
    
    public boolean hasVorbisTags() {
	if (myVorbisComment != null)
	    return true;
	return false;
    }
    
    public Properties getExtractedVorbisTags() {
	return (myVorbisComment == null ? new Properties()
		: myVorbisComment.toProperties());
    }
    
    public Properties getExtractedID3Tags() {
	return myExtractedTags;
    }
    
    public StreamInfoMetadataBlock getStreamInfo() {
	return myStreamInfo;
    }
    
    public VorbisCommentMetadataBlock getVorbisComment() {
	return myVorbisComment;
    }
    
    public IFlacMetadataBlock[] getMetadataBlocks() {
	IFlacMetadataBlock[] metadataBlocks
	    = new IFlacMetadataBlock[myMetadataBlocks.size()];
	myMetadataBlocks.copyInto(metadataBlocks);
	return metadataBlocks;
    }
    
    public static void main(String[] args) {
	try {
	    FileSeekableInputStream fsis
		= new FileSeekableInputStream(new File("h:\\test01.flac"));
	    new FlacFile(fsis);
	    fsis.close();
	} catch (Throwable t) {
	    ExceptionUtils.printChainedStackTrace(t);
	}
    }
}
