/* ExportAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.event.FileChooserKeyListener;
import org.jempeg.manager.util.FileChooserUtils;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.exporter.IExporter;

public class ExportAction extends AbstractAction
{
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
	    if (result == 0) {
		FileChooserUtils.saveLastDirectory(chooser);
		File selectedFile = chooser.getSelectedFile();
		if (selectedFile != null) {
		    PlayerDatabase playerDatabase
			= myContext.getPlayerDatabase();
		    java.io.OutputStream os
			= new FileOutputStream(selectedFile);
		    myExporter.write(playerDatabase, os);
		    os.close();
		}
	    }
	} catch (IOException e) {
	    Debug.handleError(myContext.getFrame(), e, true);
	}
    }
}
