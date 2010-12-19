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
package org.jempeg.manager.event;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import com.inzyme.util.Timer;

/**
* This is a big hack .. This recursively adds a key listener
* to every subcomponent (that isn't a JTextField, so we
* don't override the typing area) and figures out what
* the closest match file is to what the user typed, then
* sets the selected file to be that.  This is really
* weird because we don't know what PLAF the user may be
* in, so we can't talk directly to subcomponents of the
* file chooser.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class FileChooserKeyListener extends KeyAdapter {
  private JFileChooser myFileChooser;
  private StringBuffer mySelection;
  private Timer myTimer;

  public FileChooserKeyListener(JFileChooser _fileChooser) {
    myFileChooser = _fileChooser;
    addKeyListener(myFileChooser);
    mySelection = new StringBuffer();
    myTimer = new Timer(1000, this, "clear");
  }

  protected void addKeyListener(Component _comp) {
    if (_comp instanceof Container) {
      Component[] components = ((Container)_comp).getComponents();
      for (int i = 0; i < components.length; i ++) {
        if (!(components[i] instanceof JTextField)) {
          components[i].addKeyListener(this);
          addKeyListener(components[i]);
        }
      }
    }
  }

  public synchronized void keyTyped(KeyEvent _event) {
    mySelection.append(_event.getKeyChar());

    File dir = myFileChooser.getCurrentDirectory();
    String[] files = dir.list();
    if (files != null) {
      String selectionStr = mySelection.toString().toLowerCase();
      int mode = myFileChooser.getFileSelectionMode();
      boolean selected = false;
      for (int i = 0; !selected && i < files.length; i ++) {
        File file = new File(dir, files[i]);
        if ((mode == JFileChooser.FILES_AND_DIRECTORIES) ||
            (file.isDirectory() && mode == JFileChooser.DIRECTORIES_ONLY) ||
            (file.isFile() && mode == JFileChooser.FILES_ONLY)) {
          if (files[i].toLowerCase().startsWith(selectionStr)) {
            myFileChooser.setSelectedFile(file);
            selected = true;
          }
        }
      }
    }
    myTimer.mark();
  }

  public void clear() {
    mySelection.setLength(0);
  }
}
