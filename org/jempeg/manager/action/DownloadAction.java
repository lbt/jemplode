/* DownloadAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.tree.TreePath;

import com.inzyme.container.ContainerSelection;
import com.inzyme.progress.SilentProgressListener;
import com.inzyme.properties.PropertiesManager;

import org.freehep.swing.JDirectoryChooser;
import org.jempeg.ApplicationContext;
import org.jempeg.manager.SynchronizeUI;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.ISynchronizeClient;

public class DownloadAction extends AbstractAction
{
    private PropertiesManager myPropertiesManager;
    private ApplicationContext myContext;
    private File myCurrentDirectory;
    
    public DownloadAction(ApplicationContext _context) {
	super("Download");
	myContext = _context;
	myPropertiesManager = PropertiesManager.getInstance();
	String currentDirectory
	    = myPropertiesManager
		  .getProperty("jempeg.currentDownloadDirectory");
	if (currentDirectory != null)
	    myCurrentDirectory = new File(currentDirectory);
    }
    
    public void performAction() {
	File downloadDir = null;
	String downloadDirStr
	    = myPropertiesManager.getProperty("jempeg.downloadDir");
	if (downloadDirStr != null && downloadDirStr.trim().length() > 0)
	    downloadDir = new File(downloadDirStr.trim());
	else {
	    String curDirStr = myCurrentDirectory.getAbsolutePath();
	    File curDir;
	    if (curDirStr == null)
		curDir = myCurrentDirectory;
	    else
		curDir = new File(curDirStr);
	    JDirectoryChooser dlg = new JDirectoryChooser(curDir);
	    dlg.setFileSelectionMode(1);
	    dlg.setMultiSelectionEnabled(false);
	    dlg.setDialogTitle("Download into what directory...");
	    int retVal = dlg.showDialog(myContext.getFrame());
	    if (retVal == JDirectoryChooser.APPROVE_OPTION) {
		File currentDirectory = dlg.getSelectedFile();
		myCurrentDirectory = currentDirectory;
		myPropertiesManager.setProperty
		    ("jempeg.currentDownloadDirectory",
		     myCurrentDirectory.getAbsolutePath());
		try {
		    myPropertiesManager.save();
		} catch (IOException ioexception) {
		    /* empty */
		}
		downloadDir = currentDirectory;
	    }
	}
	if (downloadDir != null) {
	    ContainerSelection selection = myContext.getSelection();
	    FIDPlaylist playlist = (FIDPlaylist) selection.getContainer();
	    IFIDNode[] nodes = new IFIDNode[selection.getSize()];
	    for (int i = 0; i < nodes.length; i++)
		nodes[i] = (IFIDNode) selection.getValueAt(i);
	    TreePath path = new TreePath(new Object[] { playlist });
	    ISynchronizeClient synchronizeClient
		= myContext.getSynchronizeClient();
	    new SynchronizeUI(myContext.getPlayerDatabase(), synchronizeClient,
			      myContext.getFrame()).downloadFilesInBackground
		(path, nodes, downloadDir,
		 shouldUseHijack(synchronizeClient.getProtocolClient
				     (new SilentProgressListener())
				     .getConnection()),
		 myContext.getDownloadFilesProgressListener());
	}
    }
    
    public void actionPerformed(ActionEvent _event) {
	performAction();
    }
    
    protected boolean shouldUseHijack(IConnection _conn) {
	return false;
    }
}
