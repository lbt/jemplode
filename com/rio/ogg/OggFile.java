/* OggFile - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.ogg;
import java.io.IOException;
import java.util.Properties;

import com.inzyme.io.LimitedInputStream;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.typeconv.LittleEndianInputStream;

import org.jempeg.tags.PropertiesTagExtractorListener;
import org.jempeg.tags.id3.ID3TagExtractor;
import org.jempeg.tags.id3.ID3V1Tag;
import org.jempeg.tags.id3.ID3V2Tag;

public class OggFile
{
    private IdentificationOggHeader myIdentificationHeader;
    private CommentOggHeader myCommentHeader;
    private SetupOggHeader mySetupHeader;
    private long mySerialNumber;
    private long myPcmLength;
    private Properties myExtractedTags;
    private ID3V1Tag myID3V1Tag;
    private ID3V2Tag myID3V2Tag;
    
    public OggFile(SeekableInputStream _is)
	throws IOException, InvalidPageHeaderException {
	ID3TagExtractor id3TagExtractor = new ID3TagExtractor();
	PropertiesTagExtractorListener tagExtractorListener
	    = new PropertiesTagExtractorListener();
	ID3TagExtractor.TagOffsets offsets
	    = id3TagExtractor
		  .extractTagsWithoutFrameInfo(_is, tagExtractorListener);
	myExtractedTags = tagExtractorListener.getTags();
	myExtractedTags.put("codec", "vorbis");
	myID3V1Tag = id3TagExtractor.getID3V1Tag();
	myID3V2Tag = id3TagExtractor.getID3V2Tag();
	_is.seek((long) offsets.getOffset());
	LittleEndianInputStream eis
	    = (new LittleEndianInputStream
	       (new LimitedInputStream(_is, (long) ((int) _is.length()
						    - offsets.getTrailer()))));
	int requiredHeaderCount = 0;
	OggPage page = null;
	do {
	    page = readPage(eis);
	    if (page != null) {
		if (requiredHeaderCount < 3) {
		    for (LittleEndianInputStream pageContents
			     = page.getPageContent();
			 (requiredHeaderCount < 3
			  && pageContents.available() > 0);
			 requiredHeaderCount++) {
			IOggHeader header
			    = OggHeaderFactory.createHeader(pageContents);
			if (header instanceof IdentificationOggHeader)
			    myIdentificationHeader
				= (IdentificationOggHeader) header;
			else if (header instanceof CommentOggHeader)
			    myCommentHeader = (CommentOggHeader) header;
			else if (header instanceof SetupOggHeader)
			    mySetupHeader = (SetupOggHeader) header;
		    }
		}
		myPcmLength
		    = Math.max(myPcmLength,
			       page.getPageHeader().getGranulePosition());
		mySerialNumber = page.getPageHeader().getSerialNumber();
		page.skip();
	    }
	} while (page != null);
    }
    
    public boolean hasID3Tags() {
	if (myID3V1Tag != null && myID3V2Tag != null)
	    return true;
	return false;
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
    
    public Properties getExtractedID3Tags() {
	return myExtractedTags;
    }
    
    OggPage readPage(LittleEndianInputStream _eis)
	throws IOException, InvalidPageHeaderException {
	OggPage page;
	if (OggPage.containsPage(_eis)) {
	    page = new OggPage();
	    page.read(_eis);
	} else
	    page = null;
	return page;
    }
    
    public IdentificationOggHeader getIdentificationHeader() {
	return myIdentificationHeader;
    }
    
    public CommentOggHeader getCommentHeader() {
	return myCommentHeader;
    }
    
    public SetupOggHeader mySetupHeader() {
	return mySetupHeader;
    }
    
    public long getSerialNumber() {
	return mySerialNumber;
    }
    
    public long getDuration() {
	long duration
	    = myPcmLength * 1000L / myIdentificationHeader.getSampleRate();
	return duration;
    }
}
