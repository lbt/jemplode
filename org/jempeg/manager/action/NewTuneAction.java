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
import javax.swing.JFileChooser;

import org.freehep.swing.JDirectoryChooser;
import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.FileChooserKeyListener;
import org.jempeg.manager.ui.ContainerModifierUI;
import org.jempeg.manager.util.FileChooserUtils;
import org.jempeg.nodestore.model.ContainerModifierFactory;
import org.jempeg.nodestore.model.IContainerModifier;

import com.inzyme.container.IContainer;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.ImportFileFactory;
import com.inzyme.util.Debug;

/**
* Brings up a JFileChooser and adds the selected songs or directories to 
* the playlist.
*
* @author Mike Schrag
* @author Daniel M. Zimmerman
* @version $Revision: 1.5 $
**/
public class NewTuneAction extends AbstractAction {
	private ApplicationContext myContext;
	private int myFileSelectionMode;

	public NewTuneAction(ApplicationContext _context, int _fileSelectionMode) {
		myContext = _context;
		myFileSelectionMode = _fileSelectionMode;
	}

	public void actionPerformed(ActionEvent _event) {
		try {
			IImportFile[] importFiles = null;
			
			if (myFileSelectionMode == JFileChooser.DIRECTORIES_ONLY) {
				JDirectoryChooser chooser = new JDirectoryChooser();
				chooser.setFileSelectionMode(JDirectoryChooser.DIRECTORIES_ONLY);
				chooser.setMultiSelectionEnabled(false);
				FileChooserUtils.setToLastDirectory(chooser);

				int retVal = chooser.showDialog(myContext.getFrame());
				if (retVal == JFileChooser.APPROVE_OPTION) {
					FileChooserUtils.saveLastDirectory(chooser);

					File selectedFile = chooser.getSelectedFile();
					importFiles = new IImportFile[] { ImportFileFactory.createImportFile(selectedFile) };
				}
			}
			else {
				JFileChooser chooser = new JFileChooser();
				chooser.addKeyListener(new FileChooserKeyListener(chooser));
				FileChooserUtils.setToLastDirectory(chooser);

				chooser.setFileSelectionMode(myFileSelectionMode);
				chooser.setMultiSelectionEnabled(true);
				int retVal = chooser.showOpenDialog(myContext.getFrame());
				if (retVal == JFileChooser.APPROVE_OPTION) {
					FileChooserUtils.saveLastDirectory(chooser);

					File[] selectedFiles = FileChooserUtils.getSelectedFiles(chooser);
					importFiles = ImportFileFactory.createImportFiles(selectedFiles);
				}
			}
			
			if (importFiles != null) {
				IContainer container = myContext.getSelectedContainer();
				IContainerModifier nodeModifier = new ContainerModifierUI(myContext, ContainerModifierFactory.getInstance(container));
				nodeModifier.importFiles(importFiles, null, myContext.getImportFilesProgressListener(), true);
			}
		}
		catch (IOException e) {
			Debug.println(e);
		}
	}
}
