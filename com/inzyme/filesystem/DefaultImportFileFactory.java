package com.inzyme.filesystem;

import java.io.File;
import java.io.IOException;

/**
 * @author Mike Schrag
 */
public class DefaultImportFileFactory implements IImportFileFactory {
	public IImportFile createImportFile(String _name, Object _obj) throws IOException {
		IImportFile importFile = null;
		if (_obj instanceof File) {
			File f = (File) _obj;
			if (f.isDirectory()) {
				if (_name != null) {
					importFile = new LocalImportFolder(_name, f);
				}
				else {
					importFile = new LocalImportFolder(f);
				}
			}
			else {
				if (_name != null) {
					importFile = new LocalImportFile(_name, f);
				}
				else {
					importFile = new LocalImportFile(f);
				}
			}
		}
		return importFile;
	}
}
