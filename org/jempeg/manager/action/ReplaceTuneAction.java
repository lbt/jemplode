/* ReplaceTuneAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import com.inzyme.container.ContainerSelection;
import com.inzyme.filesystem.LocalImportFile;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.FileChooserKeyListener;
import org.jempeg.manager.util.FileChooserUtils;
import org.jempeg.nodestore.FIDLocalFile;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeReplacedDatabaseChange;
import org.jempeg.nodestore.PlayerDatabase;

public class ReplaceTuneAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public ReplaceTuneAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	try {
	    ContainerSelection selection = myContext.getSelection();
	    if (selection.getSize() != 1)
		Debug.handleError
		    ("You selected more than one tune to replace.", true);
	    else {
		IFIDNode nodeToReplace = (IFIDNode) selection.getValueAt(0);
		JFileChooser chooser = new JFileChooser();
		chooser.addKeyListener(new FileChooserKeyListener(chooser));
		FileChooserUtils.setToLastDirectory(chooser);
		chooser.setFileSelectionMode(0);
		chooser.setMultiSelectionEnabled(true);
		int retVal = chooser.showOpenDialog(myContext.getFrame());
		if (retVal == 0) {
		    FileChooserUtils.saveLastDirectory(chooser);
		    File[] selectedFiles
			= FileChooserUtils.getSelectedFiles(chooser);
		    if (selectedFiles.length > 0) {
			LocalImportFile selectedFile
			    = new LocalImportFile(selectedFiles[0]);
			PlayerDatabase playerDatabase
			    = myContext.getPlayerDatabase();
			IFIDNode existingNode
			    = FIDLocalFile.getExistingInstance(playerDatabase,
							       selectedFile);
			if (existingNode == null) {
			    IFIDNode createdNode
				= FIDLocalFile.createInstance(playerDatabase,
							      selectedFile,
							      true, false);
			    Properties tagProperties
				= createdNode.getTags().toProperties();
			    Enumeration tagNamesEnum = tagProperties.keys();
			    while (tagNamesEnum.hasMoreElements()) {
				String tagName
				    = (String) tagNamesEnum.nextElement();
				if (!"fid".equals(tagName)
				    && !"ctime".equals(tagName)) {
				    String tagValue
					= (String) tagProperties.get(tagName);
				    nodeToReplace.getTags().setValue(tagName,
								     tagValue);
				}
			    }
			    playerDatabase.getSynchronizeQueue().enqueue
				(new NodeReplacedDatabaseChange(nodeToReplace,
								selectedFile));
			} else
			    Debug.handleError
				("You selected a tune that was already going to be transferred to your device.",
				 true);
		    }
		}
	    }
	} catch (IOException e) {
	    Debug.println(e);
	}
    }
}
