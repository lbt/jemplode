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
import java.io.InputStream;
import java.util.Properties;

import com.inzyme.io.SeekableInputStream;

/**
 * IImportFile represents an abstraction on a File so
 * that we can represent various file-like resources
 * like M3U's and non-local Files (that we couldn't
 * otherwise model with java.io.File, because, retardedly,
 * it's not an interface).
 * 
 * @author Mike Schrag
 * @version $Revision: 1.3 $
 */
public interface IImportFile {
  public Properties getTags() throws IOException;
	/**
	 * Returns a unique identifier for this ImportFile.
	 * 
	 * @return a unique identifier for this ImportFile
	 * @throws IOException if a unique ID cannot be obtained
	 */
  public Object getID() throws IOException;
	
	/**
	 * Returns the name of this ImportFile.
	 * 
	 * @return the name of this ImportFile
	 */
  public String getName();

	/**
	 * Returns an String that abstractly identifies the 
	 * "location" of this file (i.e. a path or a URL)
	 * 
	 * @return the location of this ImportFile
	 * @throws IOException if the location cannot be obtained
	 */
  public String getLocation();

	/**
	 * Returns the length of this ImportFile in bytes.
	 * 
	 * @return the length of this ImportFile in bytes
	 * @throws IOException if the length cannot be determined
	 */
  public long getLength() throws IOException;

	/**
	 * Returns an InputStream onto this IImportFile.
	 * 
	 * @return an InputStream onto this IImportFile
	 * @throws IOException if an InputStream cannot be obtained
	 */
  public InputStream getInputStream() throws IOException;

	/**
	 * Returns a SeekableInputStream onto this IImportFile.
	 * 
	 * @return a SeekableInputStream onto this IImportFile
	 * @throws IOException if a SeekableInputStream cannot be obtained
	 */
  public SeekableInputStream getSeekableInputStream() throws IOException;
}
