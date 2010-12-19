/* FileChooserUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.util;
import java.awt.Container;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

import org.freehep.swing.JDirectoryChooser;

public class FileChooserUtils
{
    public static void saveLastDirectory(JFileChooser _jfc) {
	File currentDirectory = _jfc.getCurrentDirectory();
	saveLastDirectory(currentDirectory);
    }
    
    public static void saveLastDirectory(JDirectoryChooser _jdc) {
	File currentDirectory = _jdc.getSelectedFile();
	saveLastDirectory(currentDirectory);
    }
    
    public static void setToLastDirectory(JFileChooser _jfc) {
	File lastDirectory = getLastDirectory();
	_jfc.setCurrentDirectory(lastDirectory);
    }
    
    public static void setToLastDirectory(JDirectoryChooser _jdc) {
	File lastDirectory = getLastDirectory();
	_jdc.setCurrentDirectory(lastDirectory);
    }
    
    public static File getLastDirectory() {
	String lastDirectoryStr
	    = PropertiesManager.getInstance().getProperty("lastOpenDirectory");
	File lastDirectory = new File(lastDirectoryStr);
	if (!lastDirectory.exists())
	    lastDirectory = new File(PropertiesManager.getDefaults()
					 .getProperty("lastOpenDirectory"));
	return lastDirectory;
    }
    
    public static void saveLastDirectory(File _lastDirectory) {
	PropertiesManager.getInstance().setProperty("lastOpenDirectory",
						    _lastDirectory
							.getAbsolutePath());
	try {
	    PropertiesManager.getInstance().save();
	} catch (IOException e) {
	    Debug.println(e);
	}
    }
    
    private static JList findList(Container _container) {
	JList list = null;
	int count = _container.getComponentCount();
	for (int i = 0; list == null && i < count; i++) {
	    java.awt.Component comp = _container.getComponent(count);
	    if (comp instanceof JList)
		list = (JList) comp;
	    else if (comp instanceof Container)
		list = findList((Container) comp);
	}
	return list;
    }
    
    public static File[] getSelectedFiles(JFileChooser _chooser) {
	File[] selectedFiles = _chooser.getSelectedFiles();
	if (selectedFiles == null || selectedFiles.length == 0) {
	    selectedFiles = null;
	    try {
		if (UIManager.getLookAndFeel() instanceof MetalLookAndFeel) {
		    _chooser.getComponentCount();
		    JList list = findList(_chooser);
		    if (list != null) {
			Object[] selectedValues = list.getSelectedValues();
			if (selectedValues.length > 0
			    && selectedValues[0] instanceof File) {
			    selectedFiles = new File[selectedValues.length];
			    for (int i = 0; i < selectedValues.length; i++)
				selectedFiles[i] = (File) selectedValues[i];
			}
		    }
		}
	    } catch (Object object) {
		if (selectedFiles == null) {
		    File selectedFile = _chooser.getSelectedFile();
		    if (selectedFile == null)
			selectedFiles = new File[0];
		    else
			selectedFiles = new File[] { selectedFile };
		}
		throw object;
	    }
	    if (selectedFiles == null) {
		File selectedFile = _chooser.getSelectedFile();
		if (selectedFile == null)
		    selectedFiles = new File[0];
		else
		    selectedFiles = new File[] { selectedFile };
	    }
	}
	return selectedFiles;
    }
}
