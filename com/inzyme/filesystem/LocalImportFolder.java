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
import java.io.IOException;

/**
 * LocalImportFolder is an implementation of IImportFolder
 * over a folder on the local filesystem.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.1 $
 */
public class LocalImportFolder extends AbstractLocalImportFolder {
  private String myName;

  public LocalImportFolder(File _file) {
    this(_file.getName(), _file);
  }

  public LocalImportFolder(String _name, File _file) {
    super(_file, true);
    myName = _name;
  }

  public String getName() {
    return myName;
  }

  protected IImportFile[] getChildren0() throws IOException {
    File f = getFile();
    IImportFile[] children;
    String[] list = f.list();
    if (list == null) {
      children = null;
    } else {
      children = new IImportFile[list.length];
      for (int i = 0; i < list.length; i ++) {
        File childFile = new File(f, list[i]);
        children[i] = ImportFileFactory.createImportFile(childFile);
      }
    }
    return children;
  }
}
