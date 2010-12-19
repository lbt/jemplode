/* NoopTagExtractor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags;
import java.io.IOException;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.util.Debug;

public class NoopTagExtractor implements ITagExtractor
{
    private IImportFile myFile;
    
    public NoopTagExtractor(IImportFile _file) {
	myFile = _file;
    }
    
    public int isTagExtractorFor(String _name, byte[] _header) {
	return 1;
    }
    
    protected void extractFakeData(ITagExtractorListener _listener) {
	_listener.tagExtracted("type", "taxi");
	_listener.tagExtracted("codec", "taxi");
	_listener.tagExtracted("duration", 0);
	_listener.tagExtracted("bitrate", "fs128");
	_listener.tagExtracted("samplerate", 0);
	_listener.tagExtracted("offset", 0);
    }
    
    public void extractTags
	(SeekableInputStream _is, ITagExtractorListener _listener)
	throws IOException {
	Debug.println(8, ("Unable to extract tags from stream of type '"
			  + myFile + "'."));
	extractFakeData(_listener);
    }
    
    public void extractTags
	(IImportFile _file, ITagExtractorListener _listener)
	throws IOException {
	Debug.println(8, "Unable to extract tags from file '" + _file + "'.");
	extractFakeData(_listener);
    }
}
