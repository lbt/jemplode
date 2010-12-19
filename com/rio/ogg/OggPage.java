/* OggPage - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.ogg;
import java.io.IOException;

import com.inzyme.io.LimitedInputStream;
import com.inzyme.typeconv.LittleEndianInputStream;

class OggPage
{
    private OggPageHeader myPageHeader = new OggPageHeader();
    private LittleEndianInputStream myPageContent;
    
    public OggPage() {
	/* empty */
    }
    
    public static boolean containsPage(LittleEndianInputStream _eis)
	throws IOException {
	return OggPageHeader.containsPage(_eis);
    }
    
    public void read(LittleEndianInputStream _eis)
	throws IOException, InvalidPageHeaderException {
	myPageHeader.read(_eis);
	myPageContent
	    = (new LittleEndianInputStream
	       (new LimitedInputStream(_eis,
				       (long) myPageHeader.getBodyLength())));
    }
    
    public OggPageHeader getPageHeader() {
	return myPageHeader;
    }
    
    public LittleEndianInputStream getPageContent() {
	return myPageContent;
    }
    
    public void skip() throws IOException {
	int pageLeft = myPageContent.available();
	myPageContent.skip((long) pageLeft);
    }
}
