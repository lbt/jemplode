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
package org.jempeg.filesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import com.inzyme.filesystem.AbstractLocalImportFolder;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.ImportFileFactory;
import com.inzyme.util.Debug;

/**
* M3UImportFile is an implementation of an
* ImportFile on top of an M3U File.
*
* @author Mike Schrag
* @version $Revision: 1.2 $
*/
public class M3UImportFile extends AbstractLocalImportFolder {
  private String myName;

  public M3UImportFile(File _file) throws IOException {
    super(_file, false);

    String name = _file.getName();
    if (name.toLowerCase().endsWith(".m3u")) {
      name = name.substring(0, name.length() - 4);
    }
    myName = name;
  }

  public String getName() {
    return myName;
  }

  protected IImportFile[] getChildren0() throws IOException {
    File f = getFile();
    FileInputStream fis = new FileInputStream(f);
    InputStreamReader isr = new InputStreamReader(fis);
    BufferedReader br = new BufferedReader(isr);

    File path = new File(new File(getLocation()).getParent());

    Vector childrenVec = new Vector();
    while (br.ready()) {
      String line = br.readLine();
      try {
	      if (!line.startsWith("#")) {
	        File newFile = new File(line);
	        if (!newFile.isAbsolute()) {
	          newFile = new File(path.getParent(), line);
	        }
	        childrenVec.addElement(ImportFileFactory.createImportFile(newFile));
	      }
      }
      catch (IOException e) {
      	// NTS: Should we fail completely here?
      	Debug.println(e);
      }
    }

    IImportFile[] children = new IImportFile[childrenVec.size()];
    childrenVec.copyInto(children);
    return children;
  }
}
