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

import org.jempeg.nodestore.DatabaseTags;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.util.Debug;

public class NoopTagExtractor implements ITagExtractor {
	private IImportFile myFile;

	public NoopTagExtractor(IImportFile _file) {
		myFile = _file;
	}

	public int isTagExtractorFor(String _name, byte[] _header) {
		return ITagExtractor.YES;
	}

	protected void extractFakeData(ITagExtractorListener _listener) {
		_listener.tagExtracted(DatabaseTags.TYPE_TAG, DatabaseTags.TYPE_TAXI);
		_listener.tagExtracted(DatabaseTags.CODEC_TAG, DatabaseTags.CODEC_TAXI);
		_listener.tagExtracted(DatabaseTags.DURATION_TAG, 0);
		_listener.tagExtracted(DatabaseTags.BITRATE_TAG, "fs128");
		_listener.tagExtracted(DatabaseTags.SAMPLERATE_TAG, 0);
		_listener.tagExtracted(DatabaseTags.OFFSET_TAG, 0);
	}

	public void extractTags(SeekableInputStream _is, ITagExtractorListener _listener) throws IOException {
		Debug.println(Debug.WARNING, "Unable to extract tags from stream of type '" + myFile + "'.");
		extractFakeData(_listener);
	}

	public void extractTags(IImportFile _file, ITagExtractorListener _listener) throws IOException {
		Debug.println(Debug.WARNING, "Unable to extract tags from file '" + _file + "'.");
		extractFakeData(_listener);
	}
}
