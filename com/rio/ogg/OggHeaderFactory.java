/* OggHeaderFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.ogg;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;

public class OggHeaderFactory
{
    public static final int IDENTIFICATION_HEADER = 1;
    public static final int COMMENT_HEADER = 3;
    public static final int SETUP_HEADER = 5;
    
    public static IOggHeader createHeader(LittleEndianInputStream _is)
	throws IOException {
	OggHeaderID id = new OggHeaderID();
	id.read(_is);
	IOggHeader header = createHeader(id);
	header.read(_is);
	return header;
    }
    
    public static IOggHeader createHeader(OggHeaderID _headerID) {
	int type = _headerID.getType();
	IOggHeader header;
	switch (type) {
	case 1:
	    header = new IdentificationOggHeader(_headerID);
	    break;
	case 3:
	    header = new CommentOggHeader(_headerID);
	    break;
	case 5:
	    header = new SetupOggHeader(_headerID);
	    break;
	default:
	    throw new IllegalArgumentException
		      ("Unable to create header for unknown ID " + _headerID
		       + ".");
	}
	return header;
    }
}
