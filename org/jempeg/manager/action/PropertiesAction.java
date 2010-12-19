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
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.AbstractAction;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.dialog.AbstractPropertiesDialog;
import org.jempeg.manager.dialog.MixedPropertiesDialog;
import org.jempeg.manager.dialog.PlaylistPropertiesDialog;
import org.jempeg.manager.dialog.TunePropertiesDialog;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;

/**
* PropertiesAction displays the properties dialog for the currently selected node
* when any number of actions occur.
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public class PropertiesAction extends AbstractAction implements MouseListener {
	private ApplicationContext myContext;

	/**
	* Constructs a PropertiesAction with the given dialogs.
	*
	* @param _tuneDialog the tune properties dialog
	* @param _playlistDialog the playlist properties dialog
	* @param _mixedDialog the playlist + tune combo properties dialog
	* @param _tracker the selection tracker
	*/
	public PropertiesAction(ApplicationContext _context) {
		super("Properties");
		myContext = _context;
	}

	public void performAction() {
		boolean playlistsSelected = false;
		boolean tunesSelected = false;

		ContainerSelection selection = myContext.getSelection();
		if (selection != null) {
			int size = selection.getSize();
			Vector nodeVec = new Vector();
			for (int i = 0; i < size; i++) {
				Object obj = selection.getValueAt(i);
				if (obj instanceof IFIDNode) {
					if (obj instanceof FIDPlaylist) {
						playlistsSelected = true;
					}
					else {
						tunesSelected = true;
					}
					IFIDNode node = (IFIDNode) obj;
					nodeVec.addElement(node);
				}
			}

			IFIDNode[] selectedNodes = new IFIDNode[nodeVec.size()];
			nodeVec.copyInto(selectedNodes);

			if (selectedNodes.length > 0) {
				AbstractPropertiesDialog dlg = null;
				if (playlistsSelected) {
					if (tunesSelected) {
						dlg = createMixedDialog();
					}
					else {
						dlg = createPlaylistDialog();
					}
				}
				else if (tunesSelected) {
					dlg = createTuneDialog();
				}
				if (dlg != null) {
					show(dlg, selectedNodes);
					dlg.dispose();
				}
			}
		}
	}

	protected AbstractPropertiesDialog createTuneDialog() {
		TunePropertiesDialog tuneDialog = new TunePropertiesDialog(myContext.getFrame());
		return tuneDialog;
	}

	protected AbstractPropertiesDialog createPlaylistDialog() {
		PlaylistPropertiesDialog playlistDialog = new PlaylistPropertiesDialog(myContext.getFrame());
		return playlistDialog;
	}

	protected AbstractPropertiesDialog createMixedDialog() {
		MixedPropertiesDialog mixedDialog = new MixedPropertiesDialog(myContext.getFrame());
		return mixedDialog;
	}

	protected void show(AbstractPropertiesDialog _dialog, IFIDNode[] _selectedNodes) {
		_dialog.setNodes(_selectedNodes);
		_dialog.setVisible(true);
	}

	public void actionPerformed(ActionEvent _event) {
		performAction();
	}

	public void mousePressed(MouseEvent _event) {
	}

	public void mouseReleased(MouseEvent _event) {
	}

	public void mouseEntered(MouseEvent _event) {
	}

	public void mouseExited(MouseEvent _event) {
	}

	public void mouseClicked(MouseEvent _event) {
		// Check to see if the table or tree has already consumed this event
		if (!_event.isConsumed() && _event.getClickCount() == 2 && ((_event.getModifiers() & InputEvent.BUTTON1_MASK) > 0)) {
			boolean doubleClickedPlaylists = false;
			ContainerSelection selection = myContext.getSelection();
			if (selection != null) {
				Object[] values = selection.getSelectedValues();
				for (int i = 0; !doubleClickedPlaylists && i < values.length; i++) {
					if (values[i] instanceof IContainer) {
						doubleClickedPlaylists = true;
					}
				}
				if (!doubleClickedPlaylists) {
					performAction();
				}
			}
		}
	}
}
