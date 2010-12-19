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

import java.io.File;
import java.util.StringTokenizer;

/**
* Convenience methods for manipulating files.
*
* @author Mike Schrag
* @version $Revision: 1.2 $
*/
public class FileUtils {
	/**
	* Cleans up filenames by removing invalid characters.
	*
	* @param _name the original filename
	* @param _removeDirs remove directories from this pass?
	* @returns a cleansed filename
	*/
	public static String cleanseFilename(String _name, boolean _removeDirs) {
		char replaceChar = '_';

		String cleansedName = _name;
		// .. this is a lame way to do this, I know .. plus it's very "windowsy"
		if (_removeDirs) {
			cleansedName = cleansedName.replace(File.separatorChar, replaceChar);
			cleansedName = cleansedName.replace('\\', replaceChar);
			cleansedName = cleansedName.replace('/', replaceChar);
		}
		// Lame little check for Windows because it's really picky about
		// what filenames can be created
		if (File.separatorChar == '\\') {
			cleansedName = cleansedName.replace('/', replaceChar);
			cleansedName = cleansedName.replace('*', replaceChar);
			cleansedName = cleansedName.replace(':', replaceChar);
			cleansedName = cleansedName.replace('?', replaceChar);
			cleansedName = cleansedName.replace('"', replaceChar);
			cleansedName = cleansedName.replace('<', replaceChar);
			cleansedName = cleansedName.replace('>', replaceChar);
			cleansedName = cleansedName.replace('|', replaceChar);
		}
		return (cleansedName);
	}
	
	/**
	* Finds the jar file in the classpath.
	*
	* @param _name the name of the jar file to find
	* @returns a File pointing to the Jar file.
	*/
	public static File findInClasspath(String _name) {
		File foundFile = null;
		StringTokenizer tk = new StringTokenizer(System.getProperty("java.class.path"), File.pathSeparator);
		while (foundFile == null && tk.hasMoreElements()) {
			String path = tk.nextToken();
			File f = new File(path);
			if (f.isDirectory()) {
				f = new File(f, _name);
				if (f.exists()) {
					foundFile = f;
				}
			}
			else {
				if (f.exists() && f.getName().equals(_name)) {
					foundFile = f;
				}
			}
		}
		return foundFile;
	}
}
