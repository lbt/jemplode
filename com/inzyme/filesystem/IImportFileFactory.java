package com.inzyme.filesystem;

import java.io.IOException;

/**
 * IImportFileFactory is able to create an IImportFile based on a particular
 * object.
 * 
 * @author Mike Schrag
 */
public interface IImportFileFactory {
	/**
	 * Create a named ImportFile for the given Object.
	 * 
	 * @param _name the name to use for the ImportFile
	 * @param _obj the Object to create an ImportFile for
	 * @return an ImportFile for the given Object
	 * @throws IOException if an ImportFile cannot be created
	 */
	public IImportFile createImportFile(String _name, Object _obj) throws IOException;
}
