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
package org.jempeg.manager.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.dialog.PlaylistPropertiesDialog;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.IFIDPlaylistWrapper;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.text.ResourceBundleUtils;

/**
 * Creates a new empty playlist.
 *
 * @author Mike Schrag
 * @version $Revision: 1.6 $
 */
public class NewPlaylistAction extends AbstractAction {
  private ApplicationContext myContext;

  public NewPlaylistAction(ApplicationContext _context) {
    myContext = _context;
  }

  public void actionPerformed(ActionEvent _event) {
    IContainer container = myContext.getSelectedContainer();
    if (container instanceof IFIDPlaylistWrapper) {
      FIDPlaylist parentPlaylist = ((IFIDPlaylistWrapper) container).getPlaylist();

      String playlistTitle = (String) JOptionPane.showInputDialog(myContext.getFrame(), ResourceBundleUtils.getUIString("newPlaylist.prompt"), ResourceBundleUtils.getUIString("newPlaylist.frameTitle"), JOptionPane.QUESTION_MESSAGE, null, null, ResourceBundleUtils.getUIString("newPlaylist.title"));
      if (playlistTitle != null) {
        int index = parentPlaylist.createPlaylist(playlistTitle);
        myContext.setSelection(this, new ContainerSelection(myContext, parentPlaylist, new int[] {
          index
        }));
        // MODIFIED
        //myContext.clearSelection();
        //myContext.addSelection(index);

        PlaylistPropertiesDialog playlistDialog = new PlaylistPropertiesDialog(myContext.getFrame());
        playlistDialog.setNodes(new IFIDNode[] {
          parentPlaylist.getPlaylistAt(index)
        });
        playlistDialog.setVisible(true);
        playlistDialog.dispose();
      }
    }
  }
}