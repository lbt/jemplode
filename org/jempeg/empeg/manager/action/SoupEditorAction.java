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
package org.jempeg.empeg.manager.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.dialog.SoupEditorDialog;
import org.jempeg.nodestore.soup.ISoupLayer;
import org.jempeg.nodestore.soup.SoupUtils;

import com.inzyme.util.Debug;

/**
* SoupEditorAction opens a SoupEditor whech triggered.
*
* @author Mike Schrag
* @version $Revision: 1.5 $
*/
public class SoupEditorAction implements ActionListener {
	private ApplicationContext myContext;

	public SoupEditorAction(ApplicationContext _context) {
		myContext = _context;
	}

	public void actionPerformed(ActionEvent _event) {
		editSoup();
	}

	public void editSoup() {
		editSoup(null);
	}

	public void editSoup(ISoupLayer _soupLayer) {
		try {
			SoupEditorDialog soupEditor = new SoupEditorDialog(myContext, myContext.getFrame(), _soupLayer);
			soupEditor.setVisible(true);
			if (soupEditor.shouldCreateSoupPlaylist()) {
				ISoupLayer[] soupLayers = soupEditor.getSoupLayers();
				if (soupLayers != null && soupLayers.length > 0) {
					String name = soupEditor.getPlaylistName();
					boolean isTransient = soupEditor.isTransient();
					if (isTransient) {
						SoupUtils.createTransientSoupPlaylist(myContext.getPlayerDatabase(), name, soupLayers, true, false, true, null);
					}
					else {
						SoupUtils.createPersistentSoupPlaylist(myContext.getPlayerDatabase(), name, soupLayers, true);
					}
				}
			}
			soupEditor.dispose();
		}
		catch (Throwable t) {
			Debug.handleError(myContext.getFrame(), t, true);
		}
	}
}
