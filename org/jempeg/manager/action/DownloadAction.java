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
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.tree.TreePath;

import org.freehep.swing.JDirectoryChooser;
import org.jempeg.ApplicationContext;
import org.jempeg.JEmplodeProperties;
import org.jempeg.manager.SynchronizeUI;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.ISynchronizeClient;

import com.inzyme.container.ContainerSelection;
import com.inzyme.progress.SilentProgressListener;
import com.inzyme.properties.PropertiesManager;

/**
* PropertiesAction displays the properties dialog for the currently selected node
* when any number of actions occur.
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public class DownloadAction extends AbstractAction {
	private PropertiesManager myPropertiesManager;
	private ApplicationContext myContext;
	private File myCurrentDirectory;

	public DownloadAction(ApplicationContext _context) {
		super("Download");
		myContext = _context;
		myPropertiesManager = PropertiesManager.getInstance();
		String currentDirectory = myPropertiesManager.getProperty(JEmplodeProperties.CURRENT_DOWNLOAD_DIRECTORY_PROPERTY);
		if (currentDirectory != null) {
			myCurrentDirectory = new File(currentDirectory);
		}
	}

	public void performAction() {
		File downloadDir = null;

		String downloadDirStr = myPropertiesManager.getProperty(JEmplodeProperties.DOWNLOAD_DIRECTORY_PROPERTY);
		if (downloadDirStr != null && downloadDirStr.trim().length() > 0) {
			downloadDir = new File(downloadDirStr.trim());
		} else {
			File curDir;
			String curDirStr = myCurrentDirectory.getAbsolutePath();
			if (curDirStr == null) {
				curDir = myCurrentDirectory;
			} else {
				curDir = new File(curDirStr);
			}

			JDirectoryChooser dlg = new JDirectoryChooser(curDir);
			dlg.setFileSelectionMode(JDirectoryChooser.DIRECTORIES_ONLY);
			dlg.setMultiSelectionEnabled(false);
			dlg.setDialogTitle("Download into what directory...");
			int retVal = dlg.showDialog(myContext.getFrame());
			if (retVal == JDirectoryChooser.APPROVE_OPTION) {
				File currentDirectory = dlg.getSelectedFile();
				myCurrentDirectory = currentDirectory;
				myPropertiesManager.setProperty(JEmplodeProperties.CURRENT_DOWNLOAD_DIRECTORY_PROPERTY, myCurrentDirectory.getAbsolutePath());

				try {
					myPropertiesManager.save();
				} catch (IOException e) {
				}

				downloadDir = currentDirectory;
			}
		}

		if (downloadDir != null) {
			ContainerSelection selection = myContext.getSelection();
			FIDPlaylist playlist = (FIDPlaylist) selection.getContainer();
			IFIDNode[] nodes = new IFIDNode[selection.getSize()];
			for (int i = 0; i < nodes.length; i++) {
				nodes[i] = (IFIDNode) selection.getValueAt(i);
			}
			TreePath path = new TreePath(new Object[] { playlist });
			ISynchronizeClient synchronizeClient = myContext.getSynchronizeClient();
			new SynchronizeUI(myContext.getPlayerDatabase(), synchronizeClient, myContext.getFrame()).downloadFilesInBackground(path, nodes, downloadDir, shouldUseHijack(synchronizeClient.getProtocolClient(new SilentProgressListener()).getConnection()), myContext.getDownloadFilesProgressListener());
		}
	}

	public void actionPerformed(ActionEvent _event) {
		performAction();
	}

	protected boolean shouldUseHijack(IConnection _conn) {
		return false;
	}

}
