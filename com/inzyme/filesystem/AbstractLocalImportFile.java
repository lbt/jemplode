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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import com.inzyme.io.FileSeekableInputStream;
import com.inzyme.io.SeekableInputStream;

/**
 * Extended by any implementation of ImportFile that
 * is backed by a java.io.File.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.3 $
 */
public abstract class AbstractLocalImportFile implements IImportFile, Serializable {
  private File myFile;

  protected AbstractLocalImportFile(File _file, boolean _folder) {
    myFile = _file;
//		Turns out this is really expensive!! Who knew?    
//    boolean directory = _file.isDirectory();
//    if (_folder && !directory) {
//    	throw new IllegalArgumentException("You can't construct a Folder with a File.");
//    } else if (!_folder && directory) {
//    	throw new IllegalArgumentException("You can't construct a File with a Folder.");
//    }
  }
  
  public Properties getTags() {
    return new Properties();
  }
                   
  public File getFile() {
    return myFile;
  }

  public Object getID() {
    return myFile;
  }

  public abstract String getName();

  public String getLocation() {
    String location = myFile.getAbsolutePath();
    return location;
  }

  public long getLength() {
    long length = myFile.length();
    return length;
  }

  public InputStream getInputStream() throws IOException {
    FileInputStream fis = new FileInputStream(myFile);
    return fis;
  }

  public SeekableInputStream getSeekableInputStream() throws IOException {
    FileSeekableInputStream fsis = new FileSeekableInputStream(myFile);
    return fsis;
  }

  public String toString() {
    String toString = myFile.toString();
    return toString;
  }
  
}
