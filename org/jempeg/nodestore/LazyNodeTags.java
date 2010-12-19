package org.jempeg.nodestore;

import java.io.IOException;

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.filesystem.IImportFile;

/**
 * LazyNodeTags is an extension of NodeTags that lazy-loads the file length.  In
 * some uses of a nodestore, you don't need to know the length
 * of the file right up front, and it turns out that retrieving
 * the file length is actually a somewhat lengthy process.
 * 
 * @author Mike Schrag
 */
public class LazyNodeTags extends NodeTags {
	private IImportFile myFile;
	
	/**
	 * Construct a new LazyNodeTags
	 * 
	 * @param  _file the file to operate on
	 */
	public LazyNodeTags(IImportFile _file) {
		super();
		myFile = _file;
	}
	
	public String getValue(String _tagName) {
		String value = getRawValue(_tagName);
		if (value == null) {
			if (_tagName.equals(DatabaseTags.LENGTH_TAG)) {
				try {
					value = String.valueOf(myFile.getLength());
					setValue(DatabaseTags.LENGTH_TAG, value);
				}
				catch (IOException e) {
					throw new ChainedRuntimeException("Unable to lazy load the length of the file.", e);
				}
			} else {
				value = "";
			}
		}
		return value;
	}
}