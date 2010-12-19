/* MusicImportFileFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.filesystem;
import java.io.File;
import java.io.IOException;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.IImportFileFactory;
import com.inzyme.properties.PropertiesManager;

import org.jempeg.nodestore.FIDLocalFile;
import org.jempeg.nodestore.FIDRemoteTune;

public class MusicImportFileFactory implements IImportFileFactory
{
    public IImportFile createImportFile(String _name, Object _obj)
	throws IOException {
	IImportFile importFile = null;
	if (_obj instanceof File) {
	    File f = (File) _obj;
	    String name = f.getName().toLowerCase();
	    if (name.endsWith(".m3u") && PropertiesManager.getInstance()
					     .getBooleanProperty("importM3U"))
		importFile = new M3UImportFile(f);
	} else {
	    if (_obj instanceof FIDRemoteTune)
		throw new IllegalArgumentException
			  ("FIDRemoteTunes are not supported for creating import files.");
	    if (_obj instanceof FIDLocalFile) {
		FIDLocalFile localFile = (FIDLocalFile) _obj;
		importFile = localFile.getFile();
	    }
	}
	return importFile;
    }
}
