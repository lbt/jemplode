/* NewTuneAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import com.inzyme.container.IContainer;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.ImportFileFactory;
import com.inzyme.util.Debug;

import org.freehep.swing.JDirectoryChooser;
import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.FileChooserKeyListener;
import org.jempeg.manager.ui.ContainerModifierUI;
import org.jempeg.manager.util.FileChooserUtils;
import org.jempeg.nodestore.model.ContainerModifierFactory;

public class NewTuneAction extends AbstractAction
{
    private ApplicationContext myContext;
    private int myFileSelectionMode;
    
    public NewTuneAction(ApplicationContext _context, int _fileSelectionMode) {
	myContext = _context;
	myFileSelectionMode = _fileSelectionMode;
    }
    
    public void actionPerformed(ActionEvent _event) {
	try {
	    IImportFile[] importFiles = null;
	    if (myFileSelectionMode == 1) {
		JDirectoryChooser chooser = new JDirectoryChooser();
		chooser.setFileSelectionMode(1);
		chooser.setMultiSelectionEnabled(false);
		FileChooserUtils.setToLastDirectory(chooser);
		int retVal = chooser.showDialog(myContext.getFrame());
		if (retVal == 0) {
		    FileChooserUtils.saveLastDirectory(chooser);
		    java.io.File selectedFile = chooser.getSelectedFile();
		    importFiles = (new IImportFile[]
				   { ImportFileFactory
					 .createImportFile(selectedFile) });
		}
	    } else {
		JFileChooser chooser = new JFileChooser();
		chooser.addKeyListener(new FileChooserKeyListener(chooser));
		FileChooserUtils.setToLastDirectory(chooser);
		chooser.setFileSelectionMode(myFileSelectionMode);
		chooser.setMultiSelectionEnabled(true);
		int retVal = chooser.showOpenDialog(myContext.getFrame());
		if (retVal == 0) {
		    FileChooserUtils.saveLastDirectory(chooser);
		    java.io.File[] selectedFiles
			= FileChooserUtils.getSelectedFiles(chooser);
		    importFiles
			= ImportFileFactory.createImportFiles(selectedFiles);
		}
	    }
	    if (importFiles != null) {
		IContainer container = myContext.getSelectedContainer();
		org.jempeg.nodestore.model.IContainerModifier nodeModifier
		    = new ContainerModifierUI(myContext,
					      ContainerModifierFactory
						  .getInstance(container));
		nodeModifier.importFiles(importFiles, null,
					 myContext
					     .getImportFilesProgressListener(),
					 true);
	    }
	} catch (IOException e) {
	    Debug.println(e);
	}
    }
}
