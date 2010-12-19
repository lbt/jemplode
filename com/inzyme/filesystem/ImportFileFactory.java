/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package com.inzyme.filesystem;

import java.io.IOException;

/**
 * MusicImportFileFactory is responsible for creating the
 * correct implementation of IImportFile for a particular
 * type of file.  This abstracts the mapping between file
 * types and ImportFile types (like an M3U vs. an MP3).
 * 
 * @author Mike Schrag
 * @version $Revision: 1.1 $
 */
public class ImportFileFactory {
	private static IImportFileFactory[] myImportFileFactories;
	
	static {
		addImportFileFactory(new DefaultImportFileFactory());
	}
	
	public static void addImportFileFactory(IImportFileFactory _importFileFactory) {
		if (myImportFileFactories == null) {
			myImportFileFactories = new IImportFileFactory[] { _importFileFactory };
		} else {
			IImportFileFactory[] importFileFactories = new IImportFileFactory[myImportFileFactories.length + 1];
			System.arraycopy(myImportFileFactories, 0, importFileFactories, 0, myImportFileFactories.length);
			importFileFactories[importFileFactories.length - 1] = _importFileFactory;
			myImportFileFactories = importFileFactories;
		}
	}
	
	/**
	 * Returns an array of IImportFile instances that correspond to
	 * the array of objects.
	 * 
	 * @param _objs the objects to create ImportFiles for
	 * @return an array of IImportFile instances
	 * @throws IOException if the ImportFiles cannot be created
	 */
  public static IImportFile[] createImportFiles(Object[] _objs) throws IOException {
    IImportFile[] importFiles = new IImportFile[_objs.length];
    for (int i = 0; i < _objs.length; i ++) {
      importFiles[i] = createImportFile(_objs[i]);
    }
    return importFiles;
  }

	/**
	 * Create an ImportFile for the given Object.
	 * 
	 * @param _obj the Object to create an ImportFile for
	 * @return an ImportFile for the given Object
	 * @throws IOException if an ImportFile cannot be created
	 */
  public static IImportFile createImportFile(Object _obj) throws IOException {
    return createImportFile(null, _obj);
  }

	/**
	 * Create a named ImportFile for the given Object.
	 * 
	 * @param _name the name to use for the ImportFile
	 * @param _obj the Object to create an ImportFile for
	 * @return an ImportFile for the given Object
	 * @throws IOException if an ImportFile cannot be created
	 */
  public static IImportFile createImportFile(String _name, Object _obj) throws IOException {
  	IImportFile importFile = null;
  	int size = myImportFileFactories.length;
  	for (int i = size - 1; importFile == null && i >= 0; i --) {
  		importFile = myImportFileFactories[i].createImportFile(_name, _obj);
  	}
  	if (importFile == null) {
      throw new IOException("Unknown ImportFile mapping for object: " + _obj);
    }
    return importFile;
  }
}
  
