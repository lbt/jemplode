/* Chunk_iTXt - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;

final class Chunk_iTXt extends AbstractTextChunk implements TextChunk
{
    private boolean compressed;
    private String language;
    private String translated;
    
    Chunk_iTXt() {
	super(1767135348);
    }
    
    public String getTranslatedKeyword() {
	return translated;
    }
    
    public String getLanguage() {
	return language;
    }
    
    protected boolean isCompressed() {
	return compressed;
    }
    
    protected String readValue() throws IOException {
	int flag = in_data.readByte();
	int method = in_data.readByte();
	if (flag == 1) {
	    compressed = true;
	    if (method != 0)
		throw new PngExceptionSoft("Unrecognized " + typeToString(type)
					   + " compression method: " + method);
	} else if (flag != 0)
	    throw new PngExceptionSoft("Illegal " + typeToString(type)
				       + " compression flag: " + flag);
	language = in_data.readString("8859_1");
	translated = in_data.readString("UTF8");
	return super.readValue();
    }
}
