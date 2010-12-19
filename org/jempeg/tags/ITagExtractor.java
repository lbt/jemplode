/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*
* Any references to com.tffenterprises code is 
* referenced under the BSD license as defined in
* LICENSE1.
*/
package org.jempeg.tags;

import java.io.IOException;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.SeekableInputStream;

/** 
 * Extract the tags from media files.  It's actually implemented in terms of
 * derived classes ID3TagExtractor, ASFTagExtractor, etc.. There's a virtual
 * constructor/factory idiom going on here -- TagExtractor::Create looks at the
 * filename and creates the most relevant derived class.
 *
 * Both  Create and ExtractTags take the filename.  Create uses it to: - figure
 * out which extractor to use - figure out the default title
 *
 * ExtractTags  uses it to create a SeekableStream, which is passed to the other
 * overload of ExtractTags.
 * 
 * @todo If we could use the SeekableStream to figure out which extractor to
 * use, we'd only need to pass the default title.  This would make it a bit more
 * sensible to use.
 * 
 * It is possible for ExtractTags to call the observer multiple times with the
 * same tag but a different value. In these circumstances the last value
 * returned is more likely to be correct. :-)
 */
public interface ITagExtractor {
	public static final int YES = 1;
	public static final int MAYBE = 0;
	public static final int NO = -1;
	
	public int isTagExtractorFor(String _name, byte[] _header);
	
  public void extractTags(SeekableInputStream _is, ITagExtractorListener _listener) throws IOException;

  public void extractTags(IImportFile _file, ITagExtractorListener _listener) throws IOException;
}
