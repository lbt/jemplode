/* LazyNodeTags - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.io.IOException;

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.filesystem.IImportFile;

public class LazyNodeTags extends NodeTags
{
    private IImportFile myFile;
    
    public LazyNodeTags(IImportFile _file) {
	myFile = _file;
    }
    
    public String getValue(String _tagName) {
	String value = getRawValue(_tagName);
	do {
	    if (value == null) {
		if (_tagName.equals("length")) {
		    try {
			value = String.valueOf(myFile.getLength());
			setValue("length", value);
			break;
		    } catch (IOException e) {
			throw new ChainedRuntimeException
				  ("Unable to lazy load the length of the file.",
				   e);
		    }
		}
		value = "";
	    }
	} while (false);
	return value;
    }
}
