/* ITagExtractor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags;
import java.io.IOException;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.SeekableInputStream;

public interface ITagExtractor
{
    public static final int YES = 1;
    public static final int MAYBE = 0;
    public static final int NO = -1;
    
    public int isTagExtractorFor(String string, byte[] is);
    
    public void extractTags
	(SeekableInputStream seekableinputstream,
	 ITagExtractorListener itagextractorlistener)
	throws IOException;
    
    public void extractTags
	(IImportFile iimportfile, ITagExtractorListener itagextractorlistener)
	throws IOException;
}
