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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.FileChooserKeyListener;
import org.jempeg.manager.util.FileChooserUtils;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.exporter.IExporter;

import com.inzyme.util.Debug;

/**
* Brings up a JFileChooser and exports the player database
* as a file.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class ExportAction extends AbstractAction {
	private ApplicationContext myContext;
	private IExporter myExporter;

	public ExportAction(ApplicationContext _context, IExporter _exporter) {
		myContext = _context;
		myExporter = _exporter;
	}

	public void actionPerformed(ActionEvent _event) {
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.addKeyListener(new FileChooserKeyListener(chooser));
			FileChooserUtils.setToLastDirectory(chooser);
			int result = chooser.showSaveDialog(myContext.getFrame());
			if (result == JFileChooser.APPROVE_OPTION) {
				FileChooserUtils.saveLastDirectory(chooser);
				File selectedFile = chooser.getSelectedFile();
				if (selectedFile != null) {
					PlayerDatabase playerDatabase = myContext.getPlayerDatabase();
					OutputStream os = new FileOutputStream(selectedFile);
					myExporter.write(playerDatabase, os);
					os.close();
				}
			}
		}
		catch (IOException e) {
			Debug.handleError(myContext.getFrame(), e, true);
		}
	}
}
